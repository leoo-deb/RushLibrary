package ui;

import model.Contrato;
import model.Fornecedor;
import model.Funcionario;
import services.FornecedorService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) cadastrarFornecedor();
                if (op == 2) atualizarContrato();
                if (op == 3) buscarFornecedor();
                if (op == 4) listaFornecedores();
                if (op == 0) break;
            } catch (RuntimeException e) {
                write("ERROR: Digito incorreto.");
            }
        }
    }

    private void cadastrarFornecedor() {
        limparTela();
        write("═══════════════ CADASTRO DE FORNECEDOR ═══════════════");
        String nome = reader("Nome:");
        String titular = reader("Titular:");
        String cnpj = reader("CNPJ:");
        String tipo = reader("Tipo de contrato:");
        LocalDate vigenciaFormatado;
        LocalDate vencimentoFormatado;

        try {
            String vigencia = reader("Vigencia (DIA/MES/ANO):\n>");
            vigenciaFormatado = LocalDate.parse(vigencia, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String vencimento = reader("Vencimento (DIA/MES/ANO):\n>");
            vencimentoFormatado = LocalDate.parse(vencimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (RuntimeException e) {
            write("ERROR: Data invalida.");
            return;
        }

        try {
            Integer idFornecedor = fornecedorService.registrarFornecedor(nome, titular, cnpj, tipo,
                    vigenciaFormatado, vencimentoFormatado);

            limparTela();
            write("═══════════════ CADASTRO CONCLUIDO ═══════════════");
            write(String.format("""
                    Informacoes do cadastro:
                    Nome: %s
                    Titular" %s
                    CNPJ: %s
                    Tipo de contrato: %s
                    Cod. Fornecedor: %d""", nome, titular, cnpj, tipo, idFornecedor));
        } catch (Exception e) {
            write("ERROR" + e.getMessage());
        }
        aguardandoEnter();
    }

    private void atualizarContrato() {
        limparTela();
        write("═══════════════ ATUALIZAR CONTRATO ═══════════════");
        try {
            Integer code = Integer.parseInt(reader("Codigo do fornecedor:"));
            try {
                Fornecedor fornecedor = fornecedorService.buscarFornecedor(code);
                String vencimento;
                LocalDate vencimentoFormatado;

                try {
                    vencimento = reader("Novo vencimento (DIA/MES/ANO):\n>");
                    vencimentoFormatado = LocalDate.parse(vencimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException e) {
                    write("ERROR: Data incorreta.");
                    return;
                }

                Contrato contrato = fornecedorService.atualizarVencimento(fornecedor, vencimentoFormatado);
                limparTela();

                write("═══════════════ CONTRATO ATUALIZADO ═══════════════");
                write(String.format("""
                        Fornecedor: %s
                        CNPJ: %s
                        Tipo: %s
                        Novo vencimento: %s""", fornecedor.getNome(), fornecedor.getCnpj(), contrato.getTipo(), vencimento));
            } catch (Exception e) {
                write("ERROR: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            write("ERROR: Digito incorreto.");
        }
        aguardandoEnter();
    }

    private void buscarFornecedor() {
        limparTela();
        write("═══════════════ BUSCA DE FORNECEDOR ═══════════════");
        String busca = reader("Busque pelo: nome do fornecedor ou CNPJ:\n>");
        try {
            List<Fornecedor> resultado = fornecedorService.buscaFiltradaFornecedor(busca);

            if (!resultado.isEmpty()) {
                resultado
                        .forEach(f -> {
                            Contrato contrato = fornecedorService.buscarContrato(f);
                            fornecedorService.atualizarStatusContrato(contrato);

                            write(String.format("""
                                            Cod. Fornecedor: #%d
                                            Fornecedor: %s
                                            Titular: %s
                                            CNPJ: %s
                                            
                                            Tipo de contrato: %s
                                            Status: %s
                                            Vigencia: %s
                                            Vencimento: %s
                                            ═══════════════════════════════════════════════════""",
                                    f.getId(), f.getNome(), f.getTitular(), f.getCnpj(),
                                    contrato.getTipo(), contrato.getStatus(), contrato.getVigencia(), contrato.getVencimento()));
                        });
            } else {
                write("ERROR: Nenhum fornecedor encontrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void listaFornecedores() {
        limparTela();
        write("═══════════════ LISTA DE FORNECEDORES ═══════════════");
        try {
            List<Fornecedor> listarFornecedores = fornecedorService.listarFornecedores();
            if (!listarFornecedores.isEmpty()) {
                listarFornecedores
                        .forEach(f -> {
                            Contrato contrato = fornecedorService.buscarContrato(f);
                            fornecedorService.atualizarStatusContrato(contrato);

                            write(String.format("""
                                            Cod. Fornecedor: #%d
                                            Fornecedor: %s
                                            Titular: %s
                                            CNPJ: %s
                                            Tipo de contrato: %s
                                            Status: %s
                                            Vigencia: %s
                                            Vencimento: %s
                                            ═════════════════════════════════════════════════════""",
                                    f.getId(), f.getNome(), f.getTitular(), f.getCnpj(),
                                    contrato.getTipo(), contrato.getStatus(), contrato.getVigencia(), contrato.getVencimento()));
                        });
            } else {
                write("ERROR: Nenhum fornecedor encontrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
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
