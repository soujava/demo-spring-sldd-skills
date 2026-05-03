# Existing Codebase Understanding and Context Summary

## Step 99 — Existing Codebase Understanding and Context Summary

### Repository Structure Overview

```
demo-spring-sldd-skills/
├── docs/specs/expression-refactor/   # Artefatos SLDD
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java
│   ├── controller/
│   │   ├── CalculatorController.java          # Todos os endpoints REST
│   │   ├── ApiErrorHandler.java               # Tratamento global de exceções
│   │   └── api/                               # DTOs REST
│   │       ├── ExpressionDto, LiteralDto, BinaryOperationDto, OperatorDto
│   │       ├── EvaluateRequest, EvaluateResponse
│   │       ├── SumRequest, SumResponse, SubtractRequest, SubtractResponse, etc.
│   │       └── CalculationContextDto
│   └── domain/
│       ├── CalculatorService.java             # Lógica procedural centralizada
│       ├── CalculationContext.java            # scale + roundingMode
│       ├── Expression.java                    # sealed interface
│       ├── Literal.java                       # record
│       ├── BinaryOperation.java               # record (Operator, left, right)
│       ├── Operator.java                      # enum (SUM...ROOT)
│       └── NumericOverflowException.java
└── src/test/java/com/example/demo/
    ├── controller/                              # Testes de integração Spring + MockMvcTester
    │   ├── CalculatorControllerTest.java        # POST /calculator/sum
    │   ├── CalculatorEvaluateControllerTest.java
    │   ├── CalculatorSubtractControllerTest.java
    │   ├── CalculatorMultiplyControllerTest.java
    │   ├── CalculatorDivideControllerTest.java
    │   ├── CalculatorPowerControllerTest.java
    │   ├── CalculatorRootControllerTest.java
    │   └── OpenApiDocumentationTest.java
    └── domain/                                # Testes unitários JUnit puro
        ├── CalculatorServiceTest.java
        ├── CalculatorServiceSubtractionTest.java
        ├── CalculatorServiceMultiplyTest.java
        ├── CalculatorServiceDivideTest.java
        ├── CalculatorServicePowerTest.java
        ├── CalculatorServiceRootTest.java
        ├── CalculatorServiceEvaluateTest.java
        └── CalculationContextTest.java
```

### Architecture Summary

**Camada Borda (Controller + DTOs):**
- `CalculatorController` expõe endpoints REST para operações diretas (`/sum`, `/subtract`, `/multiply`, `/divide`, `/power`, `/root`) e para avaliação de expression tree (`/evaluate`).
- DTOs REST usam `record` com validação Jakarta (`@NotNull`).
- `ExpressionDto` usa `@JsonTypeInfo`/`@JsonSubTypes` para deserialização polimórfica.
- `ApiErrorHandler` centraliza mapeamento de `MethodArgumentNotValidException`, `HttpMessageNotReadableException`, `ArithmeticException`, `NumericOverflowException`.

**Camada Domínio (Service + Modelos):**
- `CalculatorService` centraliza toda a lógica matemática em métodos imperativos (`sum()`, `subtract()`...).
- `evaluate(Expression, CalculationContext)` faz pattern matching recursivo com switch por `Operator` enum.
- `CalculationContext` encapsula `scale` e `roundingMode`; pode ser herdado de pai para filho na avaliação de `/evaluate`.
- `BinaryOperation` é record genérico carregando `Operator` + `left` + `right`.

**Stack:** Spring Boot 4.0.5, Java 25, Maven.

### Conventions to Preserve

| Convenção | Onde vive | Por que preservar |
|---|---|---|
| **Testes unitários sem Spring** | `/domain/` | Lógica de negócio pura, instanciação direta |
| **Testes de integração com Spring** | `/controller/` | `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` |
| **Contrato JSON externo** | DTOs + controllers | Consumidores não devem quebrar |
| **Arquivos de request/response separados** | `/controller/api/` | Cada endpoint tem seu próprio DTO |
| **Tratamento de exceção por `ApiErrorHandler`** | Controller advice | Centralizado e padronizado |
| **BigDecimal para operações decimal críticas** | `sum()`, `multiply()`, `divide()` | Evita floating-point precision issues |
| **Conventional Commits** | `AGENTS.md` | Padrão de commit do projeto |

### Integration Points

- **HTTP API REST**: endpoints JSON são o único ponto de integração externo.
- **Jackson**: usado para deserialização polimórfica de `ExpressionDto`.
- **Jakarta Validation**: validação de campos nos DTOs de request.
- **Spring Boot Test / MockMvcTester**: infraestrutura de testes de borda.

### Risks and Unknowns

| Risco | Impacto | Notas |
|---|---|---|
| `CalculatorService` tem métodos usados diretamente por ~6 controllers | Alto | Remover service exige que todos os controllers construam `Expression` internamente |
| `BinaryOperationDto` ainda carrega `OperatorDto` | Médio | O mapeamento DTO→domínio no `/evaluate` precisa traduzir enum para tipo OO |
| Profundidade de expressão removida | Baixo | Sem limite de `depth > 10`, árvores arbitrariamente grandes são aceitas |
| Tests existentes dependem de nome de classe e estrutura | Médio | Refatoração pode quebrar imports e assertions que referenciam `BinaryOperation` |
| `CalculationContext` herança na avaliação recursiva | Médio | `evaluate()` em operação binária precisa replicar lógica de `resolveContext()` que hoje vive no controller |

### Context to Carry Into Steps 02-06

1. **Domínio atual é procedural** — refatoração para OO é principal foco do design
2. **API REST não muda** — contratos externos são estabilizadores; mudanças ficam estritamente no domínio
3. **`Operator` enum será removido do domínio** — mas `OperatorDto` ainda existe no JSON de `/evaluate` (debito técnico)
4. **`BinaryOperation` desaparece como tipo** — cada operação vira tipo próprio
5. **Controllers precisam de DTOs separados** — manter padrão; eles compõem respostas via `.value()`
6. **Testes unitários existentes validam `CalculatorService`** — serão adaptados/removidos conforme o service desaparece
7. **Convenção de commits (`AGENTS.md`)** — todos os commits devem seguir Conventional Commits
