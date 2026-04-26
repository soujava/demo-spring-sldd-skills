# Low-Level Design and Version Policy

## API contracts

- Endpoint
  - Metodo: `POST`
  - Path: `/calculator/sum`
  - Content-Type de entrada: `application/json`
  - Content-Type de saida: `application/json`

- Request schema

```json
{
  "firstAddend": 1.5,
  "secondAddend": 2.5
}
```

- Regras do request
  - `firstAddend` e obrigatorio
  - `secondAddend` e obrigatorio
  - Ambos devem ser valores JSON numericos desserializaveis para `Double`
  - Campos extras podem ser ignorados pelo Jackson padrao, sem fazer parte do contrato garantido
  - Corpo vazio ou JSON malformado deve resultar em `400`

- Response schema de sucesso

```json
{
  "result": 4.0
}
```

- Resposta de sucesso
  - Status: `200 OK`

- Error responses
  - `400 Bad Request` para:
    - corpo ausente
    - JSON malformado
    - `firstAddend` ausente
    - `secondAddend` ausente
    - valor nao numerico ou nao desserializavel
  - Corpo minimo de erro:

```json
{
  "error": "Bad Request",
  "message": "Invalid request body"
}
```

- Decisao sobre valores especiais de `double`
  - `NaN`, `Infinity` e `-Infinity` nao fazem parte do contrato suportado
  - Se nao forem aceitos pelo parser JSON padrao, a API responde `400`
  - Nao sera adicionada logica customizada para suportar esses valores

## Data models

- Objeto de entrada

```text
SumRequest
- Double firstAddend
- Double secondAddend
```

- Objeto de saida

```text
SumResponse
- double result
```

- Objeto de dominio/servico

```text
CalculatorService
- double sum(double firstAddend, double secondAddend)
```

- Controlador

```text
CalculatorController
- POST /calculator/sum
- recebe SumRequest
- retorna ResponseEntity<SumResponse> ou SumResponse
```

- Persistencia
  - Nao ha banco de dados
  - Nao ha entidades JPA
  - Nao ha migracoes

## Error model

- Erros esperados na borda HTTP
  - Corpo JSON ausente
  - Corpo JSON malformado
  - Campo obrigatorio ausente
  - Campo com tipo invalido
  - Campo com valor fora do formato numerico aceito pelo binding

- Tratamento
  - Erros de binding/validacao devem resultar em `400 Bad Request`
  - A mensagem de erro deve ser consistente e simples
  - Nao ha necessidade de exception handler global complexo para esta feature
  - Se o comportamento padrao do Spring nao entregar corpo minimamente consistente, pode ser adicionado um handler pequeno e local para padronizar `error` e `message`

- Erros de negocio
  - Nao ha erro de negocio proprio para a operacao de soma simples
  - Overflow de `double` segue a semantica padrao da plataforma Java e nao recebe tratamento especial nesta feature

## Test strategy

- Estrategia geral
  - Seguir a separacao definida em `TESTING_CONVENTIONS.md`
  - Testes unitarios para `CalculatorService`
  - Testes de integracao para `CalculatorController`

- Testes unitarios
  - Local: `src/test/java/com/example/demo/domain/CalculatorServiceTest.java`
  - Sem Spring
  - Instanciacao direta do service
  - Verificar apenas a regra de soma

- Testes de integracao
  - Local: `src/test/java/com/example/demo/controller/CalculatorControllerTest.java`
  - `@SpringBootTest` + `@AutoConfigureMockMvc`
  - Verificar status HTTP e JSON
  - Nao verificar detalhes internos da logica

- Observacao tecnica sobre tooling
  - O documento de convencoes cita `MockMvcTester` e Spring Boot 4
  - O `pom.xml` real usa Spring Boot `3.3.4`
  - A implementacao dos testes deve usar a capacidade realmente disponivel na stack atual
  - Se `MockMvcTester` nao estiver disponivel com a combinacao atual de dependencias, usar `MockMvc` como fallback minimo, mantendo a mesma separacao de camadas

## Test scenario catalog with edge cases

- Unitarios: `CalculatorService`
  - Soma dois positivos: `1.5 + 2.5 = 4.0`
  - Soma decimal: `1.1 + 2.2`
  - Soma com zero: `0.0 + 7.4`
  - Soma com negativo: `-2.5 + 4.0`
  - Soma que resulta em zero: `-5.0 + 5.0`
  - Soma com numeros grandes ainda finitos

- Integracao: sucesso
  - `POST /calculator/sum` com `firstAddend=1.5`, `secondAddend=2.5` retorna `200` e `{"result":4.0}`
  - `POST /calculator/sum` com zero retorna `200`
  - `POST /calculator/sum` com valor negativo retorna `200`

- Integracao: falha
  - Corpo vazio retorna `400`
  - JSON malformado retorna `400`
  - `firstAddend` ausente retorna `400`
  - `secondAddend` ausente retorna `400`
  - `firstAddend` como string nao numerica retorna `400`
  - `secondAddend` como boolean retorna `400`
  - Payload com ambos os campos ausentes retorna `400`

- Edge cases detalhados
  - Payload com campos extras nao deve quebrar o endpoint
  - Payload com numeros muito grandes, mas ainda desserializaveis, deve responder deterministicamente
  - Payload com precisao decimal longa deve refletir semantica padrao de `double`
  - Nao ha retries, filas ou concorrencia relevante para esta feature sincrona e stateless
  - Nao ha payload grande relevante; o corpo esperado e pequeno e fixo
  - Nao ha necessidade de testes de concorrencia dedicados nesta etapa

## Dependency/version policy

- Fonte da verdade
  - O `pom.xml` atual e a fonte de verdade para versoes efetivamente suportadas no projeto
  - `AGENTS.md` e `TESTING_CONVENTIONS.md` devem ser tratados como documentacao sujeita a correcao posterior

- Framework
  - Manter Spring Boot `3.3.x`, alinhado ao major atualmente configurado no projeto
  - Nao introduzir dependencias exclusivas de Spring Boot 4 nesta feature sem antes atualizar oficialmente a stack do projeto
  - `spring-boot-starter-web` e `spring-boot-starter-validation` permanecem como dependencias principais
  - `spring-boot-starter-test` permanece para testes

- Runtime
  - Manter Java `23` porque e o runtime declarado no `pom.xml`
  - Politica recomendada: permanecer em uma linha de runtime suportada e configurada explicitamente no build
  - Se o time desejar endurecer a politica para LTS, isso deve ser tratado como mudanca separada da feature

- Test tooling
  - Preferir os recursos suportados pela stack atual do Spring Boot `3.3.4`
  - Se `MockMvcTester` nao estiver de fato disponivel, usar `MockMvc` sem alterar a intencao dos testes
  - Nao adicionar bibliotecas de teste extras sem necessidade concreta

## Implementation plan

1. Criar o pacote de dominio ou servico com `CalculatorService`
2. Implementar o metodo `sum(double firstAddend, double secondAddend)`
3. Criar o modelo de entrada `SumRequest` com `firstAddend` e `secondAddend`
4. Definir validacao estrutural para garantir campos obrigatorios
5. Criar o modelo de saida `SumResponse` com `result`
6. Criar `CalculatorController` com endpoint `POST /calculator/sum`
7. Integrar controller ao service para executar a soma
8. Garantir resposta `200` com JSON `{"result": ...}` no fluxo feliz
9. Garantir resposta `400` para corpo ausente, malformado e campos invalidos
10. Adicionar padronizacao minima de erro se o comportamento padrao do Spring nao atender ao contrato definido
11. Escrever testes unitarios de `CalculatorService`
12. Escrever testes de integracao de `CalculatorController` para fluxo feliz
13. Escrever testes de integracao para erros de contrato HTTP
14. Executar a suite de testes e ajustar a implementacao minima ate tudo passar
15. Revisar se a documentacao de testes do projeto precisa de ajuste posterior por divergencia de versoes, sem misturar essa correcao com a feature
