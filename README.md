# Simulação de crédito

Este projeto é uma API REST para simulação de crédito, desenvolvida com Quarkus. 
A aplicação permite aos usuários simular ofertas de crédito com base no valor desejado e no prazo, 
calculando os resultados pelos sistemas de amortização SAC e Price.

## Pré-requisitos para executar o projeto
[WSL (Windows Subsystem for Linux)](https://learn.microsoft.com/pt-br/windows/wsl/install)

[Docker](https://docs.docker.com/get-started/get-docker/) e [Docker Compose](https://docs.docker.com/compose/install/)

Para rodar o projeto, execute o comando:
- "docker compose up --build"

## Tecnologias Utilizadas
- Framework: Quarkus

- Linguagem: Java 17

- Banco de Dados: PostgreSQL (para simulações) e Microsoft SQL Server (para consulta de produtos)

- Mensageria: Azure Event Hubs

- Monitoramento: Prometheus e Grafana

- Contêineres: Docker e Docker Compose

- Cache: Quarkus Cache

- Rate Limiting: Bucket4j

## Funcionalidades principais

- Criação de Simulações: Endpoint para criar novas simulações de crédito.

- Listagem de Simulações: Consulta paginada de todas as simulações realizadas.

- Relatórios Diários: Agregação de dados das simulações por dia.

- Telemetria: Métricas de performance dos endpoints.

- Cache: Mecanismo de cache para otimizar a busca de produtos e relatórios diários. 
 - Limpeza Agendada: Um scheduler limpa o cache diariamente à meia-noite para evitar dados desatualizados.
 - Limpeza Manual: Um endpoint permite a limpeza imediata do cache.

- Health Check: Verificação da saúde das conexões com os bancos de dados.

- Rate Limiting: Proteção contra um número excessivo de requisições.

- Métricas: Coleta de métricas com Prometheus e visualização em dashboards do Grafana.



## Endpoints
POST http://localhost:8080/api/v1/simulacoes

GET http://localhost:8080/api/v1/simulacoes

GET http://localhost:8080/api/v1/simulacoes/diarias

GET http://localhost:8080/api/v1/telemetria

POST http://localhost:8080/api/v1/cache/clear

http://localhost:8080/q/health/

http://localhost:8080/q/swagger-ui/

http://localhost:3000/dashboards


## 📝 Decisões de Design

- Persistência de Dados: Como não foi definido nos requisitos do desafio, para o armazenamento das simulações, optou-se por salvar os dados calculados com base no sistema de amortização Price.


- Cache: Foi implementado um cache para as consultas de produtos e relatórios diários para melhorar a performance. Um scheduler foi configurado para limpar o cache diariamente à meia-noite (0 0 0 * * ?), garantindo que os dados não fiquem desatualizados. Um endpoint de limpeza manual também foi disponibilizado para invalidação imediata.