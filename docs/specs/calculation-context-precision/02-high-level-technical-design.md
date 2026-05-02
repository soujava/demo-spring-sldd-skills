# 02 — High-Level Technical Design

## Requirements Traceability

- Contexto de calculo no dominio:
  - Atendido por `CalculationContext` em `com.example.demo.domain`.
- Defaults `scale = 10` e `roundingMode = HALF_UP`:
  - Atendido por factory/default no dominio.
- `scale` valido entre `1..16`:
  - Atendido por validacao nos DTOs de entrada e preservado na conversao para dominio.
- `roundingMode` como enum:
  - Atendido por DTO usando `java.math.RoundingMode`.
- Endpoints com `context`:
  - `/calculator/divide`
  - `/calculator/power`
  - `/calculator/root`
  - `/calculator/evaluate`
- Operacoes consumidoras:
  - `DIVIDE`: usa contexto na divisao.
  - `POWER` e `ROOT`: usam contexto para arredondar resultado final.
- `/evaluate` com contexto raiz e local:
  - Atendido por `EvaluateRequest.context` e `BinaryOperationDto.context`.
- Heranca campo a campo:
  - Atendida por resolucao explicita entre contexto local, contexto raiz e default.
- Erros 400 com mensagem clara:
  - Atendido por validacao e tratamento no `ApiErrorHandler`.

## Architecture Diagram

```text
Cliente HTTP
  |
  v
CalculatorController
  |
  |-- /divide -> DivideRequest(context?) ----\
  |-- /power  -> PowerRequest(context?) -----+--> CalculationContextDto
  |-- /root   -> RootRequest(context?) ------/
  |-- /evaluate -> EvaluateRequest(context?, expression)
                      |
                      v
              BinaryOperationDto(context?)
  |
  v
Context Mapper / Resolver
  |
  v
CalculationContext (domain)
  |
  v
CalculatorService
  |
  |-- divide(..., context)
  |-- power(..., context)
  |-- root(..., context)
  |-- evaluate(..., context)
  |
  v
Response DTO ou ApiErrorHandler -> ErrorResponse
```

## Component Responsibilities

- `CalculationContext`
  - Record de dominio.
  - Define `scale`, `roundingMode` e defaults.
  - Representa a politica efetiva usada pelas operacoes.

- `CalculationContextDto`
  - DTO opcional nos payloads permitidos.
  - Usa `Integer scale` com validacao `1..16`.
  - Usa `RoundingMode roundingMode` para OpenAPI inferir enum.

- `CalculatorController`
  - Recebe requests HTTP.
  - Valida DTOs com Bean Validation.
  - Converte `CalculationContextDto` para `CalculationContext`.
  - Resolve contexto raiz e local em `/evaluate`.
  - Delega calculo para `CalculatorService`.

- `CalculatorService`
  - Mantem regras matematicas.
  - Adiciona overloads ou assinaturas com `CalculationContext` para `divide`, `power`, `root` e `evaluate`.
  - Preserva compatibilidade para chamadas sem contexto usando `CalculationContext.defaults()`.

- `ApiErrorHandler`
  - Continua centralizando erros HTTP.
  - Deve retornar mensagens claras para contexto invalido.
  - Mantem `ArithmeticException` como `400`.
  - Mantem `NumericOverflowException` como `422`.

- DTOs existentes
  - `DivideRequest`, `PowerRequest`, `RootRequest` recebem `context` opcional.
  - `EvaluateRequest` recebe contexto raiz opcional.
  - `BinaryOperationDto` recebe contexto local opcional.

## Data Flow

### `/calculator/divide`, `/power`, `/root`

1. Cliente envia payload com ou sem `context`.
2. Spring desserializa request.
3. Bean Validation valida `scale` quando informado.
4. Controller resolve:
   - contexto informado, preenchendo campos ausentes com defaults;
   - ou `CalculationContext.defaults()` se `context` estiver ausente.
5. Controller chama operacao do `CalculatorService` com contexto resolvido.
6. Service calcula e retorna `double`.
7. Controller retorna response DTO.
8. Erros seguem `ApiErrorHandler`.

### `/calculator/evaluate`

1. Cliente envia `EvaluateRequest` com `context` raiz opcional.
2. Expressao pode conter `BinaryOperationDto.context` local opcional.
3. Controller resolve contexto efetivo durante mapeamento/avaliacao:
   - campo local informado vence;
   - campo ausente herda do contexto raiz;
   - campo ausente na raiz usa default.
4. Operacoes `DIVIDE`, `POWER` e `ROOT` usam contexto efetivo.
5. Operacoes `SUM`, `SUBTRACT` e `MULTIPLY` ignoram contexto.
6. Resultado final retorna em `EvaluateResponse`.

## Security and Observability Requirements

- Nao ha nova autenticacao/autorizacao neste workflow.
- Payload invalido deve continuar retornando erro padronizado.
- Mensagens de erro devem ser claras, mas sem expor stack trace.
- Nao ha necessidade de logs adicionais obrigatorios.
- OpenAPI deve documentar `roundingMode` como enum nos schemas gerados.

## Trade-Offs and Alternatives

- `CalculationContextDto.roundingMode` como enum vs string:
  - Decisao: enum, para melhorar inferencia OpenAPI e validacao de contrato.
- Contexto global unico vs contexto por request:
  - Decisao: contexto por payload, com defaults no dominio.
- Contexto unico em `/evaluate` vs contexto local por operacao:
  - Decisao: ambos, com heranca por campo pelo contexto mais proximo.
- Aplicar contexto a todas as operacoes vs apenas operacoes interessadas:
  - Decisao: apenas `DIVIDE`, `POWER` e `ROOT`.
- Substituir `Math.pow` em `POWER` e `ROOT`:
  - Fora de escopo; contexto arredonda o resultado final.

## High-Level Test Scenario Map

- Dominio:
  - `CalculationContext.defaults()` retorna scale 10 e HALF_UP.
  - `divide(1, 3, context scale 4 HALF_UP)` retorna valor arredondado.
  - `divide` mantem erro de divisao por zero.
  - `power` arredonda resultado final conforme contexto.
  - `root` arredonda resultado final conforme contexto.
  - chamadas sem contexto preservam comportamento compativel via defaults.

- Borda HTTP:
  - `/divide` aceita `context` completo.
  - `/power` aceita `context` completo.
  - `/root` aceita `context` completo.
  - endpoints sem `context` continuam funcionando.
  - `scale = 0` retorna 400 com mensagem clara.
  - `scale = 17` retorna 400 com mensagem clara.
  - `roundingMode` invalido retorna 400 com mensagem clara.
  - `/evaluate` aplica contexto raiz.
  - `/evaluate` aplica contexto local.
  - `/evaluate` herda campos ausentes do contexto mais proximo.
  - OpenAPI expoe `roundingMode` como enum.
