# 02 - High-Level Technical Design

## Architecture Diagram
Não se aplica a mudança de arquitetura, pois o foco é a infraestrutura de build (Maven). A relação de dependências será simplificada.

## Component Responsibilities
- `pom.xml`: Gerenciamento centralizado de dependências.
- `spring-boot-starter-parent`: Fornece gerenciamento de dependências padrão do Spring Boot.
- `spring-boot-starter-test`: Fornece MockMvc, AssertJ, JUnit 5 e Mockito para testes.

## Data Flow
N/A (Mudança apenas em tempo de build/classpath).

## Security and Observability Requirements
- A remoção de dependências não utilizadas reduz a superfície de ataque (menos código de terceiros no classpath).

## Trade-Offs and Alternatives
- **Alternativa:** Manter as dependências atuais. **Trade-off:** Maior tempo de download, classpath mais poluído e potencial para conflitos de versões no futuro.
- **Alternativa:** Usar o plugin `maven-dependency-plugin` para análise automática. **Trade-off:** Fornece bons insights, mas a remoção final deve ser validada manualmente para evitar quebras em runtime.

## High-Level Test Scenario Map
- Validar se o projeto compila após a remoção.
- Validar se os testes de unidade (`CalculatorServiceTest`) passam.
- Validar se os testes de integração de controller (`CalculatorControllerTest`) passam, garantindo que o `MockMvcTester` e as configurações de `MockMvc` ainda funcionam sem o starter redundante.
