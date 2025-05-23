-- Criando banco de dados
CREATE DATABASE saam_db;

-- Cria a tabela funcionario se não existir
CREATE TABLE IF NOT EXISTS funcionario (
id INT8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) PRIMARY KEY,
nome VARCHAR(255) NOT NULL,
data_admissao TIMESTAMP NOT NULL,
salario FLOAT NOT NULL,
status BOOLEAN NOT NULL
);

-- Cria a tabela usuario se não existir
CREATE TABLE IF NOT EXISTS usuario (
id INT8 GENERATED ALWAYS AS IDENTITY(INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) PRIMARY KEY,
usuario VARCHAR(255) NOT NULL UNIQUE,
email VARCHAR(255) NOT NULL UNIQUE,
senha VARCHAR(255) NOT NULL
);

-- populando com 5 registros;
INSERT INTO funcionario (nome, data_admissao, salario, status) VALUES
('João Silva', '2022-03-15 08:30:00', 4500.50, TRUE),
('Maria Oliveira', '2021-11-10 09:15:00', 5200.75, TRUE),
('Carlos Santos', '2023-01-22 10:00:00', 3800.25, TRUE),
('Ana Pereira', '2022-07-05 08:00:00', 4100.00, FALSE),
('Paulo Rodrigues', '2023-05-18 13:45:00', 4750.80, TRUE);

-- usuario admin para testes
insert into usuario (usuario, email, senha) VALUES ('admin','admin','admin');
