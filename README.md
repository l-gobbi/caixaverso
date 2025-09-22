# Desafio Backend Quarkus - API de Produtos de Empréstimo com Cálculo de Juros

Este projeto é uma API RESTful desenvolvida em Quarkus, com o objetivo de gerenciar produtos de empréstimo e realizar simulações de financiamento. A aplicação permite o cadastro de diferentes tipos de empréstimos, cada um com suas taxas de juros e prazos, além de oferecer um endpoint para simulações detalhadas.

## Objetivo do desafio

O principal objetivo é criar uma API que ofereça as seguintes funcionalidades:

1.  **CRUD de Produtos de Empréstimo:** Permitir a criação, leitura, atualização e exclusão de produtos de empréstimo.
2.  **Simulação de Empréstimo:** Oferecer um endpoint para simular o financiamento com base em um produto, valor solicitado e prazo desejado.

## Tecnologias Utilizadas

* **Java 17:** Linguagem de programação principal.
* **Quarkus:** Framework Java para desenvolvimento de aplicações nativas em nuvem.
* **Hibernate ORM com Panache:** Para a camada de persistência de dados.
* **H2 Database:** Banco de dados em memória para os ambientes de desenvolvimento e teste.
* **JUnit 5 e Mockito:** Para a escrita de testes unitários.
* **SmallRye OpenAPI:** Para a geração da documentação da API no padrão Swagger.
* **Maven:** Para o gerenciamento de dependências e do ciclo de vida do projeto.

## Funcionalidades

### 1. CRUD de Produtos de Empréstimo

A API oferece um conjunto completo de endpoints para o gerenciamento de produtos.

* `GET /api/v1/produtos`: Retorna a lista de todos os produtos cadastrados.
* `GET /api/v1/produtos/{id}`: Busca um produto específico pelo seu ID.
* `POST /api/v1/produtos`: Cria um novo produto de empréstimo.
* `PUT /api/v1/produtos/{id}`: Atualiza as informações de um produto existente.
* `DELETE /api/v1/produtos/{id}`: Remove um produto do sistema.

### 2. Simulação de Empréstimo

Endpoint para simular um empréstimo com base nos dados fornecidos.

* `POST /api/v1/simulacoes`: Realiza o cálculo detalhado do empréstimo, retornando o valor das parcelas, o total com juros e a memória de cálculo mês a mês.

## Como Executar

### Pré-requisitos

* JDK 17 ou superior
* Apache Maven 3.8.2 ou superior

### Executando em modo de desenvolvimento

Para iniciar a aplicação em modo de desenvolvimento, execute o seguinte comando:

```bash
./mvnw quarkus:dev