# High-Level Technical Design - HIS-202604270003

## Architecture Diagram
```text
[Client] -> JSON Tree (POST /calculator/evaluate)
   |
[CalculatorController] (DTO validation & transformation)
   |
[ExpressionMapper] (Converts ExpressionDto -> Expression Domain)
   |
[CalculatorService] (Recursive Evaluation via Pattern Matching)
   |
[BigDecimal Operations] (Existing logic for sum, sub, etc.)
```

## Component Responsibilities
* **`ExpressionDto` (Sealed Record):** Representação externa, usa Jackson para polimorfismo e Bean Validation recursivo.
* **`Expression` (Sealed Record):** Representação interna de domínio, garante integridade (ex: proibição estática de divisão por zero básica).
* **`ExpressionMapper`:** Centraliza a lógica de conversão e proteção contra recursão profunda (DoS).
* **`CalculatorService`:** Resolve a árvore de domínio recursivamente, mantendo a precisão via `BigDecimal`.

## Data Flow
1. O cliente envia uma árvore de operações no corpo de um POST.
2. O Spring desserializa para `CompoundRequest` e valida a estrutura.
3. O Controller chama o mapeador que transforma DTOs em objetos de domínio imutáveis.
4. O Service avalia a `Expression` resultante e retorna o `double` final.
5. Em caso de erro (ex: divisão por zero ou overflow), a exceção é capturada pelo `ApiErrorHandler` existente.

## Security and Observability Requirements
* **Segurança:** Limitar a profundidade da árvore (ex: máximo 10 níveis) para evitar `StackOverflowError`.
* **Observabilidade:** Adicionar logs de debug para o tempo de avaliação de expressões complexas.

## Trade-Offs and Alternatives
* **Alternativa:** Usar o Spring Expression Language (SpEL). **Decisão:** Rejeitado para manter o domínio puro e evitar dependência de framework na lógica de cálculo, além de garantir maior controle sobre as operações permitidas.
* **Alternativa:** Abordagem de Lista/Steps. **Decisão:** Rejeitado em favor da Árvore por ser mais flexível e elegante com as novas features do Java 25.

## High-Level Test Scenario Map
* **Unitários:** Avaliação de diferentes tipos de `Expression` (literais, operações simples, aninhadas).
* **Integração:** Validação de payloads JSON complexos e comportamento do endpoint REST.
* **Edge Cases:** Divisões por zero em diferentes níveis da árvore, estouro de capacidade numérica (infinity), árvores vazias ou inválidas.
