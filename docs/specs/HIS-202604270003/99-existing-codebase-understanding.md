# Existing Codebase Understanding - HIS-202604270003

## Repository Structure Overview
O projeto segue uma estrutura Spring Boot padrão com camadas bem definidas:
* `controller/`: Endpoints REST e DTOs de API.
* `domain/`: Lógica de negócio e exceções customizadas.
* `test/`: Testes de unidade e integração (Controller e Service).

## Architecture Summary
* **Estilo:** Arquitetura em camadas (Controller -> Service).
* **Tecnologias:** Java 25, Spring Boot 4.0.5, Jackson para JSON, JUnit 5 e MockMvc para testes.
* **Padronização:** Uso extensivo de `Records` para DTOs e tipos imutáveis. O `CalculatorService` utiliza `BigDecimal` internamente para garantir precisão, convertendo para `double` apenas na interface pública.

## Conventions to Preserve
* **Imutabilidade:** Uso de `Records` para dados.
* **Precisão:** Cálculos via `BigDecimal` no domínio.
* **Tratamento de Erro:** Centralizado no `ApiErrorHandler`.
* **Naming:** Requests terminam em `Request`, Responses em `Response`.

## Integration Points
* **`CalculatorController`:** Novo endpoint será adicionado aqui ou em um novo controller especializado.
* **`CalculatorService`:** A lógica de avaliação da árvore deve ser integrada aqui, reaproveitando os métodos `sum`, `subtract`, `multiply`, `divide`, `power` e `root`.
* **`ApiErrorHandler`:** Deve capturar novas exceções de domínio ou erros de parsing da árvore.

## Risks and Unknowns
* **Recursão Infinita:** Necessidade de limitar a profundidade do JSON para evitar ataques de DoS.
* **Polimorfismo no Jackson:** Configurar corretamente o Jackson para lidar com a `sealed interface` de DTOs sem perder a validação `@Valid`.

## Context to Carry Into Steps 02-06
* Devemos manter o uso de `BigDecimal.valueOf(double)` para consistência com o `CalculatorService`.
* A validação deve ser feita via Bean Validation no DTO e via Compact Constructor no Domínio.
