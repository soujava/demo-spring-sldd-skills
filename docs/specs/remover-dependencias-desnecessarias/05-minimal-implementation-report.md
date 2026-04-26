# 05 - Minimal Implementation Report

## Proposed Changes
A tentativa inicial foi remover a dependência `spring-boot-starter-webmvc-test` do `pom.xml`, sob a premissa de que ela era redundante em relação ao `spring-boot-starter-test`.

## Implementation Results
- **Arquivo Modificado:** `pom.xml`
- **Ação:** Remoção da dependência `<artifactId>spring-boot-starter-webmvc-test</artifactId>`.

## Verification Results
- **Comando:** `./mvnw clean test`
- **Resultado:** **FALHA (Regressão)**.
- **Evidência de Erro:**
  ```
  [ERROR] /home/dearrudam/Downloads/demo2/src/test/java/com/example/demo/controller/CalculatorControllerTest.java:[11,58] package org.springframework.boot.webmvc.test.autoconfigure does not exist
  [ERROR] /home/dearrudam/Downloads/demo2/src/test/java/com/example/demo/controller/CalculatorControllerTest.java:[16,2] cannot find symbol
    symbol: class AutoConfigureMockMvc
  ```

## Root Cause Analysis
A investigação revelou que os testes de controller utilizam `MockMvcTester` e a anotação `@AutoConfigureMockMvc` importada do pacote `org.springframework.boot.webmvc.test.autoconfigure`. Diferente do Spring Boot tradicional (onde estas utilidades estão no `spring-boot-test-autoconfigure`), neste projeto (Spring Boot 4.0.5), essas classes são providas especificamente pelo artefato `spring-boot-starter-webmvc-test`.

## Rollback Action
O arquivo `pom.xml` foi restaurado ao seu estado original para garantir a integridade do projeto e a passagem de todos os 89 testes.

## Conclusion for Step 05
A implementação mínima provou que a premissa de redundância estava incorreta. A dependência é necessária para o funcionamento dos testes de integração atuais.
