# 01 - Especificação de Intenção do Produto

1) Declaração do problema

O serviço CalculatorService atualmente usa tipos primitivos `double` para realizar a operação `sum`, o que causa perda de precisão em cálculos financeiros e com muitas casas decimais. Precisamos migrar a implementação interna do cálculo para `BigDecimal` para garantir precisão e previsibilidade de arredondamento, mantendo a assinatura pública que recebe/retorna `double` (opção aprovada). A política de conversão adotada: usar `BigDecimal.valueOf(double)` para conversão determinística, somar com `BigDecimal` e retornar o resultado via `doubleValue()`.

2) Usuários-alvo

- Desenvolvedores que mantêm a biblioteca/serviço.
- Consumidores internos/externos do CalculatorService (APIs, outros módulos).
- Equipe de QA responsável por validar precisão e regressões.

3) Métricas de sucesso (mensuráveis)

- Precisão: novos testes unitários demonstram correção de casos com 10+ casas decimais (comparações exatas).
- Cobertura: >= 90% de cobertura das novas rotas de código relacionadas a `sum` (branch/line para o método).
- Regressão de comportamento: para entradas comuns usadas atualmente, a diferença absoluta entre implementação antiga e nova deve ser <= 1e-12, salvo quando a diferença for causada por rounding mode explícito (se documentado).
- Performance: para listas grandes (ex.: 100k elementos), tempo médio de execução aumenta no máximo 10% em relação à implementação atual (meta a confirmar pela equipe).
- Testes: Todas as suítes existentes continuam passando em CI.

4) Fora do escopo

- Refatoração geral de toda a API matemática do projeto além do método `sum`.
- Mudanças na persistência, banco de dados ou contratos HTTP fora do CalculatorService.
- Adição de novas operações matemáticas além do necessário para suportar `BigDecimal` no `sum`.
- Alterar consumidores externos manualmente — mudanças de assinatura pública só se decididas explicitamente.

5) Riscos e pressupostos

- Pressupomos que podemos alterar a implementação interna do `sum` mantendo a assinatura pública em `double`. Mudar a assinatura pública para `BigDecimal` exigiria coordenação e versão major.
- Risco de regressão numérica se a implementação antiga lidava com Infinity/NaN ou arredondamentos particulares.
- Risco de performance: `BigDecimal` é relativamente mais lento; testes de performance são necessários.
- Risco de formatação/serialização: se o resultado for exposto via JSON, definir como `BigDecimal` será serializado (scale, trailing zeros).
- Dependências: `BigDecimal` é parte do JDK; sem dependências externas adicionais.
- Pressupomos que existe suite de testes unitários para CalculatorService ou que aceitaremos adicionar testes unitários.

Decisões tomadas:

- Opção escolhida: A — manter assinatura pública em `double` e usar `BigDecimal` internamente (conversão com `BigDecimal.valueOf(double)`; retorno via `doubleValue()`).
- Política de arredondamento/scale: por ora não aplicamos arredondamento explícito dentro do `sum`; se houver necessidade (ex.: valores monetários) será introduzida configuração dedicada com RoundingMode e escala.

6) Critérios de aceitação (Given / When / Then)

Critério A — Caminho feliz
- Dado que o CalculatorService recebe números com precisão decimal arbitrária (ex.: 0.1, 0.2, 0.0000000001)
- Quando executar `sum` com esses números
- Então o resultado deve ser calculado usando `BigDecimal` com precisão exata e corresponder ao valor matemático esperado (ex.: 0.1 + 0.2 = 0.3 exatamente). Novos testes unitários devem passar.

Critério B — Compatibilidade com inputs existentes (assinatura `double` mantida)
- Dado que a assinatura pública é mantida como `double`
- Quando chamar `sum(double a, double b)` com valores finitos
- Então a implementação deve converter os `double` para `BigDecimal` de forma determinística (`BigDecimal.valueOf(double)`) e produzir um resultado compatível com as expectativas de precisão, sem discrepâncias inesperadas para casos comuns de uso.

Critério C — Validação de entradas inválidas / não numéricas
- Dado que entradas inválidas são fornecidas (nulls)
- Quando `sum` for chamado com `null` (ou coleção contendo `null`)
- Então o serviço deve lançar `IllegalArgumentException` (ou a exceção prevista pela convenção do projeto). Testes unitários devem cobrir esses casos.

Critério D — Arredondamento e escala controlados
- Dado que há requisitos de negócio que demandem controle de escala/arredondamento
- Quando `sum` receber valores que requerem arredondamento
- Então a implementação futura deve aplicar explicitamente um RoundingMode e escala configuráveis (p.ex. RoundingMode.HALF_EVEN); este comportamento ficará documentado e configurável em tarefa separada.

Critério E — Edge case: coleção vazia / soma neutra
- Dado que `sum` é chamada com uma coleção vazia de números
- Quando a operação for executada
- Então o resultado deve ser `BigDecimal.ZERO` (convertido para `double` quando aplicável) e coberto por testes.

Critério F — Edge case: valores muito grandes / precisão extrema
- Dado que a entrada contém valores com muitas casas decimais ou magnitude muito grande
- Quando `sum` for executado
- Então o método deve comportar-se de maneira previsível: não produzir número inválido; se ultrapassar limites, lançar exceção ou aplicar política de escala/precisão definida. Testes devem verificar limites.

Critério G — Regressão de performance
- Dado um conjunto grande de números (ex.: 100k elementos)
- Quando executar `sum` em benchmark comparável à base existente
- Então a degradação de tempo médio não deve exceder a meta definida (p.ex. 10%); resultados de benchmark devem ser anexados à PR.

---

Este documento é o Passo 01 — Especificação de Intenção do Produto para a mudança de implementação do método `sum` do CalculatorService.
