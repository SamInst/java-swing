# Cadastro de Funcionários

> Aplicação desktop em Java Swing para gerenciamento de funcionários, desenvolvida por Sam Helson.

---
🔗 Links Úteis
---
 - Download do sdk do java 21: https://www.oracle.com/br/java/technologies/downloads/#java21
 - Tutorial de uso do app: https://www.youtube.com/watch?v=rnbj1c3b0gI&ab_channel=SamH.
 - Link para baixar o app: https://drive.google.com/file/d/1wdfvxvp794cSjgPSs29JQGb42ZDpM7XG/view?usp=drive_link 
ou pegue ele no diretorio `java-swing/out/artifacts/java_swing_jar`
 - 

## 🔌 Carregar Banco de Dados do Aplicativo

O banco de dados do app já vem pré-configurado com as seguintes variáveis de conexão:

```properties
url = jdbc:postgresql://localhost:5432/saam_db
user = postgres
password = 1234
```

## 🚀 Visão Geral

O **Cadastro de Funcionários** é um sistema simples e intuitivo para:

- **Registrar** novos funcionários.
- **Listar** e **remover** funcionários cadastrados.
- **Autenticar** usuários via tela de login.
- Exibir dados em tabela estilizada com ações diretas (edição, remoção).

Foi construído em **Java 21** usando componentes Swing e diversas bibliotecas para aprimorar a experiência do usuário.

---

## 🛠️ Tecnologias e Bibliotecas

### Linguagem e Plataforma
- **Java 21**
- **Swing** (UI Toolkit)

### Gerenciamento de Layout e Tema
- **FlatLaf 3.2.5** (`flatlaf-3.2.5.jar`, `flatlaf-extras-3.2.5.jar`, `flatlaf-fonts-roboto-2.137.jar`)
- **MigLayout 4.0** (`miglayout-4.0.jar`)

### Componentes e Efeitos
- **TimingFramework 0.55** (`TimingFramework-0.55.jar`) para animações suaves
- **GlassPanePopup 1.3.0** (`swing-glasspane-popup-1.3.0.jar`) para popups customizados

### Banco de Dados
- **PostgreSQL** via **JDBC**
- Arquivo `scripts.sql` contém instruções de criação e populações iniciais

---

## ⚙️ Configuração do Banco de Dados

Execute o arquivo `scripts.sql` no seu ambiente PostgreSQL para criar o banco e tabelas:

```sql
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
INSERT INTO usuario (usuario, email, senha) VALUES ('admin','admin','admin');
```

---

## 🎯 Funcionalidades Principais

1. **Cadastro de Usuário e Login**
    - Tela de registro com validação de campos (`nome`, `email`, `senha`).
    - Criptografia de senha com SHA-256 antes de salvar.
    - Tela de login com autenticação via banco de dados.

2. **Cadastro de Funcionários**
    - Formulário com `nome`, `data de admissão`, `salário` e `status`.
    - Validação de data via **DatePicker** customizado.
    - Máscara de valor para o salário.

3. **Listagem e Ações**
    - **JTable** estilizada para exibir registros.
    - Botões de **remoção** e confirmações via popup.
    - Seleção de status diretamente na tabela.

4. **Interface e Usabilidade**
    - Tema moderno com **FlatLaf**.
    - **GlassPanePopup** para mensagens elegantes.
    - Animações suaves com **TimingFramework**.
    - **MigLayout** para organização flexível de componentes.

---


```

---

## 🚀 Como Executar

1. Clone este repositório.
2. Importe no seu IDE (NetBeans ou IntelliJ) como projeto Maven.
3. Configure as dependências no `pom.xml` ou adicione os JARs manualmente.
4. Execute `scripts.sql` no seu Postgres.
5. Ajuste as credenciais de conexão em `application.properties` (ou classe de conexão).
6. Rode a classe `CadastroFuncionario`.

---
```
---
## ✒️ Autor

**Sam Helson**  
Desenvolvedor Java e entusiasta de UX em aplicações desktop.


---

