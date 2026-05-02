# Step 01 — Product Intent Specification: Calculator Operations Refactor

## Problem Statement

O `CalculatorService` atual implementa 6 operações aritméticas como métodos avulsos
com tratamento inconsistente de precisão numérica e dispatch centralizado por `switch`.
Isso torna a adição de novas operações custosa (violação do Open/Closed Principle),
cria assimetrias (ex: `subtract` usa `double` enquanto as demais usam `BigDecimal`),
e a divisão não-exata (ex: `1/3`) lança `ArithmeticException` sem possibilidade de
arredondamento controlado.

## Target Users

- Desenvolvedores que consomem a API REST do calculator
- Desenvolvedores que precisam adicionar novas operações aritméticas no futuro

## Formalized Exploration Decisions

1. As operações serão extraídas para uma interface `Operation` (Strategy Pattern)
   com método `double apply(double left, double right)`
2. `sum`, `multiply`, `subtract` usarão `BigDecimal` puro (sem escala/rounding)
3. `divide` usará `BigDecimal` com suporte opcional a `scale` e `RoundingMode`
   no request; default `scale=10, RoundingMode.HALF_UP`
4. `power` e `root` manterão `Math.pow()` (IEEE 754)
5. `BinaryOperation` para `Operator.DIVIDE` carregará um `CalculationContext`
   opcional com `scale` e `RoundingMode`
6. `CalculationContext` não é global — é específico do nó `DIVIDE`

## Success Metrics

- Todas as operações existentes continuam produzindo os mesmos resultados para
  os mesmos inputs (regressão zero)
- `subtract` passa a ser consistente com `sum`/`multiply` em precisão
- `divide(1, 3)` retorna `0.3333333333` em vez de lançar exceção
  (com default scale/rounding)
- Número de classes modificadas para adicionar uma nova operação é **1**
  (nova classe `Operation` + 1 entrada no enum + 1 endpoint)
  em vez de editar `CalculatorService`

## Out of Scope

- Não serão adicionadas novas operações além das 6 existentes
- Não será alterado o formato de resposta HTTP (status codes, estrutura JSON)
- `power`/`root` não receberão suporte a `BigDecimal` ou `CalculationContext`
- Não será implementada precisão arbitrária para `power`/`root`
- Não será alterado o mecanismo de autenticação, documentação OpenAPI,
  ou configuração de deploy

## Risks and Assumptions

- **Risco**: a refatoração pode introduzir regressão se o mapeamento de
  `DivideRequest` para `BinaryOperation(DIVIDE, ctx)` não for feito corretamente
- **Risco**: `CalculationContext` no nó `BinaryOperation` polui o modelo de domínio
  com conceito de borda — mitigável mantendo o campo opcional
- **Assunção**: os testes existentes são suficientes para garantir regressão zero
  nas operações não modificadas semanticamente
- **Assunção**: o consumidor da API prefere `1/3 = 0.3333333333` a um erro 400

## Acceptance Criteria

1. **Given** uma requisição `POST /calculator/subtract` com
   `{ "minuend": 10, "subtrahend": 3.3 }`,
   **when** processada,
   **then** retorna `200` com `{ "result": 6.7 }`
   (consistente com `sum`: `6.7 + 3.3 = 10.0`)

2. **Given** uma requisição `POST /calculator/divide` com
   `{ "dividend": 1, "divisor": 3 }`,
   **when** processada,
   **then** retorna `200` com `{ "result": 0.3333333333 }`
   (10 casas, HALF_UP)

3. **Given** uma requisição `POST /calculator/divide` com
   `{ "dividend": 1, "divisor": 3, "scale": 4, "roundingMode": "HALF_DOWN" }`,
   **when** processada,
   **then** retorna `200` com `{ "result": 0.3333 }`

4. **Given** uma requisição `POST /calculator/divide` com
   `{ "dividend": 1, "divisor": 0 }`,
   **when** processada,
   **then** retorna `400` com `ArithmeticException`

5. **Given** uma requisição `POST /calculator/evaluate` com uma expressão
   contendo `DIVIDE` sem `scale`/`roundingMode`,
   **when** processada,
   **then** usa o default (`scale=10, HALF_UP`)

6. **Given** as operações `sum`, `multiply`, `power`, `root` com mesmos inputs
   de antes da refatoração,
   **when** processadas,
   **then** retornam os mesmos resultados (regressão zero)

7. **Given** a necessidade de adicionar uma nova operação `MODULO`,
   **when** implementada via nova classe `ModuloOperation implements Operation`,
   **then** `CalculatorService` não é modificado
