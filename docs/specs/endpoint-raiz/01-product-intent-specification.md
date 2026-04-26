# 01 — Product Intent Specification: Endpoint Raiz

## Problem Statement
Implementar um endpoint REST POST `/calculator/root` que calcula a raiz de um `double` dado outro `double`, seguindo o padrão do endpoint de soma e dos endpoints matemáticos existentes.

## Target Users
- Clientes HTTP que precisam realizar cálculos de raiz de forma programática.
- Microsserviços que dependem de operações matemáticas centralizadas.

## Success Metrics
- Endpoint retorna status 200 com `{"result": <valor>}` para entradas válidas.
- Campos obrigatórios ausentes, JSON inválido e tipos incompatíveis retornam status 400 com erro padronizado.
- Operações matematicamente inválidas, como raiz de índice zero ou resultado indefinido/imaginário, retornam erro consistente.
- Testes unitários cobrem a lógica de negócio sem contexto Spring.
- Testes de integração cobrem contrato HTTP com `@SpringBootTest` + `@AutoConfigureMockMvc`.

## Out of Scope
- Suporte a números complexos.
- Suporte a `BigDecimal`.
- Interface de usuário.
- Alteração dos contratos existentes de soma, subtração, multiplicação, divisão ou potenciação.

## Risks and Assumptions
- A raiz será modelada como `radicand^(1 / index)`, usando `Math.pow`.
- `index == 0.0` é inválido porque não existe raiz de índice zero.
- Radicando negativo com índice fracionário pode produzir `NaN`; isso será tratado como operação inválida/indefinida.
- Resultados `Infinity` serão tratados como overflow, preservando o padrão do endpoint de potenciação.

## Acceptance Criteria

- **Cenário: Sucesso com raiz quadrada**
  - **Given:** payload `{"radicand": 9.0, "index": 2.0}`
  - **When:** POST `/calculator/root` é chamado
  - **Then:** status 200 e body `{"result": 3.0}`

- **Cenário: Sucesso com raiz cúbica**
  - **Given:** payload `{"radicand": 27.0, "index": 3.0}`
  - **When:** POST `/calculator/root` é chamado
  - **Then:** status 200 e body `{"result": 3.0}`

- **Cenário: Campo obrigatório ausente**
  - **Given:** payload sem `radicand` ou sem `index`
  - **When:** POST `/calculator/root` é chamado
  - **Then:** status 400 com corpo de erro padronizado

- **Cenário: Índice zero**
  - **Given:** payload `{"radicand": 9.0, "index": 0.0}`
  - **When:** POST `/calculator/root` é chamado
  - **Then:** status 400 Bad Request com mensagem de operação inválida

- **Cenário: Operação indefinida/imaginária**
  - **Given:** payload `{"radicand": -4.0, "index": 2.0}`
  - **When:** POST `/calculator/root` é chamado
  - **Then:** status 400 Bad Request com mensagem "Invalid operation: result is undefined or imaginary"
