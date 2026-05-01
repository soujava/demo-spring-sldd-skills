# 01 — Product Intent Specification: Uniformizar Precisão BigDecimal

## Problem Statement

As operações da calculadora apresentam inconsistências de precisão numérica:

- `subtract()` usa aritmética `double` pura, sujeita a perda de precisão (ex: `0.3 - 0.1` ≠ `0.2`)
- `divide()` usa `BigDecimal` mas sem `scale`/`roundingMode`, causando `ArithmeticException` para decimais não-terminantes (ex: `1÷3`)
- O endpoint `/calculator/compose` não oferece controle de precisão para operações DIVIDE compostas

## Target Users

Desenvolvedores e consumidores da API que precisam de resultados numéricos consistentes e previsíveis, com controle explícito sobre arredondamento em divisões.

## Formalized Exploration Decisions

| # | Decisão | Detalhe |
|---|---------|---------|
| 1 | `subtract()` migra para `BigDecimal.valueOf()` | Alinha com `sum()` e `multiply()` |
| 2 | `divide()` recebe `scale` e `roundingMode` parametrizáveis | Defaults: scale=10, HALF_UP |
| 3 | `power()` e `root()` mantêm `Math.pow()` | Sem mudança |
| 4 | Criar `ExpressionContext` (record) | Campos: `scale` (int, default 10), `roundingMode` (RoundingMode, default HALF_UP) |
| 5 | `ExpressionNode` recebe atributo `context` | Tipo `ExpressionContext`, default `ExpressionContext.DEFAULT` |
| 6 | `context` é preservado em todos os nós no modelo | `evaluate()` só utiliza quando `operation == DIVIDE` |
| 7 | `context` no JSON do compose é opcional | Se ausente, usa defaults; sem herança automática entre nós |
| 8 | `/calculator/divide` recebe `scale` e `roundingMode` opcionais no request | Backward-compatible |

## Success Metrics

1. `subtract()` produz resultados precisos com `BigDecimal` (ex: `0.3 - 0.1 = 0.2`)
2. `divide()` com decimais não-terminantes retorna resultado arredondado em vez de `ArithmeticException`
3. Endpoint `/calculator/divide` aceita `scale`/`roundingMode` opcionais
4. Endpoint `/calculator/compose` aceita `context` opcional em nós DIVIDE
5. Todos os testes existentes continuam passando (com ajustes para o novo comportamento do divide)
6. Cobertura de testes para `evaluate()` no domínio

## Out of Scope

- Migração de `power()`/`root()` para `BigDecimal`
- Adição de POWER/ROOT ao `ExpressionOperation`
- Mudanças na arquitetura (hexagonal)
- Padronização de `PowerResponse` (Double → double)
- Padronização de asserções nos testes (JUnit vs AssertJ)

## Risks and Assumptions

| Risco/Assunção | Impacto | Mitigação |
|----------------|---------|-----------|
| Mudança no resultado de `divide()` sem scale explícito | Médio — `10÷3` retorna `3.3333333333` em vez de exception | Documentar breaking change; defaults são razoáveis |
| `ComposeExpressionValidator` fica mais complexo | Baixo — lógica de parsing de `context` é isolada | Testes adicionais |
| Consumidores existentes do `/calculator/divide` podem depender do `ArithmeticException` | Médio | Comunicar breaking change; defaults minimizam impacto |

## Acceptance Criteria

### AC1 — Subtract com BigDecimal

- **Given** o `CalculatorService` com `subtract()` migrado para BigDecimal
- **When** `subtract(0.3, 0.1)` é chamado
- **Then** o resultado é `0.2` (precisão exata)

### AC2 — Divide com scale/rounding defaults

- **Given** o `CalculatorService` com `divide()` atualizado
- **When** `divide(10, 3)` é chamado sem parâmetros de scale
- **Then** o resultado é `3.3333333333` (scale=10, HALF_UP)

### AC3 — Divide com scale/rounding explícitos

- **Given** o `CalculatorService` com `divide()` atualizado
- **When** `divide(10, 3, 5, HALF_UP)` é chamado
- **Then** o resultado é `3.33333` (scale=5, HALF_UP)

### AC4 — Endpoint /calculator/divide com parâmetros opcionais

- **Given** o endpoint `/calculator/divide`
- **When** enviado `{ "dividend": 10, "divisor": 3, "scale": 5, "roundingMode": "HALF_UP" }`
- **Then** retorna `{ "result": 3.33333 }`

### AC5 — Endpoint /calculator/divide backward-compatible

- **Given** o endpoint `/calculator/divide`
- **When** enviado `{ "dividend": 10, "divisor": 3 }` (sem scale/roundingMode)
- **Then** retorna `{ "result": 3.3333333333 }` (defaults)

### AC6 — ExpressionContext no compose

- **Given** o endpoint `/calculator/compose`
- **When** enviado `{ "operation": "DIVIDE", "left": 10, "right": 3, "context": { "scale": 5, "roundingMode": "HALF_UP" } }`
- **Then** retorna resultado com scale=5

### AC7 — Compose sem context usa defaults

- **Given** o endpoint `/calculator/compose`
- **When** enviado `{ "operation": "DIVIDE", "left": 10, "right": 3 }` (sem context)
- **Then** retorna resultado com scale=10, HALF_UP

### AC8 — Testes existentes adaptados

- **Given** os testes unitários e de integração existentes
- **When** executados após a refatoração
- **Then** todos passam com resultados atualizados
