package ui;

import model.Funcionario;
import services.*;

import static io.Input.reader;
import static io.Output.write;

public class ConsoleUI {
    private final LivroUI livroUI;
    private final FuncionarioUI funcionarioUI;

    private final FuncionarioService funcionarioService;
    private Funcionario funcionario;

    public ConsoleUI() {
        ClienteService clienteService = new ClienteService();
        EmpresaService empresaService = new EmpresaService();
        CategoriaService categoriaService = new CategoriaService();
        EstoqueService estoqueService = new EstoqueService();
        ContratoService contratoService = new ContratoService();
        LivroService livroService = new LivroService();

        this.funcionarioService = new FuncionarioService();
        this.livroUI = new LivroUI(livroService, estoqueService, clienteService, categoriaService, contratoService);
        this.funcionarioUI = new FuncionarioUI(funcionarioService);
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

             int op = Integer.parseInt(reader(">"));

             if (op == 1) realizarLogin();
             if (op == 2) break;
             if (op > 2 || op < 1) write("Opcao incorreta.");
         }
     }

     private void realizarLogin() {
         limparTela();
         write("═══════════════ LOGIN ═══════════════");
         String cpf = reader("CPF:");

         funcionario = funcionarioService.buscarFuncionario(cpf);
         menuLivraria();
     }

     private void menuLivraria() {
         while (true) {
             limparTela();
             write("""
                     ╔════════════════════════════════════════╗
                     ║            MENU LIVRARIA               ║
                     ╠════════════════════════════════════════╣
                     ║   1 - Gerenciar livro                  ║
                     ║   2 - Gerenciar funcionario            ║
                     ║   3 - Gerenciar clientes               ║
                     ║   4 - Gerenciar contratos              ║
                     ║   0 - Sair                             ║
                     ╚════════════════════════════════════════╝""");
             int op = Integer.parseInt(reader(">"));

             if (op == 1) livroUI.gerenciarLivro(funcionario);
             if (op == 2) funcionarioUI.gerenciarFuncionario(funcionario);
             if (op == 0) break;
         }
     }

    private void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
