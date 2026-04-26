# Product Intent Specification

## 1. Problem statement

A aplicacao precisa expor um endpoint HTTP `POST` para receber dois numeros do tipo `double` e retornar o resultado da subtracao entre eles (`a - b`). O objetivo e ampliar a API com uma operacao aritmetica basica adicional, mantendo consistencia de contrato, validacao e formato de resposta ja estabelecidos pela `endpoint-soma`.

## 2. Target users

- Desenvolvedores consumindo a API para operacoes aritmeticas remotas
- Clientes HTTP que precisem calcular a diferenca entre dois valores numericos
- Time de engenharia que quer manter um padrao unico entre endpoints matematicos

## 3. Success metrics

- O endpoint retorna HTTP `200` para requisicoes validas com os dois numeros informados
- O corpo da resposta inclui claramente o resultado numerico da subtracao
- Requisicoes invalidas recebem resposta de erro consistente com HTTP `400`
- Os testes definidos para a borda HTTP e para a logica de negocio passam com sucesso
- O comportamento para valores decimais, zero e resultados negativos fica coberto por testes automatizados

## 4. Out of scope

- Persistencia em banco de dados
- Historico de operacoes
- Autenticacao ou autorizacao
- Suporte a operacoes diferentes de subtracao
- Formatacao especial de numeros alem do comportamento padrao da API
- Tratamento de limites avancados de ponto flutuante alem do que for explicitamente especificado

## 5. Risks and assumptions

- Assumo que o endpoint sera exposto via HTTP JSON, seguindo o padrao existente da aplicacao
- Assumo que o contrato deve aceitar exatamente dois valores numericos `double`
- Ha risco de ambiguidade se o contrato nao explicitar claramente que a operacao e `primeiroNumero - segundoNumero`
- Ha risco de comportamento inesperado com valores especiais de `double`, como `NaN` e infinito, se isso nao for definido no design tecnico
- Como o projeto esta enxuto, sera importante manter implementacao minima para evitar estrutura desnecessaria

## 6. Acceptance criteria

- Cenario 1: subtracao basica com inteiros representaveis em `double`
  Given que o cliente envia dois numeros validos `5.0` e `3.0`
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `2.0`

- Cenario 2: subtracao com valores decimais
  Given que o cliente envia dois numeros validos `7.75` e `2.25`
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `5.5`

- Cenario 3: subtracao com zero
  Given que o cliente envia `9.4` e `0.0`
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `9.4`

- Cenario 4: subtracao com resultado negativo
  Given que o cliente envia `2.0` e `5.5`
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `-3.5`

- Cenario 5: requisicao sem um dos campos obrigatorios
  Given que o cliente envia uma requisicao sem informar um dos dois numeros
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `400`
  And o corpo indica que a entrada enviada e invalida

- Cenario 6: requisicao com valor nao numerico
  Given que o cliente envia um valor que nao pode ser interpretado como numero
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde com HTTP `400`
  And o corpo indica erro de validacao ou formato invalido

- Cenario 7: valores extremos dentro do tipo `double`
  Given que o cliente envia dois valores `double` muito grandes, mas ainda aceitos pelo parse da aplicacao
  When a requisicao e processada pelo endpoint de subtracao
  Then a API responde de forma deterministica conforme as regras definidas no contrato
  And esse comportamento fica explicitado nas etapas de design tecnico
