# Agentes

## Build e Testes

- `./mvnw test` — executa todos os testes
- `./mvnw spring-boot:run` — inicia a aplicacao
- Spring Boot 4.0.5, Java 25, Maven project

## Arquitetura

- Ponto de entrada: `com.example.demo.DemoApplication`

## Convencoes de Teste (importante)

Existem duas camadas de teste distintas com padroes diferentes. Nao as misture:

| Camada | Local | Tipo | Anotacoes |
|--------|-------|------|-----------|
| Logica de negocio | Testes unitarios | JUnit puro, sem contexto Spring | — |
| Borda | Testes de integracao | Contexto Spring completo + MockMvcTester | `@SpringBootTest` + `@AutoConfigureMockMvc` (de `org.springframework.boot.webmvc.test.autoconfigure`) |

**Testes unitarios** (Logica de negocio): instancie a classe diretamente e teste apenas a logica de negocio.

**Testes de integracao** (Borda): testam o comportamento HTTP (status, formato do JSON), nao a logica de negocio.

Para padroes detalhados, exemplos e checklist → [TESTING_CONVENTIONS.md](TESTING_CONVENTIONS.md)

