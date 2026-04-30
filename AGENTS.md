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

## Conventional Commits

Todos os commits devem seguir o padrao [Conventional Commits](https://www.conventionalcommits.org/): `tipo(escopo): descricao`.

Tipos: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `ci`, `perf`, `style`, `build`.

## Convencoes Java 25

O projeto usa Java 25. Sempre prefira as features modernas da linguagem.

Para padroes detalhados, exemplos e checklist → [JAVA25_CONVENTIONS.md](JAVA25_CONVENTIONS.md)

