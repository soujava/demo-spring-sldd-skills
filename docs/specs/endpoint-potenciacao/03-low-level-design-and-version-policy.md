# 03 — Low-Level Design and Version Policy: Endpoint Potenciação

## API Contracts

### POST `/calculator/power`
- **Request Body (`PowerRequest`):**
  ```json
  {
    "base": 2.0,
    "exponent": 3.0
  }
  ```
- **Response Body (`PowerResponse`):**
  ```json
  {
    "result": 8.0
  }
  ```
- **Error Response (400/422):**
  ```json
  {
    "error": "Bad Request / Unprocessable Entity",
    "message": "Detailed error message"
  }
  ```

## Data Models
- `public record PowerRequest(@NotNull Double base, @NotNull Double exponent) {}`
- `public record PowerResponse(Double result) {}`

## Error Model
- **NumericOverflowException (Runtime):** Lançada quando o resultado é `Infinite`. Mapeada para 422.
- **ArithmeticException (Standard):** Lançada para resultados `NaN`. Mapeada para 400.

## Test Strategy
- **TDD (Red-Green-Refactor):** Escrita de testes unitários e de integração que falham antes da implementação.
- **Unit Tests:** `CalculatorService` (casos de borda: 0^0, base negativa, overflow).
- **Integration Tests:** `CalculatorController` (fluxo HTTP completo e tratamento de erros).

## Test Scenario Catalog

| ID | Scenario | Input | Expected Output | Status |
|----|----------|-------|-----------------|--------|
| TS01 | Sucesso simples | `2^3` | `200 OK, 8.0` | 200 |
| TS02 | Base zero | `0^5` | `200 OK, 0.0` | 200 |
| TS03 | Expoente zero | `5^0` | `200 OK, 1.0` | 200 |
| TS04 | Overflow | `10^1000` | `422 Unprocessable Entity` | 422 |
| TS05 | Resultado Imaginário | `-4^0.5` | `400 Bad Request` | 400 |
| TS06 | Campos Nulos | `{}` | `400 Bad Request` | 400 |

## Dependency and Version Policy
- Java 21+ (conforme `pom.xml`).
- Spring Boot 4.0.5.
- Sem novas dependências externas.

## Ordered Implementation Plan
1. **Passo 04 (Tests First):**
   - Criar `NumericOverflowException`.
   - Criar `CalculatorServicePowerTest` com cenários de sucesso e falha.
   - Criar `CalculatorPowerControllerTest` (integração).
   - Validar falha dos testes (Fase Red).
2. **Passo 05 (Implementation):**
   - Criar `PowerRequest` e `PowerResponse` records.
   - Atualizar `ApiErrorHandler` para tratar `NumericOverflowException`.
   - Implementar `CalculatorService.power`.
   - Adicionar o endpoint no `CalculatorController`.
3. **Passo 06 (Verification):**
   - Executar todos os testes e validar conformidade.
