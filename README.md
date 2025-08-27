# Simulação de crédito

Este projeto é uma API REST para simulação de crédito, desenvolvida com Quarkus. 
A aplicação permite aos usuários simular ofertas de crédito com base no valor desejado e no prazo, 
calculando os resultados pelos sistemas de amortização SAC e Price.

## Pré-requisitos para executar o projeto
WSL (Windows Subsystem for Linux)

Docker e Docker Compose

Em seguida, execute o comando:
- "docker compose up --build"

## Tecnologias usadas

-Foi implementado mecanismo de cache ao buscar produtos e relatorios diários. 
Para evitar o retorno de dados desatualizados também foi adicionado um scheduler que limpa o cache
(o horário pode ser ajustado, nesse caso foi escolhido todo dia à meia-noite) e um endpoint
que limpa o cache caso queira os dados atualizados imediatamente.

-Health check para verificar se a conexão com o banco de dados está ativa.

-Outra tecnologia utilizada foi o scraping de métricas com o prometheus e a utilização 
do grafana para demonstrá-las em gráfico.


## Endpoints
POST http://localhost:8080/api/v1/simulacoes

GET http://localhost:8080/api/v1/simulacoes

GET http://localhost:8080/api/v1/simulacoes/diarias

GET http://localhost:8080/api/v1/telemetria

POST http://localhost:8080/api/v1/cache/clear

http://localhost:8080/q/health/

http://localhost:8080/q/swagger-ui/

http://localhost:3000/dashboards


## Decisões negociais

-Como não foi definido o que salvar no banco, foram usados os dados da simulação price para serem salvas no banco.