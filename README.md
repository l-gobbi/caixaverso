# Simulação de crédito

Este projeto é uma API REST para simulação de crédito, desenvolvida com Quarkus. 
A aplicação permite aos usuários simular ofertas de crédito com base no valor desejado e no prazo, 
calculando os resultados pelos sistemas de amortização SAC e Price.

## Pré-requisitos para executar o projeto
WSL (Windows Subsystem for Linux)

Docker e Docker Compose

Em seguida, execute o comando:
- "docker compose up --build"

## Funcionalidades principais

- Criação de Simulações: Endpoint para criar novas simulações de crédito.

- Listagem de Simulações: Consulta paginada de todas as simulações realizadas.

- Relatórios Diários: Agregação de dados das simulações por dia.

- Telemetria: Métricas de performance dos endpoints.

- Mecanismo de cache ao buscar produtos e relatorios diários. 
Para evitar o retorno de dados desatualizados também foi adicionado um scheduler que limpa o cache
(o horário pode ser ajustado, nesse caso foi escolhido todo dia à meia-noite) e um endpoint
que limpa o cache caso queira os dados atualizados imediatamente.

- Health Check: Verificação da saúde das conexões com os bancos de dados.

- Scraping de métricas com o prometheus e a utilização 
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


## 📝 Decisões de Design

- Persistência de Dados: Como não foi definido nos requisitos do desafio, para o armazenamento das simulações, optou-se por salvar os dados calculados com base no sistema de amortização Price.


- Cache: Foi implementado um cache para as consultas de produtos e relatórios diários para melhorar a performance. Um scheduler foi configurado para limpar o cache diariamente à meia-noite (0 0 0 * * ?), garantindo que os dados não fiquem desatualizados. Um endpoint de limpeza manual também foi disponibilizado para invalidação imediata.