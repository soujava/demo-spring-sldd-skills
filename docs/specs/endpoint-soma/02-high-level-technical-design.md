# High-Level Technical Design

## Architecture diagram

```text
HTTP Client
    |
    v
POST /calculator/sum
    |
    v
CalculatorController
    - recebe JSON com firstAddend e secondAddend
    - valida presenca/formato da entrada
    - delega a regra de negocio
    |
    v
CalculatorService
    - executa a soma de dois doubles
    - concentra a logica de negocio
    |
    v
SumResponse
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
  - Expor o endpoint `POST /calculator/sum`
  - Receber o payload JSON da requisicao
  - Acionar validacao de campos obrigatorios `firstAddend` e `secondAddend`
  - Converter a resposta da camada de negocio em JSON HTTP
  - Deixar a regra de soma fora da camada web

- `CalculatorService`
  - Implementar a operacao de soma entre dois `double`
  - Servir como fronteira de logica de negocio testavel por unit tests
  - Manter a implementacao minima, sem dependencias externas

- `SumRequest`
  - Representar o contrato de entrada com os campos `firstAddend` e `secondAddend`
  - Permitir validacao estrutural da requisicao

- `SumResponse`
  - Representar o contrato de saida contendo o resultado da soma

- Infraestrutura Spring MVC
  - Fazer binding JSON -> objeto Java
  - Rejeitar payload invalido com `400`
  - Integrar controller ao ciclo HTTP da aplicacao

## Data flow

- O cliente envia `POST /calculator/sum` com JSON contendo `firstAddend` e `secondAddend`
- O Spring MVC desserializa o corpo da requisicao em `SumRequest`
- Se houver campo ausente, tipo invalido ou corpo malformado, a requisicao falha na borda e a API responde com `400`
- Se a entrada for valida, o `CalculatorController` chama `CalculatorService`
- O `CalculatorService` calcula `firstAddend + secondAddend`
- O `CalculatorController` encapsula o resultado em `SumResponse`
- A API retorna `200` com JSON contendo o valor calculado

## Security and observability requirements

- Nao ha requisito de autenticacao ou autorizacao nesta feature
- O endpoint nao deve executar nada alem da operacao de soma sobre dados recebidos
- A validacao deve impedir processamento de payload estruturalmente invalido
- O contrato deve definir explicitamente o comportamento para valores especiais de `double`, como `NaN` e infinito, antes da implementacao
- Nao ha necessidade de telemetria avancada para esta feature minima
- Logs padrao de erro do Spring sao suficientes para falhas de desserializacao e validacao nesta fase
- O principal mecanismo de confianca sera a cobertura automatizada de testes unitarios e de integracao

## Key trade-offs and alternatives considered

- Metodo HTTP
  - Escolha proposta: `POST /calculator/sum`
  - Motivo: o projeto ja documenta esse formato em `TESTING_CONVENTIONS.md`, e o uso de corpo JSON acomoda claramente `firstAddend` e `secondAddend`
  - Alternativa: `GET` com query parameters
  - Motivo para nao escolher: adiciona ambiguidade desnecessaria e foge do padrao ja exemplificado no projeto

- Nomes de atributos
  - Escolha proposta: `firstAddend` e `secondAddend`
  - Motivo: nomes mais explicitos do que `a` e `b`, melhorando clareza do contrato
  - Alternativa: `a` e `b`
  - Motivo para nao escolher: semantica fraca para API publica

- Controller chamando service vs logica direto no controller
  - Escolha proposta: controller delega a um service
  - Motivo: preserva separacao entre borda HTTP e logica de negocio, alinhado com a convencao de testes em duas camadas
  - Alternativa: somar direto no controller
  - Motivo para nao escolher: dificulta aderencia ao padrao de unit test dedicado para negocio

- Validacao minima vs regras numericas adicionais
  - Escolha proposta: validar presenca e formato dos campos no alto nivel; detalhar `NaN`/infinito no Step 03
  - Motivo: manter o design enxuto sem decidir prematuramente politicas de ponto flutuante
  - Alternativa: rejeitar imediatamente qualquer valor numerico especial
  - Motivo para nao escolher: precisa de definicao explicita de contrato antes do low-level design

- Risco de alinhamento tecnico
  - O guia de testes menciona recursos de Spring Boot 4, mas o `pom.xml` real usa Spring Boot `3.3.4`
  - Isso pode afetar a estrategia exata dos testes de integracao e deve ser resolvido no Step 03 antes do Step 04

## High-level test scenario map

- Happy path
  - `POST /calculator/sum` com `firstAddend` e `secondAddend` positivos
  - Soma com valores decimais
  - Soma com zero
  - Soma com valor negativo

- Failure paths
  - `firstAddend` ausente
  - `secondAddend` ausente
  - Campo com tipo nao numerico
  - Corpo JSON malformado
  - Corpo vazio

- Edge-case families
  - Valores muito grandes ainda aceitos como `double`
  - Resultado com precisao decimal tipica de ponto flutuante
  - Valores especiais como `NaN`, `Infinity` e `-Infinity`, sujeitos a definicao explicita no Step 03
  - Combinacao de numeros positivos e negativos resultando em zero
