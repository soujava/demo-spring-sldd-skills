# Demo Calculator API

Este é um projeto de exemplo com o objetivo de testar o workflow [SLDD (Software Lifecycle-Driven Development)](https://github.com/soujava/sldd-skills).

API HTTP de calculadora criada com Spring Boot 4, Java 25 e Maven. A aplicacao expoe operacoes aritmeticas por endpoints REST, valida payloads JSON com Jakarta Validation e publica documentacao OpenAPI via SpringDoc.

## Requisitos

- Java 25
- Maven Wrapper incluido no projeto (`./mvnw`)

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicacao sobe por padrao em:

```text
http://localhost:8080
```

Observacao: use `spring-boot:run` com hifen. O comando `springboot:run` nao e um goal Maven valido para este projeto.

## Como testar

```bash
./mvnw test
```

Os testes seguem duas camadas:

- Logica de negocio: testes unitarios puros, sem contexto Spring.
- Borda HTTP: testes de integracao com `@SpringBootTest`, `@AutoConfigureMockMvc` e `MockMvcTester`.

Veja detalhes em [TESTING_CONVENTIONS.md](TESTING_CONVENTIONS.md).

## OpenAPI

Com a aplicacao em execucao:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

Todos os endpoints recebem `Content-Type: application/json` e retornam um JSON com o campo `result` em caso de sucesso.

| Metodo | Caminho | Payload |
|--------|---------|---------|
| POST | `/calculator/sum` | `firstAddend`, `secondAddend` |
| POST | `/calculator/subtract` | `minuend`, `subtrahend` |
| POST | `/calculator/multiply` | `multiplicand`, `multiplier` |
| POST | `/calculator/divide` | `dividend`, `divisor` |
| POST | `/calculator/power` | `base`, `exponent` |
| POST | `/calculator/root` | `radicand`, `index` |

## Exemplos

Soma:

```bash
curl -s -X POST http://localhost:8080/calculator/sum \
  -H 'Content-Type: application/json' \
  -d '{"firstAddend":1.5,"secondAddend":2.5}'
```

Resposta:

```json
{"result":4.0}
```

Divisao:

```bash
curl -s -X POST http://localhost:8080/calculator/divide \
  -H 'Content-Type: application/json' \
  -d '{"dividend":10.0,"divisor":2.0}'
```

Potenciacao:

```bash
curl -s -X POST http://localhost:8080/calculator/power \
  -H 'Content-Type: application/json' \
  -d '{"base":2.0,"exponent":3.0}'
```

Raiz:

```bash
curl -s -X POST http://localhost:8080/calculator/root \
  -H 'Content-Type: application/json' \
  -d '{"radicand":9.0,"index":2.0}'
```

## Erros

Payloads invalidos, JSON malformado e operacoes aritmeticas invalidas retornam erro padronizado:

```json
{"error":"Bad Request","message":"Invalid request body"}
```

Resultados numericos fora do limite suportado podem retornar:

```json
{"error":"Unprocessable Entity","message":"Numeric overflow: result is too large"}
```

## Estrutura principal

```text
src/main/java/com/example/demo/
├── DemoApplication.java
├── controller/
│   ├── CalculatorController.java
│   ├── ApiErrorHandler.java
│   └── api/
└── domain/
    ├── CalculatorService.java
    └── NumericOverflowException.java
```

## Stack

- Spring Boot 4.0.5
- Java 25
- Maven
- Spring MVC
- Jakarta Validation
- SpringDoc OpenAPI
- JUnit 5
- AssertJ
