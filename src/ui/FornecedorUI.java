package ui;

import model.Funcionario;
import services.FornecedorService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.Input.reader;
import static io.Output.write;

public class FornecedorUI {
    private final FornecedorService fornecedorService;

    public FornecedorUI(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    public void gerenciarFornecedor(Funcionario funcionario) {
        fornecedorService.validacaoGerente(funcionario);

        while (true) {
            limparTela();
            menuGerenciamentoFornecedor();
            int op = Integer.parseInt(reader(">"));

            if (op == 0) break;
        }
    }

    private void cadastrarFornecedor() {
        limparTela();
        write("═══════════════ CADASTRO DE FORNECEDOR ═══════════════");
        String nome = reader("Nome:");
        String cpf = reader("Titular:");
        String email = reader("CNPJ:");
        String tipo = reader("Tipo de contrato:");

        String vigencia = reader("Vigencia (DIA/MES/ANO):\n>");
        LocalDate vigenciaFormatado = LocalDate.parse(vigencia, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String vencimento = reader("Vencimento (DIA/MES/ANO):\n>");
        LocalDate vencimentoFormatado = LocalDate.parse(vencimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));


    }

    // -> Cabecarios de menus
    private void menuGerenciamentoFornecedor() {
        write("""
                    ╔════════════════════════════════════════╗
                    ║        GENRENCIAR FORNECEDORES         ║
                    ╠════════════════════════════════════════╣
                    ║   1 - Cadastrar novo fornecedor        ║
                    ║   2 - Atualizar contrato               ║
                    ║   3 - Buscar fornecedor                ║
                    ║   4 - Listar fornecedores              ║
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
