# Low-Level Design and Version Policy

## API contracts

- Endpoint principal
  - Metodo: `POST`
  - Path: `/calculator/sum`
  - Request content type: `application/json`
  - Response content type: `application/json`

- Request schema

```json
{
  "firstAddend": 1.5,
  "secondAddend": 2.5
}
```

- Success response

```json
{
  "result": 4.0
}
```

- Error response padronizado

```json
{
  "error": "Bad Request",
  "message": "Invalid request body"
}
```

- Regras de contrato
  - `firstAddend` e obrigatorio
  - `secondAddend` e obrigatorio
  - ambos devem ser numericos e desserializaveis para `Double`
  - corpo vazio deve retornar `400`
  - JSON malformado deve retornar `400`
  - tipos invalidos, como string nao numerica ou boolean, devem retornar `400`
  - `NaN`, `Infinity` e `-Infinity` ficam fora do contrato suportado
  - payload com campos extras nao deve quebrar o endpoint
  - o contrato de sucesso do endpoint nao muda com a migracao para Spring Boot `4`

- Contrato de migracao
  - o build oficial deve usar Java `25`
  - o projeto deve usar Spring Boot `4`
  - a documentacao deve refletir essa stack como oficial

## Data models

- Request DTO

```text
SumRequest
- Double firstAddend
- Double secondAddend
- Bean Validation para campos obrigatorios
```

- Success DTO

```text
SumResponse
- double result
```

- Error DTO

```text
ErrorResponse
- String error
- String message
```

- Service

```text
CalculatorService
- double sum(double firstAddend, double secondAddend)
```

- Handler de erro

```text
ApiErrorHandler
- trata erros de validacao do request
- trata erros de desserializacao/parsing
- retorna ErrorResponse com status 400
```

- Arquivos documentais afetados

```text
pom.xml
AGENTS.md
TESTING_CONVENTIONS.md
```

## Error model

- Validacao estrutural
  - `SumRequest` usa Bean Validation para garantir `firstAddend` e `secondAddend` obrigatorios
  - falhas esperadas:
    - `MethodArgumentNotValidException` para campo ausente apos binding de `null`
  - resposta:
    - HTTP `400`
    - corpo `{"error":"Bad Request","message":"Invalid request body"}`

- Validacao de parsing/binding
  - JSON vazio, malformado, ou tipo nao desserializavel gera erro no binding do Spring/Jackson
  - falhas esperadas:
    - `HttpMessageNotReadableException`
  - resposta:
    - HTTP `400`
    - corpo `{"error":"Bad Request","message":"Invalid request body"}`

- Valores especiais de `double`
  - politica da feature:
    - `NaN`, `Infinity` e `-Infinity` sao fora do contrato suportado
  - tratamento:
    - se forem rejeitados no parse/binding, retornam `400` pelo mesmo handler
    - nao sera criada logica de negocio para suportar esses valores

- Erros de negocio
  - nao ha novo erro de negocio
  - `CalculatorService` continua apenas somando dois `double`

## Test strategy

- Testes de migracao de stack
  - validar que o projeto compila e testa com Java `25`
  - validar que o projeto sobe e testa com Spring Boot `4`
  - validar que os imports e anotacoes de teste ficam consistentes com a nova stack

- Testes unitarios
  - manter `CalculatorServiceTest`
  - nao devem mudar a regra de negocio
  - servem para confirmar ausencia de regressao da soma

- Testes de integracao HTTP
  - atualizar `CalculatorControllerTest` para o estilo oficial da stack migrada
  - objetivo:
    - happy path segue `200`
    - falhas de contrato retornam `400`
    - corpo de erro passa a ser assertado
  - adotar `MockMvcTester` no Step 04 se a stack Spring Boot `4` no projeto o suportar conforme planejado

- Testes documentais/contratuais
  - nao sao testes executaveis de software, mas o Step 06 deve verificar:
    - `AGENTS.md` alinhado com Java `25` e Spring Boot `4`
    - `TESTING_CONVENTIONS.md` alinhado com a abordagem real de teste
    - exemplos atualizados para `firstAddend` e `secondAddend`

## Test scenario catalog with edge cases

- Sucesso
  - `POST /calculator/sum` com `1.5` e `2.5` retorna `200` e `result=4.0`
  - `POST /calculator/sum` com zero retorna `200`
  - `POST /calculator/sum` com valor negativo retorna `200`
  - `POST /calculator/sum` com doubles muito grandes, mas finitos, retorna `200`

- Falha de validacao
  - ausencia de `firstAddend` retorna `400` com `ErrorResponse`
  - ausencia de `secondAddend` retorna `400` com `ErrorResponse`

- Falha de parsing
  - corpo vazio retorna `400` com `ErrorResponse`
  - JSON malformado retorna `400` com `ErrorResponse`
  - `firstAddend` como `"abc"` retorna `400` com `ErrorResponse`
  - `secondAddend` como `true` retorna `400` com `ErrorResponse`

- Edge cases de contrato
  - payload com campos extras continua nao quebrando o endpoint
  - `NaN`, `Infinity` e `-Infinity` sao tratados como fora do contrato suportado
  - o corpo de erro para qualquer entrada invalida coberta pela feature deve ser consistente

- Migracao
  - testes passam apos atualizar `pom.xml` para Java `25` e Spring Boot `4`
  - testes de integracao usam a abordagem oficial documentada da stack nova
  - nao ha regressao do contrato `200` do endpoint

## Dependency/version policy

- Runtime e framework oficiais
  - Java `25`
  - Spring Boot `4.x`
  - o `pom.xml` deve ser atualizado para refletir essa combinacao e passar a ser a fonte de verdade oficial

- Politica de dependencia
  - manter apenas dependencias necessarias:
    - `spring-boot-starter-web`
    - `spring-boot-starter-validation`
    - `spring-boot-starter-test`
  - so introduzir dependencia adicional de teste se Spring Boot `4` realmente exigir para habilitar `MockMvcTester` no modo desejado

- Politica para testes
  - `TESTING_CONVENTIONS.md` deve refletir o estilo oficialmente suportado na stack nova
  - o codigo de teste deve ser alinhado ao mecanismo realmente disponivel no classpath apos a migracao

- Politica documental
  - `AGENTS.md` e `TESTING_CONVENTIONS.md` devem ser atualizados na mesma feature da migracao
  - nao pode permanecer divergencia entre `pom.xml`, testes e documentacao

## Implementation plan

1. Atualizar `pom.xml` para Java `25` e Spring Boot `4`
2. Ajustar qualquer incompatibilidade de build causada pela migracao
3. Definir e criar `ErrorResponse`
4. Definir e criar um handler minimo para `MethodArgumentNotValidException`
5. Estender o handler para `HttpMessageNotReadableException`
6. Garantir retorno `400` com corpo padronizado para erros de request invalido
7. Manter `CalculatorController` com o mesmo contrato de sucesso
8. Revisar `SumRequest` para confirmar a estrategia final de validacao estrutural
9. Atualizar `AGENTS.md` para Java `25` e Spring Boot `4`
10. Atualizar `TESTING_CONVENTIONS.md` para refletir a stack nova e os nomes `firstAddend` e `secondAddend`
11. Revisar a estrategia de teste HTTP para uso oficial de `MockMvcTester`
12. Escrever testes red phase cobrindo corpo de erro padronizado e migracao de estilo de teste
13. Implementar o minimo necessario para fazer esses testes passarem
14. Executar a suite completa e validar ausencia de regressoes
15. Verificar no Step 06 se os blockers anteriores foram realmente eliminados
