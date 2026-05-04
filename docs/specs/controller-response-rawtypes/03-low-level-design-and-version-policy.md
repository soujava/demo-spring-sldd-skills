# Low-Level Design and Version Policy

## Requirement-to-Design Traceability

- AC1 e coberto por assinaturas `ResponseEntity<T>` em cada endpoint do `CalculatorController`.
- AC2 e coberto por nao alterar body, status HTTP, rotas ou logica de negocio, validando com os testes existentes.

## API Contracts

- `/calculator/evaluate` retorna `ResponseEntity<EvaluateResponse>`.
- `/calculator/sum` retorna `ResponseEntity<SumResponse>`.
- `/calculator/subtract` retorna `ResponseEntity<SubtractResponse>`.
- `/calculator/multiply` retorna `ResponseEntity<MultiplyResponse>`.
- `/calculator/divide` retorna `ResponseEntity<DivideResponse>`.
- `/calculator/power` retorna `ResponseEntity<PowerResponse>`.
- `/calculator/root` retorna `ResponseEntity<RootResponse>`.

## Data Models

Nenhum DTO muda. Os modelos atuais de request e response permanecem como contrato serializado.

## Error Model

Nenhum handler muda. `ApiErrorHandler` continua retornando `ResponseEntity<ErrorResponse>`.

## Test Strategy

Step 04 deve introduzir uma verificacao automatizada que falhe enquanto houver `ResponseEntity` raw no controller. Step 05 altera as assinaturas e roda a suite Maven para confirmar compilacao e comportamento existente.

## Test Scenario Catalog

- Detectar retorno raw `ResponseEntity` em metodos HTTP do controller.
- Confirmar compilacao apos tipar assinaturas.
- Confirmar testes HTTP existentes sem regressao.

## Dependency and Version Policy

As dependencias atuais sao suficientes. Nenhuma nova dependencia e necessaria, evitando impacto em runtime, testes e manutencao.

## Ordered Implementation Plan

1. Adicionar teste/checagem para raw types em `CalculatorController`.
2. Confirmar falha do teste novo contra o estado atual.
3. Mudar assinaturas do controller para `ResponseEntity<...Response>`.
4. Executar busca estatica por raw `ResponseEntity` em controllers.
5. Executar `./mvnw test`.
6. Registrar verificacao final.
