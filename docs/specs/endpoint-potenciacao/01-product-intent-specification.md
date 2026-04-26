# 01 — Product Intent Specification: Endpoint Potenciação

## Problem Statement
Implementar um endpoint REST POST `/calculator/power` que calcula a potência de uma base elevada a um expoente utilizando o tipo `Double`, garantindo robustez no tratamento de limites numéricos e erros matemáticos.

## Target Users
- Clientes HTTP que precisam realizar cálculos de potência de forma programática.
- Microsserviços que dependem de operações matemáticas centralizadas.

## Success Metrics
- Endpoint retorna o resultado exato (dentro da precisão do `Double`) com status 200.
- Falhas de validação de entrada (campos ausentes ou `NaN`) retornam status 400.
- Resultados que excedem o limite do sistema (overflow) retornam status 422.

## Out of Scope
- Operações com números complexos (resultados imaginários).
- Suporte a `BigDecimal` (mantendo o padrão dos endpoints de multiplicação/divisão existentes).
- Interface de usuário (UI).

## Risks and Assumptions
- **Overflow:** A potenciação cresce exponencialmente, atingindo `Infinity` rapidamente. O sistema deve detectar e reportar isso ao invés de retornar `Infinity` no JSON.
- **Underflow:** Resultados muito próximos de zero serão tratados como `0.0` (comportamento padrão de ponto flutuante).

## Acceptance Criteria

- **Cenário: Sucesso**
  - **Given:** payload `{"base": 2.0, "exponent": 3.0}`
  - **When:** POST `/calculator/power` é chamado
  - **Then:** status 200 e body `{"result": 8.0}`

- **Cenário: Entrada Inválida (Validation)**
  - **Given:** payload `{"base": NaN, "exponent": 2.0}` ou campo ausente
  - **When:** POST `/calculator/power` é chamado
  - **Then:** status 400 Bad Request

- **Cenário: Overflow**
  - **Given:** payload que excede o limite do Double `{"base": 10.0, "exponent": 1000.0}`
  - **When:** POST `/calculator/power` é chamado
  - **Then:** status 422 Unprocessable Entity com mensagem "Numeric overflow: result is too large"

- **Cenário: Operação Indefinida/Imaginária**
  - **Given:** base negativa e expoente fracionário `{"base": -4.0, "exponent": 0.5}`
  - **When:** POST `/calculator/power` é chamado
  - **Then:** status 400 Bad Request com mensagem "Invalid operation: result is undefined or imaginary"
