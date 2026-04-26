# 01 — Product Intent Specification

## Problem Statement

Implementar um endpoint REST POST `/calculator/multiply` que calcula o produto de dois numeros decimais (doubles) com precisao e robustez adequadas.

## Target Users

- Clientes HTTP que precisam realizar operacoes de multiplicacao via API
- Microsservicos que delegam calculos matematicos a este servico

## Success Metrics

- Endpoint responde com codigo 200 e resultado correto para entrada valida
- Erros de validacao retornam 400 com corpo de erro padronizado
- Tratamento consistente de edge cases (NaN, Infinity, zero, valores negativos)

## Out of Scope

- Operacoes com mais de dois operandos
- Persistencia de dados
- Autenticacao/Autorizacao (ja coberto por infrastructure)

## Risks and Assumptions

- Usuarios podem enviar valores que causam overflow/underflow
- Precisao de ponto flutuante pode causar resultados aproximados

## Acceptance Criteria

```
Given: um cliente com payload valido {"multiplicand": 3.0, "multiplier": 2.0}
When: POST /calculator/multiply e chamado
Then: resposta 200 com {"result": 6.0}

Given: um cliente com payload {"multiplicand": NaN, "multiplier": 2.0}
When: POST /calculator/multiply e chamado
Then: resposta 400 com erro padrao

Given: um cliente com payload invalido (campo ausente)
When: POST /calculator/multiply e chamado
Then: resposta 400 com corpo de erro padronizado
```