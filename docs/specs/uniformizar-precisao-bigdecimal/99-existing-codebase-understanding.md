# 99 — Existing Codebase Understanding: Uniformizar Precisão BigDecimal

## Repository Structure Overview

```
src/main/java/com/example/demo/
├── DemoApplication.java
├── controller/
│   ├── CalculatorController.java          ← 7 POST endpoints
│   ├── ApiErrorHandler.java               ← 5 exception handlers
│   ├── api/
│   │   ├── DivideRequest.java             ← @NotNull Double dividend, divisor
│   │   ├── DivideResponse.java            ← double result
│   │   └── ... (13 DTOs)
│   └── validation/
│       ├── ComposeExpressionValidator.java ← parse JSON → Expression
│       └── InvalidExpressionPayloadException.java
└── domain/
    ├── CalculatorService.java              ← 6 ops + evaluate()
    ├── NumericOverflowException.java
    └── expression/
        ├── Expression.java                 ← sealed interface
        ├── ExpressionLiteral.java          ← record(double value)
        ├── ExpressionNode.java             ← record(operation, left, right)
        └── ExpressionOperation.java        ← enum ADD/SUBTRACT/MULTIPLY/DIVIDE

src/test/java/com/example/demo/
├── controller/                             ← @SpringBootTest integration tests
│   ├── CalculatorDivideControllerTest.java (12 tests)
│   └── CalculatorComposeControllerTest.java (7 tests)
└── domain/                                 ← JUnit 5 unit tests (no Spring)
    ├── CalculatorServiceSubtractionTest.java (6 tests)
    └── CalculatorServiceDivideTest.java (6 tests, uses AssertJ)
```

## Architecture Summary

- **2 camadas**: Controller (REST) + Domain (lógica de negócio)
- **Injeção via construtor**: `CalculatorService` + `ComposeExpressionValidator`
- **Validação**: Jakarta `@Valid` nos endpoints regulares; validação manual no compose
- **Tratamento de erros**: `ApiErrorHandler` centralizado

## Conventions to Preserve

| Convenção | Detalhe |
|-----------|---------|
| DTOs são records | Request/Response como Java records |
| Campos `@NotNull` | `Double` boxed nos requests para permitir validação |
| Response com `double` primitivo | Exceto `PowerResponse` (boxed) — fora de escopo |
| Testes unitários no domínio | JUnit 5 puro, sem contexto Spring |
| Testes de integração no controller | `@SpringBootTest` + `MockMvcTester` + AssertJ |
| `ComposeExpressionValidator` rejeita campos extras | `ALLOWED_FIELDS` rigoroso |
| Compose usa `String` como body | Não usa `@Valid` nem DTO tipado |

## Integration Points

| Ponto | Arquivo | Impacto da refatoração |
|-------|---------|----------------------|
| `CalculatorService.divide()` | `:34-41` | Assinatura muda (scale, roundingMode) |
| `CalculatorService.subtract()` | `:23-25` | Implementação muda para BigDecimal |
| `CalculatorService.evaluate()` | `:69-84` | Chamada a `divide()` precisa passar scale/rounding |
| `CalculatorController.divide()` | `:55-59` | Deve ler scale/roundingMode do request |
| `DivideRequest` | `DivideRequest.java` | Adicionar `scale` e `roundingMode` opcionais |
| `ExpressionNode` | `ExpressionNode.java` | Adicionar `context: ExpressionContext` |
| `ComposeExpressionValidator` | `ComposeExpressionValidator.java:16,52` | `ALLOWED_FIELDS` + construção de `ExpressionNode` com context |
| `CalculatorServiceDivideTest` | Teste unitário | 6 testes precisam de ajuste (sem scale) |
| `CalculatorDivideControllerTest` | Teste integração | 12 testes — novos casos com scale/roundingMode |
| `CalculatorServiceSubtractionTest` | Teste unitário | 6 testes — adicionar teste de precisão (0.3-0.1) |
| `CalculatorComposeControllerTest` | Teste integração | 7 testes — adicionar casos com context |

## Risks and Unknowns

| Risco | Detalhe |
|-------|---------|
| `divide()` sem scale lança `ArithmeticException` hoje | Ex: `1÷3` → exception; após mudança → resultado arredondado. Testes que esperam exception para decimais não-terminantes precisam ser reescritos |
| `ALLOWED_FIELDS` no compose é rigoroso | Adicionar `context` ao set; validar estrutura interna de context |
| `evaluate()` chama `divide(left, right)` sem parâmetros | Precisa passar `node.context().scale()` e `node.context().roundingMode()` |
| Nenhum teste unitário para `evaluate()` | Precisa ser criado na Step 04 |

## Context to Carry Into Steps 02-06

1. **`subtract()`** hoje faz `minuend - subtrahend` (double puro) — migração é direta, segue padrão de `sum()`
2. **`divide()`** usa `BigDecimal.divide()` sem scale — adicionar overload com scale/rounding
3. **`ExpressionContext`** é um novo record — precisa de factory `DEFAULT` e validação de `roundingMode` no compose
4. **`ExpressionNode`** passa de 3 para 4 componentes — `context` com default
5. **`ComposeExpressionValidator`** precisa de parsing recursivo de `context` + validação de `roundingMode` válido
6. **`DivideRequest`** adiciona `Integer scale` e `String roundingMode` opcionais — backward-compatible
7. **Testes existentes de divide** (unitários e integração) não cobrem decimais não-terminantes — precisam de novos testes
8. **Sem teste de `evaluate()` no domínio** — criar na Step 04
