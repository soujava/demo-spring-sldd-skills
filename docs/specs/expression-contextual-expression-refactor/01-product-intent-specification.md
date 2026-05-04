# Product Intent Specification: Expression Contextual Expression Refactor

## Problem Statement

O endpoint `/calculator/evaluate` suporta contexto de calculo no nivel raiz e contexto local por operacao. O contexto local pode ser parcial e herda campos ausentes do contexto pai.

Hoje esse comportamento existe como regra procedural na camada de controller. Isso dificulta a evolucao do modelo `Expression`, porque a arvore de dominio nao representa explicitamente que uma expressao pode ter um contexto local associado.

A intencao deste refactor e preservar o comportamento externo atual e tornar a regra de contexto por no uma capacidade expressavel no modelo de dominio.

## Target Users

- Consumidores da API `/calculator/evaluate` que dependem de contexto raiz e contexto local por operacao.
- Desenvolvedores mantendo o dominio de calculo.
- Desenvolvedores adicionando novas operacoes ou evoluindo a avaliacao de arvores de expressao.

## Formalized Exploration Decisions

- O contrato HTTP atual de `/calculator/evaluate` deve ser preservado.
- O comportamento de contexto local por operacao deve continuar existindo.
- Contextos locais parciais devem herdar campos ausentes do contexto herdado.
- O dominio deve passar a conseguir representar uma expressao com contexto local.
- A assinatura publica de avaliacao de `Expression` deve permanecer baseada em `evaluate(CalculationContext)` e `evaluate()`.
- O contexto local deve ser dado estrutural da arvore de expressao, nao um terceiro parametro de avaliacao.

## Success Metrics

- O comportamento atual de `/calculator/evaluate` permanece compativel.
- Uma operacao com contexto local parcial continua herdando campos do contexto raiz ou pai.
- O modelo de dominio consegue representar uma expressao contextual sem depender de avaliacao recursiva procedural no controller.
- Testes existentes relacionados a contexto continuam passando.
- Novos testes cobrem explicitamente avaliacao de expressao contextual no dominio.

## Out of Scope

- Alterar o contrato JSON publico de `/calculator/evaluate`.
- Remover suporte a contexto local por operacao.
- Trocar `Literal` de `double` para `BigDecimal`.
- Redesenhar todas as operacoes matematicas.
- Alterar endpoints diretos como `/sum`, `/divide`, `/power` e `/root`, exceto quando necessario para preservar compatibilidade.
- Implementar novas operacoes matematicas.

## Risks and Assumptions

- A mudanca pode afetar switches exaustivos sobre a hierarquia `Expression`.
- O refactor deve evitar mudanca comportamental disfarcada em `/calculator/evaluate`.
- A validacao de limites de contexto, como `scale` entre 1 e 16, deve continuar compativel com a API atual.
- A separacao entre DTO de contexto parcial e contexto de dominio completo precisa ficar clara nos proximos passos.
- Assume-se que `ContextualExpression` e uma melhoria interna de modelagem, nao uma nova funcionalidade de API.

## Acceptance Criteria

### AC1: Preservar contexto raiz

Given uma requisicao para `/calculator/evaluate` com `context` raiz
When a expressao contem uma operacao que depende de escala ou arredondamento
Then a operacao deve usar o contexto raiz para calcular o resultado.

### AC2: Preservar contexto local completo

Given uma requisicao para `/calculator/evaluate` com uma operacao contendo `context` local completo
When a operacao e avaliada
Then o contexto local deve prevalecer sobre o contexto herdado.

### AC3: Preservar heranca parcial de contexto local

Given uma requisicao para `/calculator/evaluate` com contexto raiz e uma operacao contendo contexto local parcial
When a operacao e avaliada
Then campos ausentes do contexto local devem ser herdados do contexto pai.

### AC4: Representar contexto local no dominio

Given uma expressao de dominio com contexto local associado
When ela e avaliada com um contexto herdado
Then ela deve resolver o contexto efetivo e avaliar a expressao interna com esse contexto.

### AC5: Preservar assinatura de avaliacao

Given qualquer expressao de dominio
When seu contrato publico e usado
Then a avaliacao deve continuar disponivel por `evaluate(CalculationContext)` e `evaluate()`.

### AC6: Nao alterar contrato HTTP

Given clientes existentes do endpoint `/calculator/evaluate`
When enviarem payloads validos ja suportados
Then as respostas devem manter o mesmo formato e comportamento observavel.
