# Low-Level Design and Version Policy

## API contracts

- Endpoint
  - Metodo: `POST`
  - Path: `/calculator/subtract`
  - Content-Type entrada: `application/json`
  - Content-Type saida: `application/json`

- Request schema

```json
{
  "minuend": 7.75,
  "subtrahend": 2.25
}
```

- Regras do request
  - `minuend` e obrigatorio
  - `subtrahend` e obrigatorio
  - Ambos devem ser numericos e desserializaveis para `Double`
  - Corpo vazio ou JSON malformado deve retornar `400`
  - Campos extras podem ser ignorados pelo parser padrao, sem garantia contratual adicional

- Response schema de sucesso

```json
{
  "result": 5.5
}
```

- Respostas de erro
  - `400 Bad Request` para corpo ausente, malformado, campo ausente ou tipo invalido
  - Corpo minimo de erro (se precisar padronizacao explicita)

```json
{
  "error": "Bad Request",
  "message": "Invalid request body"
}
```

- Politica para valores especiais de ponto flutuante
  - `NaN`, `Infinity` e `-Infinity` nao fazem parte do contrato suportado
  - Se entrada nao for parseavel em JSON numerico valido, retorna `400`
  - Sem logica custom para aceitar representacoes especiais

## Data models

- Objeto de entrada

```text
SubtractRequest
- Double minuend
- Double subtrahend
```

- Objeto de saida

```text
SubtractResponse
- double result
```

- Servico de dominio

```text
CalculatorService
- double subtract(double minuend, double subtrahend)
```

- Controlador HTTP

```text
CalculatorController
- POST /calculator/subtract
- recebe SubtractRequest
- retorna SubtractResponse (ou ResponseEntity<SubtractResponse>)
```

- Persistencia
  - Nao ha banco de dados
  - Nao ha entidades JPA
  - Nao ha migracoes

## Error model

- Erros de borda HTTP esperados
  - JSON ausente
  - JSON malformado
  - `minuend` ausente
  - `subtrahend` ausente
  - `minuend`/`subtrahend` com tipo nao numerico

- Tratamento
  - Falhas de binding/validacao retornam `400 Bad Request`
  - Mensagem de erro deve ser consistente e simples
  - Sem handler global complexo; usar comportamento padrao do Spring
  - Se necessario para consistencia do contrato, criar handler minimo local

- Erros de negocio
  - Nao ha regra de erro de negocio adicional para subtracao simples
  - Resultado segue semantica nativa de `double` no Java (sem politica extra nesta feature)

## Test strategy

- Seguir convencao do projeto (duas camadas, sem misturar)
  - Unitario: logica de negocio pura, sem Spring
  - Integracao: borda HTTP com Spring completo + `@SpringBootTest` + `@AutoConfigureMockMvc`

- Unit tests (`CalculatorService`)
  - Validar apenas `minuend - subtrahend`
  - Cobrir inteiro, decimal, zero e resultado negativo

- Integration tests (`CalculatorController`)
  - Validar status HTTP
  - Validar JSON de resposta
  - Cobrir sucesso e invalidacoes de contrato

- Execucao
  - `./mvnw test` como gate de conclusao da implementacao

## Test scenario catalog with edge cases

- Unitarios (negocio)
  - `5.0 - 3.0 = 2.0`
  - `7.75 - 2.25 = 5.5`
  - `9.4 - 0.0 = 9.4`
  - `2.0 - 5.5 = -3.5`
  - `5.5 - 5.5 = 0.0`
  - Numeros grandes ainda finitos

- Integracao (sucesso)
  - `POST /calculator/subtract` valido retorna `200` e `{ "result": ... }`
  - Payload decimal retorna `200`
  - Payload com resultado negativo retorna `200`

- Integracao (falha)
  - Corpo vazio retorna `400`
  - JSON malformado retorna `400`
  - Campo `minuend` ausente retorna `400`
  - Campo `subtrahend` ausente retorna `400`
  - `minuend` string invalida retorna `400`
  - `subtrahend` boolean retorna `400`

- Fronteiras e operacao
  - Payload com campos extras nao deve quebrar endpoint
  - Precisao decimal segue comportamento padrao de `double`
  - Payloads grandes: nao aplicavel (contrato pequeno e fixo)
  - Retries: nao aplicavel no endpoint sincrono/stateless
  - Concorrencia: sem requisito especifico; endpoint deve permanecer stateless sob chamadas paralelas

## Dependency/version policy

- Fonte de verdade: `pom.xml`
  - Spring Boot: `4.0.5`
  - Java: `25`

- Politica de framework
  - Permanecer no major configurado e suportado ativamente pelo projeto (`4.x`)
  - Nao introduzir dependencias fora do ecossistema Spring Boot sem necessidade
  - Manter `spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-test`, `spring-boot-starter-webmvc-test`

- Politica de runtime
  - Manter Java `25` (linha declarada no build)
  - Mudanca de runtime/major do framework deve ser tratada separadamente desta feature

- Politica de testes
  - Usar anotacoes e utilitarios da stack atual (Spring Boot 4 + webmvc-test)
  - Nao adicionar biblioteca de teste extra sem ganho objetivo para este escopo

## Implementation plan

1. Criar `SubtractRequest` com `minuend` e `subtrahend`.
2. Definir validacoes estruturais de campos obrigatorios no request.
3. Criar `SubtractResponse` com campo `result`.
4. Adicionar metodo `subtract(double minuend, double subtrahend)` em `CalculatorService`.
5. Implementar regra minima de subtracao no service.
6. Expor endpoint `POST /calculator/subtract` em `CalculatorController`.
7. Conectar controller ao service, retornando `SubtractResponse`.
8. Garantir retorno `200` no fluxo feliz com JSON esperado.
9. Garantir retorno `400` para corpo ausente/malformado/campos invalidos.
10. Se necessario, adicionar padronizacao minima de erro para consistencia do contrato.
11. Escrever testes unitarios para `CalculatorService` (sem Spring).
12. Escrever testes de integracao para sucesso HTTP no controller.
13. Escrever testes de integracao para falhas de contrato (`400`).
14. Rodar `./mvnw test` e ajustar implementacao minima ate todos os testes passarem.
15. Confirmar aderencia final ao contrato definido nesta etapa antes de iniciar Step 04.
