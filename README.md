# Sistema de Pagamento

Este projeto é uma implementação de um sistema de pagamentos desenvolvido em Java utilizando Spring Boot, projetado para validar transações de pagamento e gerenciar a comunicação com o AWS SQS.

## Funcionalidades

- **Recepção de Dados**: Recebe informações sobre o vendedor e uma lista de pagamentos.
- **Validação**: Verifica a existência do vendedor e da cobrança.
- **Classificação de Pagamentos**: Identifica se o pagamento é parcial, total ou excedente.
- **Integração com AWS SQS**: Envia mensagens para filas distintas com base no status do pagamento.
- **Tratamento de Erros**: Respostas apropriadas com códigos de status HTTP.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- AWS SDK para Java (SQS)
- JPA/Hibernate para persistência de dados

## Estrutura do Projeto

- **Modelo**: Classes de domínio como `Cobranca`, `Pagamento` e `Vendedor`.
- **Repositórios**: Interfaces que extendem JpaRepository para operações CRUD.
- **Serviços**: Lógica de negócio para processamento de pagamentos.
- **Controladores**: Endpoints REST para interação com o cliente.
- **Tratamento de Exceções**: Manipulação personalizada de erros.
