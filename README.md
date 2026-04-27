# Demo Calculator API

API HTTP de calculadora feita com Spring Boot 4.0.5, Java 25 e Maven. O projeto expoe operacoes aritmeticas simples e tambem avaliacao de expressoes compostas via endpoints REST.

Este repositorio tambem e usado para testar o workflow [SLDD (Software Lifecycle-Driven Development)](https://github.com/soujava/sldd-skills).

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

## Como testar

```bash
./mvnw test
```

Os testes seguem duas camadas distintas:

- Logica de negocio: testes unitarios puros, sem contexto Spring.
- Borda HTTP: testes de integracao com `@SpringBootTest`, `@AutoConfigureMockMvc` e `MockMvcTester`.

Veja os padroes completos em [TESTING_CONVENTIONS.md](TESTING_CONVENTIONS.md).

## OpenAPI

Com a aplicacao em execucao:

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

Todos os endpoints recebem `Content-Type: application/json` e retornam JSON.

| Metodo | Caminho | Payload |
|--------|---------|---------|
| POST | `/calculator/sum` | `firstAddend`, `secondAddend` |
| POST | `/calculator/subtract` | `minuend`, `subtrahend` |
| POST | `/calculator/multiply` | `multiplicand`, `multiplier` |
| POST | `/calculator/divide` | `dividend`, `divisor` |
| POST | `/calculator/power` | `base`, `exponent` |
| POST | `/calculator/root` | `radicand`, `index` |
| POST | `/calculator/compose` | `operation`, `left`, `right` |

## Respostas

### Sucesso

Os endpoints de operacao simples retornam:

```json
{"result":4.0}
```

O endpoint `/calculator/compose` tambem retorna o mesmo formato:

```json
{"result":16.0}
```

### Erros

Payloads invalidos, JSON malformado e operacoes aritmeticas invalidas retornam um erro padronizado:

```json
{"error":"Bad Request","message":"Invalid request body"}
```

Resultados numericos fora do limite suportado podem retornar:

```json
{"error":"Unprocessable Entity","message":"Numeric overflow: result is too large"}
```

## Exemplos

### Soma

```bash
curl -s -X POST http://localhost:8080/calculator/sum \
  -H 'Content-Type: application/json' \
  -d '{"firstAddend":1.5,"secondAddend":2.5}'
```

Resposta:

```json
{"result":4.0}
```

### Divisao

```bash
curl -s -X POST http://localhost:8080/calculator/divide \
  -H 'Content-Type: application/json' \
  -d '{"dividend":10.0,"divisor":2.0}'
```

### Potenciacao

```bash
curl -s -X POST http://localhost:8080/calculator/power \
  -H 'Content-Type: application/json' \
  -d '{"base":2.0,"exponent":3.0}'
```

### Raiz

```bash
curl -s -X POST http://localhost:8080/calculator/root \
  -H 'Content-Type: application/json' \
  -d '{"radicand":9.0,"index":2.0}'
```

### Expressao composta

```bash
curl -s -X POST http://localhost:8080/calculator/compose \
  -H 'Content-Type: application/json' \
  -d '{"operation":"ADD","left":10,"right":{"operation":"MULTIPLY","left":2,"right":3}}'
```

## Estrutura principal

```text
src/main/java/com/example/demo/
├── DemoApplication.java
├── controller/
│   ├── CalculatorController.java
│   ├── ApiErrorHandler.java
│   └── api/
├── controller/validation/
│   └── ComposeExpressionValidator.java
└── domain/
    ├── CalculatorService.java
    └── expression/
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
