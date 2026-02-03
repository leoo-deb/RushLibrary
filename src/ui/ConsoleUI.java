package ui;

import model.Funcionario;
import services.*;

import static io.Input.reader;
import static io.Output.write;

public class ConsoleUI {
    private final LivroUI livroUI;
    private final FuncionarioUI funcionarioUI;
    private final ClienteUI clienteUI;
    private final FornecedorUI fornecedorUI;

    private Funcionario funcionario;

    public ConsoleUI() {
        ClienteService clienteService = new ClienteService();
        FornecedorService fornecedorService = new FornecedorService();
        LivroService livroService = new LivroService();
        FuncionarioService funcionarioService = new FuncionarioService();

        this.livroUI = new LivroUI(livroService, clienteService, fornecedorService);
        this.funcionarioUI = new FuncionarioUI(funcionarioService);
        this.clienteUI = new ClienteUI(clienteService);
        this.fornecedorUI = new FornecedorUI(fornecedorService);
    }

    public void executar() {
         while (true) {
             funcionario = null;
             write("""
                     ╔════════════════════════════════════════╗
                     ║             RushLibrary                ║
                     ╠════════════════════════════════════════╣
                     ║   1 - Login                            ║
                     ║   2 - Sair                             ║
                     ╚════════════════════════════════════════╝""");

             try {
                 int op = Integer.parseInt(reader(">"));

                 if (op == 1) realizarLogin();
                 if (op == 2) break;
             } catch (NumberFormatException e) {
                 write("ERROR: digito incorreto.");
             }
         }
     }

    private void realizarLogin() {
        limparTela();
        write("═══════════════ LOGIN ═══════════════");
        String cpf = reader("CPF:");
        String senha = reader("Senha:");

        try {
            FuncionarioService funcionarioService = new FuncionarioService();
            funcionario = funcionarioService.buscarFuncionario(cpf);
            if (!funcionario.getSenha().equals(senha)) {
                write("Senha incorreta.");
                return;
            }

            menuLivraria();
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
    }

    private void menuLivraria() {
        while (true) {
            limparTela();
            menu();
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) livroUI.gerenciarLivro(funcionario);
                if (op == 2) funcionarioUI.gerenciarFuncionario(funcionario);
                if (op == 3) clienteUI.gerenciarCliente(funcionario);
                if (op == 4) fornecedorUI.gerenciarFornecedor(funcionario);
                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("Digito incorreto.");
            }
        }
    }

    private void menu() {
        write("""
                     ╔════════════════════════════════════════╗
                     ║            MENU LIVRARIA               ║
                     ╠════════════════════════════════════════╣
                     ║   1 - Gerenciar livro                  ║
                     ║   2 - Gerenciar funcionario            ║
                     ║   3 - Gerenciar clientes               ║
                     ║   4 - Gerenciar empresas               ║
                     ║   0 - Sair                             ║
                     ╚════════════════════════════════════════╝""");
    }

    private void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
