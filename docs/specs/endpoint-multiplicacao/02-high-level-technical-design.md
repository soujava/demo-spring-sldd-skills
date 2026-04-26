# 02 — High-Level Technical Design

## Architecture Diagram

```
Cliente HTTP -> CalculatorController (/calculator/multiply) -> CalculatorService.multiply() -> ResponseEntity<MultiplyResponse>
```

## Component Responsibilities

- `CalculatorController`: Recebe request POST, valida input, delega ao service, retorna ResponseEntity
- `CalculatorService`: Implementa logica de multiplicacao
- `MultiplyRequest`/`MultiplyResponse`: DTOs para request/response

## Data Flow

1. Request JSON -> `MultiplyRequest` (record)
2. Controller valida com `@Valid`
3. Service.multiply(multiplicand, multiplier)
4. Service retorna double -> envolta em `MultiplyResponse`
5. ResponseEntity.ok() com 200

## Security and Observability Requirements

- Ja coberto por infraestrutura existente (Spring Boot)
- Logs via existing SLF4J configuration

## Trade-Offs and Alternatives

- Usar BigDecimal para precisao (como sum) vs double direto
- Decisao: seguir mesmo pattern do sum para consistencia

## High-Level Test Scenario Map

- Sucesso: payload valido -> 200 + resultado
- Validacao: campo ausente -> 400
- Parsing: JSON malformado -> 400
- Edge cases: NaN, Infinity -> 400