package ui;

import model.Cliente;
import model.Funcionario;
import services.ClienteService;
import java.util.List;
import static io.Input.reader;
import static io.Output.write;

public record ClienteUI(ClienteService clienteService) {

    public void gerenciarCliente(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuGerenciamentoCliente();
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) cadastrarCliente(funcionario);
                if (op == 2) removerCliente(funcionario);
                if (op == 3) atualizarCliente(funcionario);
                if (op == 4) buscarCliente();
                if (op == 5) listarCliente();
                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("ERROR: Digito incorreto.");
            }
        }
    }

    // -> CRUD de clientes
    private void cadastrarCliente(Funcionario funcionario) {
        limparTela();
        write("═══════════════ CADASTRO DE CLIENTE ═══════════════");
        String nome = reader("Nome e Sobrenome:");
        String cpf = reader("CPF:");
        String email = reader("Email:");
        String numero = reader("Numero:");

        try {
            Integer idCliente = clienteService.cadastrarCliente(funcionario, nome, cpf, email, numero);
            limparTela();

            write("═══════════════ CADASTRO CONCLUIDO ═══════════════");
            Cliente c = clienteService.buscarCliente(idCliente);
            write(String.format("""
                    Informacoes cadastrais:
                    Nome: %s
                    CPF: %s
                    Email: %s
                    Numero de contato: %s""", c.getNome(), c.getCpf(), c.getEmail(), c.getNumero()));
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void removerCliente(Funcionario funcionario) {
        clienteService.validacaoGerente(funcionario);

        limparTela();
        write("═══════════════ REMOCAO DE CLIENTE ═══════════════");
        String cpf = reader("Digite o CPF do cliente:\n>");
        try {
            Cliente c = clienteService.buscarCliente(cpf);

            write(String.format("""
                            Informacoes do cliente:
                            ID: %d
                            Nome: %s
                            CPF: %s
                            Email: %s
                            Numero de contato: %s""", c.getId(), c.getNome(), c.getCpf(),
                    c.getEmail(), c.getNumero()));

            String confirmacao = reader("""
                    ═════════════════════════════════════════════════════
                    Deseja realmente remover esse cliente?
                    WARN: Ao confirmar, todos os dados deste cliente serao apagados.
                    >""").toUpperCase();

            if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                clienteService.removerCliente(funcionario, c);
                write("SUCCESS: Operacao concluida.");
            } else {
                write("ERROR; Operacao cancelada.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void atualizarCliente(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuAtualizacaoCliente();

            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) {
                    limparTela();
                    write("═══════════════ ATUALIZAR NOME ═══════════════");
                    String cpf = reader("Digite o CPF do cliente:\n>");
                    try {
                        Cliente c = clienteService.buscarCliente(cpf);
                        write(String.format("""
                                Informacoes do cliente:
                                Nome Atual: %s
                                CPF: %s
                                ═════════════════════════════════════════════════════""",
                                c.getNome(), c.getCpf()));
                        String novoNome = reader("Digite o novo numero:");

                        String confirmacao = reader("""
                                Deseja realmente atualizar esse cliente?
                                >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            clienteService.atualizarDados(funcionario, c, novoNome, null, null);
                            write("SUCCESS: Operacao concluida.");
                        } else {
                            write("ERROR; Operacao cancelada.");
                        }
                    } catch (RuntimeException e) {
                        write("ERROR: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 2) {
                    limparTela();
                    write("═══════════════ ATUALIZAR EMAIL ═══════════════");
                    String cpf = reader("Digite o CPF do cliente:\n>");
                    try {
                        Cliente c = clienteService.buscarCliente(cpf);
                        write(String.format("""
                                Informacoes do cliente:
                                Nome: %s
                                Email atual: %s
                                ══════════════════════════════════════════════════════""",
                                c.getNome(), c.getEmail()));
                        String novoEmail = reader("Digite o novo email:");

                        String confirmacao = reader("""
                                Deseja realmente atualizar esse cliente?
                                >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            clienteService.atualizarDados(funcionario, c, null, novoEmail, null);
                            write("SUCCESS: Operacao concluida.");
                        } else {
                            write("ERROR; Operacao cancelada.");
                        }
                    } catch (Exception e) {
                        write("ERROR: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 3) {
                    limparTela();
                    write("═══════════════ ATUALIZAR NUMERO ═══════════════");
                    String cpf = reader("Digite o CPF do cliente:\n>");
                    try {
                        Cliente c = clienteService.buscarCliente(cpf);
                        write(String.format("""
                                Informacoes do cliente:
                                Nome Atual: %s
                                Numero atual: %s
                                ═════════════════════════════════════════════════════""",
                                c.getNome(), c.getNumero()));
                        String novoNumero = reader("Digite o novo numero:");

                        String confirmacao = reader("""
                                Deseja realmente atualizar esse cliente?
                                >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            clienteService.atualizarDados(funcionario, c, null, null, novoNumero);
                            write("SUCCESS: Operacao concluida.");
                        } else {
                            write("ERROR; Operacao cancelada.");
                        }
                    } catch (RuntimeException e) {
                        write("ERRO: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("ERROR: Digito invalido.");
            }
        }
    }

    private void buscarCliente() {
        limparTela();
        write("═══════════════ BUSCA DE CLIENTE ═══════════════");
        String busca = reader("Busque pelo: nome, cpf ou email");
        try {
            List<Cliente> resultado = clienteService.buscaFiltradaCliente(busca);
            if (!resultado.isEmpty()) {
                resultado
                        .forEach(c -> write(String.format("""
                                        ═════════════════════════════════════════════════
                                        ID: %d
                                        Nome: %s
                                        CPF: %s
                                        Email: %s
                                        Numero: %s
                                        Quant. livro: %d""",
                                c.getId(), c.getNome(), c.getCpf(), c.getEmail(), c.getNumero(), c.getQuantidadeLivro())));
            } else {
                write("ERROR: Nenhum cliente encontrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void listarCliente() {
        limparTela();
        write("═══════════════ LISTA DE CLIENTES ═══════════════");
        try {
            if (!clienteService.listaClientes().isEmpty()) {
                clienteService
                        .listaClientes()
                        .forEach(c -> write(String.format("""
                                    ID: %d
                                    Nome: %s
                                    CPF: %s
                                    Email: %s
                                    Numero: %s
                                    Quant. livros: %d
                                    ═════════════════════════════════════════════════""",
                            c.getId(), c.getNome(), c.getCpf(), c.getEmail(), c.getNumero(), c.getQuantidadeLivro())));
            } else {
                write("ERROR: Nenhum cliente cadastrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    // -> Cabecarios de menus
    private void menuGerenciamentoCliente() {
        write("""
                    ╔════════════════════════════════════════╗
                    ║         GENRENCIAR CLIENTE             ║
                    ╠════════════════════════════════════════╣
                    ║   1 - Cadastrar cliente                ║
                    ║   2 - Remover cliente                  ║
                    ║   3 - Atualizar dados                  ║
                    ║   4 - Buscar cliente                   ║
                    ║   5 - Listar clientes                  ║
                    ║   0 - Sair                             ║
                    ╚════════════════════════════════════════╝""");
    }

    private void menuAtualizacaoCliente() {
        write("""
                    ╔════════════════════════════════════════╗
                    ║           ATUALIZAR CLIENTE            ║
                    ╠════════════════════════════════════════╣
                    ║   1 - Atualizar nome                   ║
                    ║   2 - Atualizar email                  ║
                    ║   3 - Atualizar numero                 ║
                    ║   0 - Sair                             ║
                    ╚════════════════════════════════════════╝""");
    }

    // -> Utilitarios
    private void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void aguardandoEnter() {
        String enter;
        do {
            enter = reader("Pressione (ENTER) para continuar.");
        } while (!enter.isBlank());
    }
}
