# Product Intent Specification

## Step 01 — Product Intent Specification

### Problem Statement

O modelo atual de domínio distingue entre:
- **Operações diretas** (`sum`, `subtract`...) como métodos procedurais no `CalculatorService`
- **Árvore de expressões** (`/evaluate`) como estrutura de dados com enum `Operator` e lógica avaliada via `switch`

Esta dualidade cria:
- **Duplicação de lógica**: a mesma regra (ex: divisão por zero) existe nos métodos do service e no `evaluate()`
- **Modelo não-OO**: `BinaryOperation` é uma estrutura passiva de dados; a inteligência fica centralizada no `CalculatorService`
- **Dificuldade de extensão**: adicionar uma nova operação exige modificar `Operator`, `CalculatorService`, e múltiplos pontos de `switch`

### Target Users

- **Desenvolvedores mantenedores da aplicação** — que precisam adicionar/modificar operações matemáticas
- **Consumidores da API REST** — que esperam os mesmos contratos externos preservados

### Formalized Exploration Decisions

1. **Comportamento em objetos**: cada operação (`Sum`, `Subtract`, `Multiply`, `Divide`, `Power`, `Root`) será um tipo próprio implementando `Expression`, encapsulando sua própria lógica de avaliação
2. **Retorno avaliado**: `Expression.evaluate(CalculationContext)` retorna `Literal`; valor cru acessível via `.value()`
3. **DSL estático com nomes ricos**: `Expression.sum(firstAddend, secondAddend)`, `Expression.divide(dividend, divisor)` etc.
4. **Eliminação de `CalculatorService` e `BinaryOperation`**: substituídos pela hierarquia de tipos
5. **Preservação de contrato externo**: endpoints REST e payloads JSON não mudam; apenas o domínio por baixo evolui
6. **`Operator` enum removido do domínio**: cada operação é representada por seu tipo próprio

### Success Metrics

- Todos os testes existentes (unitários e de integração) continuam passando sem modificação nos cenários suportados
- Cobertura de teste não diminui — novos testes cobrem a nova hierarquia de tipos
- Adição de uma futura operação exige criar **apenas 1 novo tipo** + 1 método fábrica, sem alterar `switch` ou enum

### Out of Scope

- Mudança nos DTOs REST (`SumRequest`, `SumResponse`, `EvaluateRequest`... mantêm-se inalterados)
- Mudança no `CalculationContext` (escopo, validação, defaults)
- Mudança nos endpoints REST (adicionar ou remover rotas)
- Refatoração do `ApiErrorHandler`
- Migração de `OperatorDto` fora do contrato JSON `/evaluate` (deferred)

### Risks and Assumptions

| Risco / Assunção | Mitigação |
|---|---|
| Exceções de domínio (divisão por zero, overflow) ficam dispersas em múltiplos tipos | Testes unitários específicos para cada tipo de operação validam cenários de erro |
| `OperatorDto` ainda existe no DTO de `/evaluate`, criando débito técnico | Documentado como out-of-scope; pode ser removido em evolução futura da API |
| Avaliação recursiva com contexto herdado precisa ser reconstruída | Cada tipo binário gerencia a herança de contexto em seu próprio `evaluate()` |
| `Literal` retornando `this` é trivial — mas precisa ser testado | Criar teste unitário mínimo para `Literal.evaluate()` |

### Acceptance Criteria (Given/When/Then)

**AC-1 — Literal avalia para si mesmo**
```
Given um Literal com valor 10.0
When evaluate() é chamado
Then retorna o mesmo Literal com value = 10.0
```

**AC-2 — Objeto Sum realiza adição**
```
Given Expression.sum(Literal(10.0), Literal(5.0))
When evaluate() é chamado
Then retorna Literal com value = 15.0
```

**AC-3 — Objeto Divide realiza divisão e lança exceção em divisor zero**
```
Given Expression.divide(Literal(10.0), Literal(0.0))
When evaluate() é chamado
Then ArithmeticException é lançada com mensagem "Division by zero"
```

**AC-4 — Objetos Power e Root detectam overflow**
```
Given Expression.power(Literal(2.0), Literal(1024.0))
When evaluate() é chamado
Then NumericOverflowException é lançada
```

**AC-5 — Contexto é respeitado em operações que o usam**
```
Given CalculationContext com scale=2 e Expression.divide com dividend=1.0, divisor=3.0
When evaluate(context) é chamado
Then retorna Literal com value ≈ 0.33
```

**AC-6 — Endpoint /sum preserva contrato externo**
```
Given POST /calculator/sum com firstAddend=1.5 e secondAddend=2.5
When a requisição é processada
Then retorna 200 com SumResponse contendo result=4.0
```

**AC-7 — Endpoint /evaluate preserva contrato JSON**
```
Given POST /calculator/evaluate com JSON contendo type="operation", operator="MULTIPLY"
When a requisição é processada
Then retorna 200 com EvaluateResponse contendo o resultado correto
```

**AC-8 — CalculatorService é removido**
```
When o projeto é compilado
Then a classe CalculatorService não existe mais
```
