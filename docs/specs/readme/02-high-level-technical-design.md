# High-Level Technical Design: README

## Architecture Diagram

```text
README.md
   |
   +-- requisitos e comandos
   +-- endpoints e exemplos curl
   +-- OpenAPI e Swagger UI
   +-- estrutura do projeto
   +-- link para TESTING_CONVENTIONS.md
```

## Component Responsibilities

- `README.md`: ponto de entrada documental para executar, testar e consumir a API.
- `TESTING_CONVENTIONS.md`: fonte detalhada para convencoes de teste, referenciada pelo README.
- Codigo fonte existente: fonte factual para endpoints, DTOs e stack documentados.

## Data Flow

1. Um usuario abre o repositorio.
2. O usuario le o README.
3. O README orienta instalacao, execucao, testes, acesso a OpenAPI e chamadas HTTP.
4. O usuario consulta `TESTING_CONVENTIONS.md` se precisar contribuir com testes.

## Security and Observability Requirements

- Nenhuma mudanca de seguranca.
- Nenhuma mudanca de observabilidade.
- O README deve informar que OpenAPI e Swagger UI estao disponiveis nas rotas padrao quando a aplicacao esta em execucao.

## Trade-Offs and Alternatives

- Preferido: README conciso e pratico, com exemplos essenciais.
- Alternativa rejeitada: README extenso com toda a especificacao de cada endpoint, pois a OpenAPI ja cobre detalhes navegaveis.
- Alternativa rejeitada: duplicar `TESTING_CONVENTIONS.md`, pois isso aumenta risco de divergencia.

## High-Level Test Scenario Map

- Verificar que `README.md` existe.
- Verificar que comandos principais estao documentados.
- Verificar que endpoints da calculadora aparecem.
- Verificar que rotas OpenAPI aparecem.
- Verificar que `TESTING_CONVENTIONS.md` e referenciado.
