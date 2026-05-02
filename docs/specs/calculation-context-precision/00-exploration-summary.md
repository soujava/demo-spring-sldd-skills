# 00 — Exploration Summary

## Current Understanding

O objetivo do workflow e adotar e definir regras de precisao para operacoes matematicas da calculadora por meio de um contexto de calculo explicito.

O foco deste workflow e exclusivamente `CalculationContext` e suas regras de precisao. Ideias anteriores sobre refatorar `Expression` para records de operacao, Strategy ou outro modelo orientado a objetos foram descartadas deste escopo.

## Candidate Product Decisions

- Introduzir `CalculationContext` como um Java record no pacote `com.example.demo.domain`.
- Definir `CalculationContext.defaults()` com:
  - `scale = 10`
  - `roundingMode = RoundingMode.HALF_UP`
- Permitir que apenas os seguintes endpoints recebam `context`:
  - `POST /calculator/divide`
  - `POST /calculator/power`
  - `POST /calculator/root`
  - `POST /calculator/evaluate`
- As operacoes que consomem contexto sao:
  - `DIVIDE`
  - `POWER`
  - `ROOT`
- `DIVIDE` usa `scale` e `roundingMode` na divisao.
- `POWER` e `ROOT` usam `scale` e `roundingMode` para arredondar o resultado final.
- `/calculator/evaluate` deve aceitar contexto raiz e contexto local por operacao.
- Em `/calculator/evaluate`, a heranca de contexto segue a precedencia:
  - contexto local da operacao
  - contexto raiz do request
  - `CalculationContext.defaults()`
- A heranca de contexto e campo a campo.

## Candidate API Rules

- `scale` e opcional.
- `roundingMode` e opcional.
- `scale` informado deve estar entre `1` e `16`.
- `roundingMode` deve ser tipado como enum nos payloads para favorecer inferencia no OpenAPI.
- `roundingMode` aceita todos os nomes oficiais de `java.math.RoundingMode`:
  - `UP`
  - `DOWN`
  - `CEILING`
  - `FLOOR`
  - `HALF_UP`
  - `HALF_DOWN`
  - `HALF_EVEN`
  - `UNNECESSARY`
- Contexto invalido deve retornar `400 Bad Request` com `ErrorResponse` e mensagem clara sobre a causa.

## Payload Examples

### Divide with Context

```json
{
  "dividend": 1,
  "divisor": 3,
  "context": {
    "scale": 4,
    "roundingMode": "HALF_UP"
  }
}
```

### Power with Context

```json
{
  "base": 2,
  "exponent": 0.5,
  "context": {
    "scale": 4,
    "roundingMode": "HALF_UP"
  }
}
```

### Root with Context

```json
{
  "radicand": 2,
  "index": 2,
  "context": {
    "scale": 6,
    "roundingMode": "HALF_EVEN"
  }
}
```

### Evaluate with Root Context and Local Override

```json
{
  "context": {
    "scale": 6,
    "roundingMode": "HALF_EVEN"
  },
  "expression": {
    "type": "operation",
    "operator": "DIVIDE",
    "left": {
      "type": "literal",
      "value": 1
    },
    "right": {
      "type": "literal",
      "value": 3
    },
    "context": {
      "scale": 2
    }
  }
}
```

Nesse exemplo, a operacao `DIVIDE` usa `scale = 2` e `roundingMode = HALF_EVEN`.

## Alternatives Discussed

- Usar `PrecisionContext` como nome do contexto.
- Usar `CalculationContext`, aprovado por deixar espaco para informacoes futuras alem de precisao.
- Aceitar `roundingMode` como `String`.
- Tipar `roundingMode` como enum no DTO, aprovado para melhorar inferencia no OpenAPI.
- Receber contexto apenas em `/divide` e `/evaluate`.
- Expandir para `/divide`, `/power`, `/root` e `/evaluate`, aprovado porque `DIVIDE`, `POWER` e `ROOT` consomem contexto.

## Open Questions

Nao ha questoes funcionais principais pendentes ao final da exploracao. Detalhes de design, contratos internos, testes e versao devem ser definidos nos passos SLDD apropriados.

## Risks and Assumptions

- `RoundingMode.UNNECESSARY` pode causar excecao quando uma operacao exigir arredondamento.
- `POWER` e `ROOT` permanecem dependentes de `Math.pow` para o calculo base; o contexto atua no arredondamento do resultado final.
- A mudanca altera comportamento numerico de operacoes que atualmente retornam resultados sem arredondamento padronizado.
- O contrato deve preservar compatibilidade para payloads existentes sem `context`.

## Suggested Next SLDD Step

Iniciar `sldd-01-product-intent-specification` para formalizar problema, usuarios, escopo, metricas de sucesso, riscos e criterios de aceitacao.
