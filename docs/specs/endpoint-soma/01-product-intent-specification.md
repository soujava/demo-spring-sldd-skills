# Product Intent Specification

## 1. Problem statement

A aplicacao precisa expor um endpoint HTTP simples para receber dois numeros do tipo `double` e retornar o resultado da soma entre eles. O objetivo e disponibilizar uma operacao aritmetica basica pela API, com comportamento previsivel tanto para entradas validas quanto para entradas invalidas, seguindo as convencoes existentes do projeto Spring Boot.

## 2. Target users

- Desenvolvedores consumindo a API para testes ou aprendizado
- Clientes HTTP que precisem executar soma remota de dois valores numericos
- Time de engenharia que quer estabelecer um primeiro endpoint com padrao claro de validacao e resposta

## 3. Success metrics

- O endpoint retorna HTTP `200` para requisicoes validas com os dois numeros informados
- O corpo da resposta inclui claramente o resultado numerico da soma
- Requisicoes invalidas recebem resposta de erro consistente com HTTP `400`
- Os testes definidos para a borda HTTP e para a logica de negocio passam com sucesso
- O comportamento para valores decimais e para zero fica coberto por testes automatizados

## 4. Out of scope

- Persistencia em banco de dados
- Historico de operacoes
- Autenticacao ou autorizacao
- Suporte a operacoes diferentes de soma
- Formatacao especial de numeros alem do comportamento padrao da API
- Tratamento de limites avancados de ponto flutuante alem do que for explicitamente especificado

## 5. Risks and assumptions

- Assumo que o endpoint sera exposto via HTTP JSON, ja que o projeto usa `spring-boot-starter-web`
- Assumo que o contrato deve aceitar exatamente dois valores numericos `double`
- Ha um risco de ambiguidade sobre como representar a entrada: query params ou JSON no corpo
- Ha um risco de comportamento inesperado com valores especiais de `double`, como `NaN` e infinito, se isso nao for definido no design
- Como o projeto ainda esta enxuto, sera importante manter a implementacao minima para nao introduzir estrutura desnecessaria

## 6. Acceptance criteria

- Cenario 1: soma basica com inteiros representaveis em `double`
  Given que o cliente envia dois numeros validos `2.0` e `3.0`
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `5.0`

- Cenario 2: soma com valores decimais
  Given que o cliente envia dois numeros validos `1.5` e `2.25`
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `3.75`

- Cenario 3: soma com zero
  Given que o cliente envia `0.0` e `7.4`
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `7.4`

- Cenario 4: soma com numero negativo
  Given que o cliente envia `-2.5` e `4.0`
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `200`
  And o corpo retorna o resultado `1.5`

- Cenario 5: requisicao sem um dos campos obrigatorios
  Given que o cliente envia uma requisicao sem informar um dos dois numeros
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `400`
  And o corpo indica que a entrada enviada e invalida

- Cenario 6: requisicao com valor nao numerico
  Given que o cliente envia um valor que nao pode ser interpretado como numero
  When a requisicao e processada pelo endpoint de soma
  Then a API responde com HTTP `400`
  And o corpo indica erro de validacao ou formato invalido

- Cenario 7: valores extremos dentro do tipo `double`
  Given que o cliente envia dois valores `double` muito grandes, mas ainda aceitos pelo parse da aplicacao
  When a requisicao e processada pelo endpoint de soma
  Then a API responde de forma deterministica conforme as regras definidas no contrato
  And esse comportamento fica explicitado nas etapas de design tecnico
