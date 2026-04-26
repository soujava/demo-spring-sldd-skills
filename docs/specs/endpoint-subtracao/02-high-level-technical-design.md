# High-Level Technical Design

## Architecture diagram

```text
HTTP Client
    |
    v
POST /calculator/subtract
    |
    v
CalculatorController
    - recebe JSON com minuend e subtrahend
    - valida presenca/formato da entrada
    - delega a regra de negocio
    |
    v
CalculatorService
    - executa a subtracao entre dois doubles
    - concentra a logica de negocio
    |
    v
SubtractResponse
    - estrutura de resposta JSON com o resultado

Fluxo de erro:
HTTP Client
    |
    v
CalculatorController / camada Spring MVC
    |
    v
Validacao / desserializacao falha
    |
    v
HTTP 400 Bad Request
```

## Component responsibilities

- `CalculatorController`
  - Expor o endpoint `POST /calculator/subtract`
  - Receber o payload JSON da requisicao
  - Acionar validacao de campos obrigatorios `minuend` e `subtrahend`
  - Converter a resposta da camada de negocio em JSON HTTP
  - Manter a regra de subtracao fora da camada web

- `CalculatorService`
  - Implementar a operacao `minuend - subtrahend` entre `double`
  - Servir como fronteira de logica de negocio testavel por unit tests
  - Manter implementacao minima, sem dependencias externas

- `SubtractRequest`
  - Representar o contrato de entrada com campos `minuend` e `subtrahend`
  - Permitir validacao estrutural da requisicao

- `SubtractResponse`
  - Representar o contrato de saida com o resultado da subtracao

- Infraestrutura Spring MVC
  - Fazer binding JSON -> objeto Java
  - Rejeitar payload invalido com `400`
  - Integrar controller ao ciclo HTTP da aplicacao

## Data flow

- O cliente envia `POST /calculator/subtract` com JSON contendo `minuend` e `subtrahend`
- O Spring MVC desserializa o corpo da requisicao em `SubtractRequest`
- Se houver campo ausente, tipo invalido ou corpo malformado, a requisicao falha na borda e a API responde com `400`
- Se a entrada for valida, o `CalculatorController` chama `CalculatorService`
- O `CalculatorService` calcula `minuend - subtrahend`
- O `CalculatorController` encapsula o resultado em `SubtractResponse`
- A API retorna `200` com JSON contendo o valor calculado

## Security and observability requirements

- Nao ha requisito de autenticacao ou autorizacao nesta feature
- O endpoint deve executar apenas operacao aritmetica sobre entrada recebida
- A validacao deve impedir processamento de payload estruturalmente invalido
- O contrato deve definir o comportamento para `NaN` e infinito antes da implementacao
- Logs padrao do Spring para erro de binding/validacao sao suficientes nesta etapa
- Confiabilidade sera sustentada por testes unitarios (negocio) e integracao (borda HTTP)

## Key trade-offs and alternatives considered

- Metodo HTTP
  - Escolha: `POST /calculator/subtract`
  - Motivo: consistencia com `endpoint-soma` e contrato JSON claro
  - Alternativa: `GET` com query params
  - Nao escolhido: reduz consistencia e clareza do contrato atual

- Nomes de campos
  - Escolha: `minuend` e `subtrahend`
  - Motivo: semantica explicita para subtracao (`minuend - subtrahend`)
  - Alternativa: `a` e `b`
  - Nao escolhido: baixa clareza em API publica

- Separacao controller/service
  - Escolha: controller delega para service
  - Motivo: alinhamento com convencao de testes em duas camadas
  - Alternativa: logica direta no controller
  - Nao escolhido: enfraquece isolamento da logica de negocio

- Politica de numeros especiais
  - Escolha: definir no Step 03
  - Motivo: evita decisao prematura no alto nivel
  - Alternativa: rejeitar de imediato `NaN`/`Infinity`
  - Nao escolhido: depende de contrato detalhado low-level

## High-level test scenario map

- Happy path
  - `POST /calculator/subtract` com numeros positivos
  - Subtracao com decimais
  - Subtracao com zero
  - Subtracao com resultado negativo

- Failure paths
  - `minuend` ausente
  - `subtrahend` ausente
  - Campo com tipo nao numerico
  - JSON malformado
  - Corpo vazio

- Edge-case families
  - Valores muito grandes aceitos como `double`
  - Precisao decimal tipica de ponto flutuante
  - Valores especiais `NaN`, `Infinity`, `-Infinity` (sujeitos a definicao no Step 03)
  - Combinacao que resulta exatamente em `0.0` (ex.: `5.5 - 5.5`)
