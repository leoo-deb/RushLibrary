# RushLibrary - Gerenciador de Livraria Digital

Sistema de uma livraria digital completo desenvolvido em Java com arquitetura em camadas, banco de dados MySQL e interface de terminal interativa.

## Sobre o Projeto

A RushLibrary é um sistema de gestão de uma livraria, com funcionalidades reais, como: registrar livros, buscar livros, cadastrar cliente, gerir empresas e contratos, etc. Desenvolvido com foco em boas práticas de programação e experiência do usuário.

## Funcionalidades

### Autenticação e Cadastro
- Login seguro com CPF e senha;
- Cadastro completo de novos funcionarios e clientes; e
- Validação de cargos entre funcionário comum e funcionário gerente.

### Gerenciamento de Livro
- Cadastro de novos livros a partir de contratos e categorias já existentes;
- Remoção de livros com confimação do funcionário;
- Consulta de livros por nome ou autor e da lista completa de livros no sistema;
- **Gerenciamento de estoque**:
  - Registros de abastecimentos, emprestimos e retornos de livros;
  - Busca de movimentações baseados em dados do funcionário ou cliente;
  - Listagem das últimos movimentações do sistema.
    

### Gerenciamento de Funcionário / Cliente
- **Cadastro**: Cadastra novos funcionários e clientes à Livraria
- **Remoção**: Remove funcionários ou um clientes a partir de um CPF
- **Atualização**: Atualiza dados de um funcionário ou cliente, como nome e email
- **Busca**: Realiza buscas de funcionários e clientes cadastrados no sistema
- **Listagem**: Lista todos os funcionários ou clientes cadastrados na Livraria

### Gerenciamento de Fornecedores
- **Cadastro**: Cadastra novos contratos de empresas
- **Rescisao**: Reincide contratos de fornecedores ativos
- **Busca**: Realiza buscas de fornecedores
- **Listagem**: Lista todos os fornecedores na livraria

## Tecnologias Utilizadas

- **Java** - Linguagem de programação principal
- **MySQL** - Banco de dados relacional
- **JDBC** - Conectividade com banco de dados
- **MySQL Connector/J 8.3.0** - Driver JDBC para MySQL
- **Git** - Controle de versão

## Arquitetura do Projeto

O projeto utiliza **Arquitetura em Camadas (Layered Architecture)** para separação de responsabilidades:

```
src/
├── dao/                # Camada de Acesso a Dados (Data Access Objects)
│   ├── AuditoriaDAO.java
│   ├── ClienteDAO.java
│   ├── ContradoDAO.java
│   ├── DAO.java
│   ├── EstoqueDAO.java
│   ├── FornecedorDAO.java
│   ├── FuncionarioDAO.java
│   ├── LivroDAO.java
│   └── MovimentacaoDAO.java
│
├── io/                 # Camada de I/O via terminal
│   ├── Input.java
│   └── Output.java
│
├── exceptions/         # Camada de exceções complementares para livraria
│   ├── AcessoNegadoException.java
│   ├── FormatoIncorretoException.java
│   └── FuncionarioExistenteException.java
│
├── model/              # Camada de Modelo (Entidades)
│   ├── Auditoria.java
│   ├── Cliente.java
│   ├── Contrato.java
│   ├── Estoque.java
│   ├── Fornecedor.java
│   ├── Funcionario.java
│   ├── Livro.java
│   └── Movimentacao.java
│
├── service/            # Camada de Serviço (Lógica de Negócio)
│   ├── ClienteService.java
│   ├── FornecedorService.java
│   ├── FuncionarioService.java
│   └── LivroService.java
│
├── ui/               # Camada de Apresentação (Interface)
│   ├── ClienteUI.java
│   ├── ConsoleUI.java
│   ├── EstoqueUI.java
│   ├── FornecedorUI.java
│   ├── FuncionarioUI.java
│   └── LivroUI.java
│
├── util/             # Camada de Utilitários
│   └── ConnectManager.java
│
└── Main.java         # Ponto de entrada da aplicação
```

### Descrição das Camadas

- **DAO**: Responsável pela comunicação direta com o banco de dados
- **Exceptions**: Contém exceções complementares para a estrutura de erros
- **IO**: Responsável pela entrada e saída de textos via terminal
- **Model**: Entidades que representam os dados do sistema
- **Service**: Contém as regras de negócio e validações
- **UI**: Interface com o usuário (menus e interações)
- **Util**: Componentes auxiliares reutilizáveis

## Banco de Dados

O sistema utiliza as seguintes tabelas:

- **Funcionário**: Dados cadastrais dos funcionários
- **Cliente**: Dados cadastrais dos clientes
- **Livro**: Dados cadastrais dos livros
- **Estoque**: Controle de estoque de cada livro
- **Categoria**: Divisões de livros por categorias
- **Contrato**: Gerenciamento de contratos geridos para cada livro a partir de uma empresa
- **Empresa**: Responsável pelos dados dos contratos
- **Registros**: Histórico de operações de livros

## Como Executar

### Pré-requisitos

- Java JDK 8 ou superior
- MySQL Server
- MySQL Connector/J 8.3.0

### Configuração do Banco de Dados

1. Execute o script SQL para criar o banco de dados:
```bash
mysql -u root < database.sql
```

Ou use o PowerShell:
```powershell
Get-Content database.sql | mysql -u root
```

### Compilação

```bash
javac -cp mysql-connector-j-8.3.0.jar -d bin src/**/*.java src/*.java
```

### Execução

```bash
java -cp "bin;mysql-connector-j-8.3.0.jar" Main
```

## Funcionalidades Técnicas

### Segurança
- Validação de operações sobre os cargos entre funcionário comum e gerente
- Validação de CPF único
- Operações com confirmação

### Performance
- Índices otimizados no banco de dados
- Uso de PreparedStatement para prevenir SQL Injection
- ConnectionManager para gerenciamento eficiente de conexões

### Interface
- Menus hierárquicos intuitivos
- Design visual com caracteres especiais (╔═╗║)
- Mensagens de sucesso e erro coloridas
- Navegação por submenus
- Validação de entrada do usuário

## Boas Práticas Implementadas

- Arquitetura em camadas
- Separação de responsabilidades
- Uso de PreparedStatement
- Try-with-resources para gerenciamento de recursos
- Tratamento de exceções
- Código limpo e legível
- Nomenclatura clara de variáveis e métodos
- Foreign keys e constraints no banco de dados

## Atualizações do Projeto

- [x] Construção de toda a estrutura do projeto
- [ ] Implementar sistema de um novo sistema de remoção mais eficiente e mais seguro
- [ ] Realizar testes dos erros e gradualmente tratá-los
- [ ] Testar o projeto com objetivo da procura de erros pendentes
- [ ] Finalização do projeto

## Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

---

⭐ Se este projeto foi útil para você, considere dar uma estrela no repositório!