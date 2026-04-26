# 06 — Verification and Feedback Report: Endpoint Raiz

## Compliance Matrix
| Requirement | Status | Evidence |
|-------------|--------|----------|
| `POST /calculator/root` exists | Pass | `CalculatorController.root` |
| Request uses `radicand` and `index` | Pass | `RootRequest` |
| Response returns `result` | Pass | `RootResponse` |
| Valid square root returns 200 | Pass | `CalculatorRootControllerTest.shouldReturnSquareRootForValidPayload` |
| Valid cube root returns 200 | Pass | `CalculatorRootControllerTest.shouldReturnCubeRootForValidPayload` |
| Missing fields return 400 | Pass | controller missing-field tests |
| Malformed JSON/type mismatch returns 400 | Pass | controller parsing tests |
| Index zero returns 400 | Pass | service and controller tests |
| Invalid/imaginary result returns error | Pass | service test |
| Unit/integration test separation preserved | Pass | domain tests use direct instantiation; controller tests use Spring + MockMvcTester |

## Version and Dependency Validation
- Spring Boot remains `4.0.5`.
- Java remains `25`.
- No Maven dependency or version changes were made.
- `ApiErrorHandler` and `NumericOverflowException` were reused.

## Test Convention Compliance
- Unit tests are in `src/test/java/com/example/demo/domain/`.
- Unit tests instantiate `CalculatorService` directly.
- Integration tests are in `src/test/java/com/example/demo/controller/`.
- Integration tests use `@SpringBootTest`, `@AutoConfigureMockMvc`, and `MockMvcTester`.
- Integration tests validate HTTP status and JSON shape, not service internals.

## Risks by Severity
- Low: raiz impar de radicando negativo nao tem tratamento manual; `Math.pow(-27.0, 1.0 / 3.0)` pode produzir `NaN`, mantendo a operacao fora do escopo atual.
- Low: Maven/JDK emite warnings sobre native access e Mockito self-attach; nao afetam a feature, mas podem exigir ajuste futuro de build com versoes futuras do Java.

## Remediation Steps
- Se for necessario suportar raiz impar de negativos, adicionar criterio especifico e implementar normalizacao manual antes de `Math.pow`.
- Avaliar configuracao futura do Mockito como Java agent se os warnings passarem a falhar em versoes futuras do JDK.

## Go/No-Go Decision and Rationale
Go.

A feature atende aos criterios aprovados, preserva as convencoes de arquitetura e teste, nao altera dependencias e passa a suite completa.

Verification command:
```bash
./mvnw test
```

Result:
```text
Tests run: 101, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
