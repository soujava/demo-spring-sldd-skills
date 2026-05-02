# 01 — Product Intent Specification

## Problem Statement

As operacoes matematicas da calculadora que dependem de precisao e arredondamento precisam aceitar regras explicitas de calculo, evitando comportamento numerico implicito, inconsistente ou espalhado pelo codigo.

A API deve permitir que clientes configurem contexto de calculo para `divide`, `power`, `root` e para operacoes equivalentes dentro de `/calculator/evaluate`, mantendo compatibilidade com payloads existentes que nao enviam contexto.

## Target Users

- Clientes HTTP que consomem a API de calculadora.
- Microsservicos que dependem de resultados numericos previsiveis.
- Desenvolvedores que mantem ou expandem operacoes matematicas no dominio.

## Formalized Exploration Decisions

- Introduzir `CalculationContext` no dominio.
- Defaults: `scale = 10` e `roundingMode = HALF_UP`.
- `scale` valido: `1..16`.
- `roundingMode` deve ser enum baseado em `java.math.RoundingMode`.
- Endpoints que aceitam `context`: `/calculator/divide`, `/calculator/power`, `/calculator/root`, `/calculator/evaluate`.
- Operacoes que consomem contexto: `DIVIDE`, `POWER`, `ROOT`.
- `/evaluate` aceita contexto raiz e contexto local por operacao.
- Heranca de contexto em `/evaluate` segue o contexto mais proximo, campo a campo.
- Contexto invalido retorna HTTP 400 com `ErrorResponse` e mensagem clara.

## Success Metrics

- Payloads antigos sem `context` continuam funcionando.
- `divide`, `power` e `root` aceitam contexto opcional.
- `/evaluate` permite contexto raiz e local em operacoes interessadas.
- `scale` invalido retorna 400.
- `roundingMode` invalido retorna 400.
- Divisao nao terminante pode ser arredondada conforme contexto.
- OpenAPI consegue inferir valores validos de `roundingMode`.

## Out of Scope

- Refatorar `Expression` para records de operacao.
- Introduzir Strategy pattern.
- Alterar endpoints que nao consomem contexto.
- Trocar todos os payloads numericos para `BigDecimal` ou string.
- Persistencia de contexto.
- Configuracao global externa via properties.
- Autenticacao ou autorizacao.

## Risks and Assumptions

- `RoundingMode.UNNECESSARY` pode causar erro quando arredondamento for necessario.
- `POWER` e `ROOT` continuam usando o calculo base atual, aplicando contexto ao resultado final.
- Mudancas de arredondamento podem alterar resultados previamente retornados por `power` e `root`.
- Mensagens de erro dependem do tratamento global de excecoes HTTP.
- O contrato deve preservar compatibilidade para clientes que nao enviam `context`.

## Acceptance Criteria

```
Given: um cliente envia POST /calculator/divide sem context
When: a divisao e executada
Then: a API usa CalculationContext default com scale 10 e HALF_UP

Given: um cliente envia POST /calculator/divide com context scale 4 e roundingMode HALF_UP
When: divide 1 por 3
Then: a resposta usa arredondamento conforme o contexto informado

Given: um cliente envia context com scale 0
When: chama endpoint que aceita context
Then: a resposta e HTTP 400 com ErrorResponse contendo mensagem clara sobre scale invalido

Given: um cliente envia context com scale 17
When: chama endpoint que aceita context
Then: a resposta e HTTP 400 com ErrorResponse contendo mensagem clara sobre scale invalido

Given: um cliente envia roundingMode invalido
When: chama endpoint que aceita context
Then: a resposta e HTTP 400 com ErrorResponse contendo mensagem clara sobre roundingMode invalido

Given: um cliente envia POST /calculator/power com context
When: a operacao e executada
Then: o resultado final e arredondado usando scale e roundingMode resolvidos

Given: um cliente envia POST /calculator/root com context
When: a operacao e executada
Then: o resultado final e arredondado usando scale e roundingMode resolvidos

Given: um cliente envia POST /calculator/evaluate com context raiz
When: a expressao contem DIVIDE, POWER ou ROOT sem context local
Then: essas operacoes usam o context raiz

Given: uma operacao dentro de /calculator/evaluate possui context local parcial
When: a operacao e avaliada
Then: campos ausentes sao herdados do contexto mais proximo disponivel

Given: payloads existentes sem context
When: clientes chamam endpoints existentes
Then: os contratos continuam funcionando sem exigir alteracao dos clientes
```
