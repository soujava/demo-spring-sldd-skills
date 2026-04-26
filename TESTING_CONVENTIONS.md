# Convencoes de Teste

## Referencia Rapida

| Camada | Tipo | Anotacoes | Instanciacao |
|--------|-------|------|-----------|--------------|
| Logica de negocio | Testes unitarios | Nenhuma | `new ClassName()` direto |
| Borda | Testes de integracao | `@SpringBootTest` + `@AutoConfigureMockMvc` | `@Autowired MockMvcTester` |

**Principio central:** testes unitarios verificam a logica de negocio; testes de integracao verificam contratos HTTP. **Nao misture essas camadas.**

---

## Estrutura de Diretorios

Exemplo de estrutura de testes:

```
src/test/java/com/example/demo/
├── DemoApplicationTests.java              # Smoke test da aplicacao
├── controller/
│   └── CalculatorControllerTest.java      # Testes de integracao (camada HTTP)
└── domain/
    └── CalculatorServiceTest.java         # Testes unitarios (logica de negocio)
```

Os testes espelham a estrutura principal de codigo-fonte em `src/test/java/`.

---

## Convencoes de Nomenclatura

| Camada | Padrao | Exemplo |
|--------|--------|---------|
| Testes unitarios | `{ClassName}Test.java` | `CalculatorServiceTest.java` |
| Testes de integracao | `{ClassName}Test.java` | `CalculatorControllerTest.java` |
| Testes da aplicacao | `{ApplicationName}Tests.java` | `DemoApplicationTests.java` |

---

## Frameworks

- **JUnit 5** — Execucao de testes
- **AssertJ** — Assercoes fluentes (`assertThat(...)`)
- **Spring Boot Test** — Contexto completo + MockMvcTester

**Dependencias** (do `pom.xml`):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Padrao de Teste Unitario

**Local:** `src/test/java/com/example/demo/domain/`

**Regra:** sem anotacoes Spring. Instancie a classe diretamente. Use `org.junit.jupiter.api.Assertions`.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CalculatorService")
class CalculatorServiceTest {

    private CalculatorService service;

    @BeforeEach
    void setUp() {
        service = new CalculatorService();  // Instanciacao direta
    }

    @Test
    @DisplayName("retorna a soma de dois doubles positivos")
    void sumTwoPositiveDoubles() {
        assertEquals(4.0, service.sum(1.5, 2.5));
    }
}
```

**Caracteristicas principais:**
- `@DisplayName` na classe e nos metodos para nomes descritivos
- `@BeforeEach` para setup
- Instanciacao direta da classe
- Assercoes com `assertEquals` do JUnit
- Teste de um cenario por metodo

---

## Padrao de Teste de Integracao

Exemplo de teste de integracao para um endpoint hipotético POST `/calculator/sum`:

**Local:** `src/test/java/com/example/demo/controller/`

**Regra:** contexto Spring completo. Use `MockMvcTester`. Teste apenas o comportamento HTTP. Use `org.assertj.core.api.Assertions`.

```java
import com.example.demo.controller.api.ErrorResponse;
import com.example.demo.controller.api.SumResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("POST /calculator/sum")
class CalculatorControllerTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    @DisplayName("retorna 200 com resultado 4.0 quando firstAddend=1.5 e secondAddend=2.5")
    void happyPath() {
        assertThat(mvc.post().uri("/calculator/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"firstAddend":1.5,"secondAddend":2.5}
                        """))
                .hasStatusOk()
                .bodyJson()
                .convertTo(SumResponse.class)
                .satisfies(response -> assertThat(response.result()).isEqualTo(4.0));
    }
}
```

**Caracteristicas principais:**
- `@SpringBootTest` — Carrega o contexto completo da aplicacao
- `@AutoConfigureMockMvc` — Configura o MockMvc (de `org.springframework.boot.webmvc.test.autoconfigure`)
- `MockMvcTester` — DSL fluente para testes HTTP (recurso do Spring Boot 4)
- Assercoes fluentes com AssertJ
- Testa status e formato do JSON, NAO a logica de negocio

---

## Testes de Tratamento de Erros (Integracao)

```java
@Test
    @DisplayName("retorna 400 quando firstAddend esta ausente")
    void missingFirstAddend() {
        assertThat(mvc.post().uri("/calculator/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"secondAddend":2.5}
                        """))
                .hasStatusBadRequest()
                .bodyJson()
                .convertTo(ErrorResponse.class)
                .satisfies(error -> {
                    assertThat(error.error()).isEqualTo("Bad Request");
                    assertThat(error.message()).isEqualTo("Invalid request body");
                });
    }
```

---

## DTOs para Assercoes com Seguranca de Tipo

Use classes `record` com `.convertTo()` para respostas estruturadas:

```java
record SumResponse(double result) {}
record ErrorResponse(String error, String message) {}

.bodyJson()
.convertTo(SumResponse.class)
.satisfies(response -> assertThat(response.result()).isEqualTo(4.0));
```

---

## Checklist

- [ ] Testes unitarios ficam no pacote da classe alvo
- [ ] Testes de integracao ficam no pacote da classe que trata a borda
- [ ] Testes unitarios usam instanciacao direta, sem `@Autowired`
- [ ] Testes de integracao usam `@Autowired MockMvcTester`
- [ ] Testes unitarios usam `org.junit.jupiter.api.Assertions`
- [ ] Testes de integracao usam `org.assertj.core.api.Assertions`
- [ ] Cada metodo de teste foca em um cenario
- [ ] Use `@DisplayName` para nomes de teste descritivos
- [ ] Testes de integracao verificam status HTTP e JSON, nao logica de negocio
