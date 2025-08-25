-- Inserir dados na tabela PRODUTO para o datasource 'consulta'
-- Lembre-se que este script será executado no banco de dados de teste (H2 por padrão no Dev Services, a menos que configurado de outra forma)
INSERT INTO PRODUTO(CO_PRODUTO, NO_PRODUTO, PC_TAXA_JUROS, NU_MINIMO_MESES, NU_MAXIMO_MESES, VR_MINIMO, VR_MAXIMO)
VALUES (1, 'Crédito Pessoal', 0.01, 12, 48, 1000.00, 50000.00);

INSERT INTO PRODUTO(CO_PRODUTO, NO_PRODUTO, PC_TAXA_JUROS, NU_MINIMO_MESES, NU_MAXIMO_MESES, VR_MINIMO, VR_MAXIMO)
VALUES (2, 'Crédito Consignado', 0.005, 6, 72, 500.00, 100000.00);