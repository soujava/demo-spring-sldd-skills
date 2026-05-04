# High-Level Technical Design

## Requirements Traceability

Cada acceptance criterion sera atendido tipando os retornos dos 7 endpoints de `CalculatorController` com os DTOs atuais, sem mudar corpo ou status HTTP.

## Architecture Diagram

```text
HTTP request -> CalculatorController -> domain Expression -> DTO response -> ResponseEntity<DTO>
Exception -> ApiErrorHandler -> ResponseEntity<ErrorResponse>
```

## Component Responsibilities

- `CalculatorController`: manter orquestracao HTTP e expor contratos genericos concretos nas respostas.
- DTOs de API: permanecer como contratos de request/response existentes.
- Dominio `Expression`: permanecer responsavel pela logica de calculo.
- `ApiErrorHandler`: manter respostas de erro tipadas ja existentes.

## Data Flow

Entrada, validacao, processamento e serializacao permanecem iguais. A mudanca planejada e somente na assinatura dos metodos, de `ResponseEntity` para `ResponseEntity<...Response>`.

## Security and Observability Requirements

Sem mudanca de autenticacao, autorizacao, logs ou metricas.

## Trade-Offs and Alternatives

- Usar tipo concreto por endpoint e preferivel a `ResponseEntity<?>`, pois elimina raw type e preserva contrato mais preciso.
- Nao criar wrapper comum porque seria mudanca maior sem necessidade para esta correcao.

## High-Level Test Scenario Map

- Busca estatica por raw `ResponseEntity` em controllers.
- Executar suite Maven para confirmar compilacao e comportamento HTTP existente.
