# Product Intent Specification - HIS-202604270003

## Problem Statement
Atualmente, a API de calculadora exige múltiplas requisições para realizar cálculos compostos (ex: `(a + b) * c`), o que aumenta a latência e a complexidade no lado do cliente. É necessária uma forma de enviar uma árvore de expressões completa para processamento em uma única chamada.

## Target Users
Desenvolvedores de integração que precisam realizar operações matemáticas encadeadas com alta precisão e segurança.

## Success Metrics
* Avaliação correta de expressões aninhadas (árvores) respeitando a ordem definida.
* Validação rigorosa de segurança (profundidade da árvore) e domínio (divisão por zero).
* Manutenção da precisão decimal através do uso de `BigDecimal` conforme padrão do projeto.

## Out of Scope
* Funções científicas avançadas (seno, cosseno, logaritmos).
* Suporte a variáveis ou memória de estado entre requisições.
* Interface gráfica para construção das expressões.

## Risks and Assumptions
* **Assunção:** O uso de Java 25 facilitará o mapeamento e avaliação via Pattern Matching e Records.
* **Risco:** Árvores excessivamente profundas podem causar `StackOverflowError` se não houver limite de recursão.
* **Risco:** Divisões por zero em nós intermediários da árvore.

## Acceptance Criteria (Given/When/Then)

### AC1: Cálculo de Árvore Simples
* **Given:** Uma expressão representando `(10 + 5) * 2`.
* **When:** O endpoint `/calculator/evaluate` for chamado.
* **Then:** O resultado deve ser `30.0`.

### AC2: Validação de Divisão por Zero
* **Given:** Uma expressão contendo uma divisão por zero em qualquer nível.
* **When:** O endpoint for chamado.
* **Then:** Deve retornar um erro `400 Bad Request` com mensagem apropriada.

### AC3: Tipagem e Estrutura
* **Given:** Um JSON que não segue a estrutura de `Literal` ou `BinaryOperation`.
* **When:** O endpoint for chamado.
* **Then:** O Bean Validation deve rejeitar a requisição.
