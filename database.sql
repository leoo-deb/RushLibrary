CREATE DATABASE RushLibrary;
USE RushLibrary;

CREATE TABLE Livro (
id_livro INT PRIMARY KEY AUTO_INCREMENT,
nome_livro VARCHAR(20) NOT NULL UNIQUE,
sinopse_livro VARCHAR(200) NOT NULL,
autor_livro VARCHAR(20) NOT NULL,
isbn_livro BIGINT NOT NULL,
genero_livro VARCHAR(50) NOT NULL,
tipo_livro ENUM('FISICO', 'DIGITAL', 'EBOOK') DEFAULT 'FISICO',

id_fornc INT NOT NULL,

FOREIGN KEY (id_fornc) REFERENCES Fornecedor(id_fornc)
);

CREATE TABLE Contrato(
codigo_contrato INT PRIMARY KEY AUTO_INCREMENT,
tipo_contrato VARCHAR(30) NOT NULL,
status_contrato ENUM('VIGENTE', 'VENCIDO', 'RESCINDIDO') DEFAULT 'VIGENTE',
vigencia_contrato DATE,
vencimento_contrato DATE
);

CREATE TABLE Fornecedor (
id_fornc INT PRIMARY KEY AUTO_INCREMENT,
nome_empresa VARCHAR(30) NOT NULL,
titular_empresa VARCHAR(30) NOT NULL,
cnpj_empresa VARCHAR(50) NOT NULL,
codigo_contrato INT NOT NULL,

FOREIGN KEY (codigo_contrato) REFERENCES Contrato(codigo_contrato)
);

CREATE TABLE Estoque (
id_estoque INT PRIMARY KEY AUTO_INCREMENT,
id_livro INT NOT NULL,
qunt_estoque INT NOT NULL,

FOREIGN KEY (id_livro) REFERENCES Livro(id_livro)
);

CREATE TABLE Funcionario (
id_func INT PRIMARY KEY AUTO_INCREMENT,
nome_func VARCHAR(45) NOT NULL,
cpf_func VARCHAR(25) NOT NULL UNIQUE,
email_func VARCHAR(100) NOT NULL UNIQUE,
senha_func VARCHAR(8) NOT NULL,
cargo_func ENUM('GERENTE', 'COMUM') DEFAULT 'COMUM',
dataadimissao_func DATE,
ultimo_acesso_func TIMESTAMP DEFAULT NULL
);

CREATE TABLE Cliente (
id_cli INT PRIMARY KEY AUTO_INCREMENT,
nome_cli VARCHAR(45) NOT NULL,
cpf_cli VARCHAR(25) NOT NULL UNIQUE,
email_cli VARCHAR(100) NOT NULL UNIQUE,
numero_cli VARCHAR(25) NOT NULL UNIQUE,
data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
quantidade_livro INT DEFAULT '0'
);

CREATE TABLE Movimentacao (
id_movi INT PRIMARY KEY AUTO_INCREMENT,
id_estoque INT,
id_func INT,

quant_movi INT,
tipo_movi ENUM('ENTRADA_COMPRA', 'SAIDA_EMPRESTIMO', 'RETORNO_EMPRESTIMO', 'REMOCAO'),
data_movi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

id_cli INT DEFAULT NULL,
dataprevista_reg DATE DEFAULT NULL,
dataentrega_reg DATE DEFAULT NULL,
status_entrega ENUM('ABERTO', 'DEVOLVIDO', 'ATRASADO', 'CONCLUIDO') DEFAULT 'CONCLUIDO',

FOREIGN KEY (id_estoque) REFERENCES Estoque(id_estoque),
FOREIGN KEY (id_func) REFERENCES Funcionario(id_func),
FOREIGN KEY (id_cli) REFERENCES Cliente(id_cli)
);

CREATE TABLE Auditoria (
id_aud INT PRIMARY KEY AUTO_INCREMENT,
tipo_aud ENUM('CADASTRO_CLIENTE', 'CADASTRO_FUNCIONARIO', 'REGISTRO_FORNECEDOR', 'ENTRADA_FUNCIONARIO'),
id_func INT NOT NULL,
id_tipo INT NOT NULL,
data_aud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

FOREIGN KEY (id_func) REFERENCES Funcionario(id_func)
);