package ui;

import model.Funcionario;
import services.FuncionarioService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static io.Input.reader;
import static io.Output.write;

public record FuncionarioUI(FuncionarioService funcionarioService) {

    public void gerenciarFuncionario(Funcionario funcionario) {
        funcionarioService.validacaoGerente(funcionario);

        while (true) {
            limparTela();
            menuGerenciarFuncionario();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) cadastrarFucnionario();
            if (op == 2) removerFuncionario();
            if (op == 3) atualizarFuncionario();
            if (op == 4) buscarFuncionario();
            if (op == 5) listarFuncionario();
            if (op == 0) break;
        }
    }

    private void cadastrarFucnionario() {
        limparTela();
        write("═══════════════ CADASTRO DE FUNCIONARIO ═══════════════");
        String nome = reader("Nome:");
        String cpf = reader("CPF:");
        String email = reader("Email:");
        String cargo;
        while (true) {
            write("""
                    Selecione o cargo:
                    [1] Gerente
                    [2] Comum""");
            int escolha = Integer.parseInt(reader(">"));

            if (escolha == 1) {
                cargo = "GERENTE";
                break;
            }
            if (escolha == 2) {
                cargo = "COMUM";
                break;
            }
            write("ERROR: Dado incorreto.");
        }
        String dataAdmissao = reader("Data de admissao (DIA/MES/ANO)\n>");
        LocalDate data = LocalDate.parse(dataAdmissao, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Integer idFuncionario = funcionarioService.cadastrarFuncionario(nome, cpf, email, cargo, data);
        limparTela();

        write("═══════════════ FUNCIONARIO CADASTRADO ═══════════════");
        write(String.format("""
                Informacoes cadastrais:
                ID: %d
                Nome: %s
                CPF: %s
                Email: %s
                Cargo: %s
                Data de admissao: %s""", idFuncionario, nome, cpf, email, cargo, data));

        aguardandoEnter();
    }

    private void removerFuncionario() {
        limparTela();
        write("═══════════════ REMOCAO DE FUNCIONARIO ═══════════════");
        String cpfFuncionario = reader("CPF do funcionario:");
        Funcionario f = funcionarioService.buscarFuncionario(cpfFuncionario);

        write(String.format("""
                Informacoes do funcionario:
                ID: %d
                Nome: %s
                CPF: %s
                Email: %s
                Cargo: %s
                Data de admissao: %s
                Data de admissao: %s""", cpfFuncionario, f.getNome(), f.getCpf(), f.getEmail(), f.getCargo(), f.getAdimissao()));

        String confirmacao = reader("""
                Deseja realmente remover esse funcionario?
                WARN: Ao confirmar, todos os dados deste funcionario serao apagados.
                >""").toUpperCase();

        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
            funcionarioService.removerFuncionario(cpfFuncionario);
            write("SUCCESS: Operacao concluida.");
            aguardandoEnter();
        } else {
            write("ERROR; Operacao cancelada.");
            aguardandoEnter();
        }
    }

    private void atualizarFuncionario() {
        while (true) {
            limparTela();
            menuAtualizarFuncionario();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) {
                write("═══════════════ ATUALIZAR NOME ═══════════════");
                String cpf = reader("CPF do funcionario:");
                Funcionario f = funcionarioService.buscarFuncionario(cpf);

                write(String.format("""
                        ID: %d
                        Nome: %s
                        CPF: %s""", f.getId(), f.getNome(), f.getCpf()));

                String novoNome = reader("Novo nome:");
                String confirmacao = reader("Deseja realmente atualizar o nome?\n>").toUpperCase();

                if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                    funcionarioService.atualizarDados(cpf, novoNome, null);
                    write("SUCCESS: Operacao cocnluida.");
                    aguardandoEnter();
                } else {
                    write("INFO: Operacao cancelada.");
                    aguardandoEnter();
                }
            }

            if (op == 2) {
                write("═══════════════ ATUALIZAR EMAIL ═══════════════");
                String cpf = reader("CPF do funcionario:");
                Funcionario f = funcionarioService.buscarFuncionario(cpf);

                write(String.format("""
                        ID: %d
                        Nome: %s
                        Email: %s""", f.getId(), f.getNome(), f.getEmail()));

                String novoEmail = reader("Novo email:");
                String confirmacao = reader("Deseja realmente atualizar o email?\n>").toUpperCase();

                if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                    funcionarioService.atualizarDados(cpf, null, novoEmail);
                    write("SUCCESS: Operacao cocnluida.");
                    aguardandoEnter();
                } else {
                    write("INFO: Operacao cancelada.");
                    aguardandoEnter();
                }
            }

            if (op == 3) {
                write("═══════════════ PROMOVER FUNCIONARIO ═══════════════");
                String cpf = reader("CPF do funcionario:");
                Funcionario f = funcionarioService.buscarFuncionario(cpf);
                String novoCargo;

                write(String.format("""
                        ID: %d
                        Nome: %s
                        Cargo atual: %s""", f.getId(), f.getNome(), f.getCargo()));

                while (true) {
                    write("""
                            Selecione o cargo para promocao:
                            [1] Gerente
                            [2] Comum""");
                    int esc = Integer.parseInt(reader(">"));

                    if (esc == 1) {
                        novoCargo = "GERENTE";
                        break;
                    }

                    if (esc == 2) {
                        novoCargo = "COMUM";
                        break;
                    }
                    write("ERROR: Dado invalido.");
                }

                    String confirmacao = reader("Deseja realmente atualizar o email?\n>").toUpperCase();

                    if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                        funcionarioService.realizarPromocao(cpf, novoCargo);
                        write("SUCCESS: Operacao cocnluida.");
                    } else {
                        write("INFO: Operacao cancelada.");
                    }
                    aguardandoEnter();
                }

            if (op == 0 ) break;
        }
    }

    private void buscarFuncionario() {
        limparTela();
        write("═══════════════ BUSCAR FUNCIONARIO ═══════════════");
        String busca = reader("Busque por: id, nome, cpf ou email do funcionario\n");

        funcionarioService
                .listarFuncionario()
                .stream()
                .filter(f -> f.getId() == Integer.parseInt(busca)
                        || f.getNome().contains(busca)
                        || f.getCpf().contains(busca)
                        || f.getEmail().contains(busca))
                .forEach(f -> write(String.format("""
                        ID: %d
                        Nome: %s
                        CPF: %s
                        Email: %s
                        Cargo atual: %s
                        Data de admissao: %s""",f.getId(), f.getNome(), f.getCpf(), f.getEmail(), f.getCargo(), f.getAdimissao())));
        aguardandoEnter();
    }

    private void listarFuncionario() {
        limparTela();
        write("═══════════════ LISTA DE FUNCIONARIOS ═══════════════");
        funcionarioService
                .listarFuncionario()
                .forEach(f -> write(String.format("""
                        Informacoes do funcionario:
                        ID: %d
                        Nome: %s
                        CPF: %s
                        Email: %s
                        Cargo atual: %s
                        Data de admissao: %s""",f.getId(), f.getNome(), f.getCpf(), f.getEmail(), f.getCargo(), f.getAdimissao())));
        aguardandoEnter();
    }

    private void menuGerenciarFuncionario() {
        write("""
                     ╔════════════════════════════════════════╗
                     ║         GERENCIAR FUNCIONARIO          ║
                     ╠════════════════════════════════════════╣
                     ║   1 - Cadastrar funcionario            ║
                     ║   2 - Remover funcionario              ║
                     ║   3 - Atualizar dados funcionario      ║
                     ║   4 - Buscar funcionario               ║
                     ║   5 - Listar funcionarios              ║
                     ║   0 - Sair                             ║
                     ╚════════════════════════════════════════╝""");
    }

    private void menuAtualizarFuncionario() {
        write("""
                     ╔════════════════════════════════════════╗
                     ║           ATUALIZAR DADOS              ║
                     ╠════════════════════════════════════════╣
                     ║   1 - Atualizar nome                   ║
                     ║   2 - Atualizar email                  ║
                     ║   3 - Promover funcionario             ║
                     ║   0 - Sair                             ║
                     ╚════════════════════════════════════════╝""");
    }

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
