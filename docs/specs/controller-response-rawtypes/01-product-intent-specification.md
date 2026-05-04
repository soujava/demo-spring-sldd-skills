# Product Intent Specification

## Problem Statement

`CalculatorController` declara metodos HTTP com `ResponseEntity` raw, gerando risco de warning/erro de qualidade e perdendo o contrato tipado dos corpos de resposta.

## Target Users

- Mantenedores do backend que precisam de codigo sem raw types.
- Consumidores indiretos da API que dependem de contratos HTTP claros.

## Formalized Exploration Decisions

- Corrigir somente assinaturas de retorno dos metodos do controller.
- Preservar endpoints, payloads, status HTTP e logica de negocio existentes.
- Usar os DTOs de response atuais como tipos concretos de `ResponseEntity<T>`.

## Success Metrics

- Nenhuma ocorrencia de retorno raw `ResponseEntity` em controllers.
- Testes existentes passam.
- Comportamento HTTP permanece igual para requisicoes validas e invalidas.

## Out of Scope

- Alterar DTOs.
- Alterar modelo de dominio.
- Alterar handlers de erro.
- Alterar rotas, JSON, OpenAPI ou regras de calculo.

## Risks and Assumptions

- Assume-se que os corpos atuais (`EvaluateResponse`, `SumResponse`, `SubtractResponse`, `MultiplyResponse`, `DivideResponse`, `PowerResponse`, `RootResponse`) sao o contrato correto.
- O principal risco e introduzir mudancas comportamentais desnecessarias ao corrigir apenas tipos genericos.

## Acceptance Criteria

- Scenario 1: metodos do controller sem raw types
  Given o projeto possui endpoints do calculator
  When o codigo e compilado/testado
  Then metodos do controller retornam `ResponseEntity<T>` com DTO concreto e sem raw types.

- Scenario 2: comportamento HTTP preservado
  Given uma requisicao valida existente
  When chamar qualquer endpoint alterado
  Then o status e JSON de resposta permanecem equivalentes ao comportamento atual.
