# 02 — High-Level Technical Design

## Architecture Diagram

```
Cliente HTTP -> CalculatorController (/calculator/divide) -> CalculatorService.divide() -> ResponseEntity<DivideResponse>
                                              |
                                              v
                                    ArithmeticException (divisao por zero)
                                              |
                                              v
                                    ApiErrorHandler -> 400 ErrorResponse
```

## Component Responsibilities

- `CalculatorController`: Recebe request POST, valida input, delega ao service, retorna ResponseEntity
- `CalculatorService`: Implementa logica de divisao com tratamento de divisoes por zero
- `DivideRequest`/`DivideResponse`: DTOs para request/response
- `ApiErrorHandler`: Trata ArithmeticException para retorno 400

## Data Flow

1. Request JSON -> `DivideRequest` (record)
2. Controller valida com `@Valid`
3. Service.divide(dividend, divisor)
4. Se divisor == 0, lanca ArithmeticException
5. ApiErrorHandler captura ArithmeticException -> 400 ErrorResponse
6. Se sucesso, Service retorna double -> envolta em `DivideResponse`
7. ResponseEntity.ok() com 200

## Security and Observability Requirements

- Ja coberto por infraestrutura existente (Spring Boot)
- Logs via existing SLF4J configuration

## Trade-Offs and Alternatives

- Usar BigDecimal para precisao (como sum/multiply) vs double direto
- Decisao: seguir mesmo pattern do sum/multiply para consistencia
- Tratar divisao por zero via exception (ArithmeticException) vs validacao previa
- Decisao: lancar ArithmeticException para tratamento centralizado no ApiErrorHandler

## High-Level Test Scenario Map

- Sucesso: payload valido -> 200 + resultado
- Divisao por zero: divisor=0 -> 400
- Validacao: campo ausente -> 400
- Parsing: JSON malformado -> 400
- Edge cases: NaN, Infinity -> 400