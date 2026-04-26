# 03 — Low-Level Design and Version Policy: Endpoint Raiz

## API Contracts

### POST `/calculator/root`
- **Request Body (`RootRequest`):**
  ```json
  {
    "radicand": 9.0,
    "index": 2.0
  }
  ```
- **Response Body (`RootResponse`):**
  ```json
  {
    "result": 3.0
  }
  ```
- **Error Response (400/422):**
  ```json
  {
    "error": "Bad Request",
    "message": "Invalid request body"
  }
  ```

## Data Models
- `public record RootRequest(@NotNull Double radicand, @NotNull Double index) {}`
- `public record RootResponse(Double result) {}`

## Error Model
- `MethodArgumentNotValidException`: campos obrigatorios ausentes, mapeado para 400 pelo `ApiErrorHandler`.
- `HttpMessageNotReadableException`: JSON invalido ou tipo incompativel, mapeado para 400 pelo `ApiErrorHandler`.
- `ArithmeticException`: indice zero ou resultado `NaN`, mapeado para 400 pelo `ApiErrorHandler`.
- `NumericOverflowException`: resultado `Infinity`, mapeado para 422 pelo `ApiErrorHandler`.

## Test Strategy
- **Step 04:** escrever testes antes da implementacao e confirmar falha.
- **Unit tests:** testar `CalculatorService.root` diretamente, sem Spring.
- **Integration tests:** testar `POST /calculator/root` com contexto Spring completo e `MockMvcTester`.
- **Separacao de camadas:** unitarios validam calculo e excecoes; integracao valida status HTTP e formato JSON.

## Test Scenario Catalog

| ID | Scenario | Input | Expected Output | Status |
|----|----------|-------|-----------------|--------|
| TS01 | Raiz quadrada valida | `radicand=9.0,index=2.0` | `3.0` | 200 |
| TS02 | Raiz cubica valida | `radicand=27.0,index=3.0` | `3.0` | 200 |
| TS03 | Raiz de zero | `radicand=0.0,index=2.0` | `0.0` | 200 |
| TS04 | Indice zero | `radicand=9.0,index=0.0` | `Bad Request` | 400 |
| TS05 | Resultado indefinido/imaginario | `radicand=-4.0,index=2.0` | `Bad Request` | 400 |
| TS06 | Campo `radicand` ausente | `{"index":2.0}` | `Invalid request body` | 400 |
| TS07 | Campo `index` ausente | `{"radicand":9.0}` | `Invalid request body` | 400 |
| TS08 | JSON malformado | JSON invalido | `Invalid request body` | 400 |
| TS09 | Tipo incompativel | `{"radicand":"abc","index":2.0}` | `Invalid request body` | 400 |

## Dependency and Version Policy
- Manter Spring Boot 4.0.5.
- Manter Java 25 conforme projeto atual.
- Nao adicionar dependencias Maven.
- Nao alterar versoes existentes no `pom.xml`.

## Ordered Implementation Plan
1. **Step 04 — Tests First:**
   - Criar `CalculatorServiceRootTest` com cenarios TS01 a TS05.
   - Criar `CalculatorRootControllerTest` com cenarios HTTP TS01, TS02, TS04, TS06, TS07, TS08 e TS09.
   - Executar testes e registrar falha por ausencia de `root`, `RootRequest`, `RootResponse` e endpoint.
2. **Step 05 — Minimal Implementation:**
   - Criar `RootRequest`.
   - Criar `RootResponse`.
   - Adicionar `CalculatorService.root(double radicand, double index)`.
   - Adicionar `POST /calculator/root` em `CalculatorController`.
   - Reutilizar `ApiErrorHandler` e `NumericOverflowException` existentes.
3. **Step 06 — Verification:**
   - Executar `./mvnw test`.
   - Verificar aderencia as convencoes de teste.
   - Produzir decisao Go/No-Go.
