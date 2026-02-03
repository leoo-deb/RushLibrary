package ui;

import model.*;
import services.ClienteService;
import services.LivroService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static io.Input.reader;
import static io.Output.write;

public record EstoqueUI(LivroService livroService, ClienteService clienteService) {

    public void gerenciarEstoque(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuGerenciarEstoque();
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) registrarAbastecimentoEstoque(funcionario);
                if (op == 2) registrarEmprestimoEstoque(funcionario);
                if (op == 3) registrarRetornoEstoque(funcionario);
                if (op == 4) buscarMovimentacaoEstoque();
                if (op == 5) listarMovimentacaoEstoque();
                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("ERROR: Digito incorreto.");
            }
        }
    }

    // — > Registros de movimentacao de estoques
    private void registrarAbastecimentoEstoque(Funcionario funcionario) {
        limparTela();
        write("═══════════════ REGISTRAR ABASTECIMENTO ═══════════════");
        int idEstoque;
        int quant;
        try {
            idEstoque = Integer.parseInt(reader("ID estoque:"));
            quant = Integer.parseInt(reader("Quantidade do abastecimento:"));
        } catch (NumberFormatException e) {
            write("ERROR: Digito invalido.");
            return;
        }
        try {
            Estoque estoque = livroService.buscarEstoque(idEstoque);
            livroService.registrarAbastecimento(estoque, funcionario, quant);
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void registrarEmprestimoEstoque(Funcionario funcionario) {
        while (true) {
            limparTela();
            write("═══════════════ REGISTRAR EMPRESTIMO ═══════════════");
            try {
                int idEstoque = Integer.parseInt(reader("ID estoque:"));
                int quant = Integer.parseInt(reader("Quantidade de livros:"));

                try {
                    Estoque estoque = livroService.buscarEstoque(idEstoque);
                    Livro livro = livroService.buscarLivro(estoque.getId_livro());

                    String cpfCliente = reader("CPF do cliente:");
                    Cliente cliente = clienteService.buscarCliente(cpfCliente);

                    if (livroService.verificarEmprestimoAtrasado(cliente)) {
                        write(String.format("WARN: O cliente %s ja possui emprestimos abertos.", cliente.getNome()));
                        aguardandoEnter();
                    }

                    if (livroService.verificarEmprestimoAtrasado(cliente)) {
                        write(String.format("WARN: O cliente %s possui emprestimos atrasados.", cliente.getNome()));
                        aguardandoEnter();
                    }

                    String dataEntrega = reader("Data para prevista para entrega: (DIA/MES/ANO)\n>");
                    LocalDate dataFormatada = LocalDate.parse(dataEntrega, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    write(String.format("""
                            
                            Informacoes do emprestimo:
                            Cliente: %s
                            CPF: %s
                            Estoque: %d
                            Livro: %s
                            ISBN: %d
                            Quat.: %d
                            """, cliente.getNome(), cliente.getCpf(), estoque.getId(), livro.getNome(), livro.getIsbn(), quant));

                    String confirmacao = reader("""
                            Confirme com (sim/s) se todos os dados estao correto
                            ou digite (cancelar) para cancelar a operaccao:
                            >""").toUpperCase();

                    if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                        livroService.registrarEmprestimo(estoque, funcionario, cliente, dataFormatada, quant);
                        write("SUCCESS: Operacao concluida com sucesso.");
                        break;
                    }
                    if (confirmacao.equals("CANCELAR")) {
                        write("INFO: Operacao cancelada.");
                        break;
                    }
                } catch (Exception e) {
                    write("ERROR: " + e.getMessage());
                }

            } catch (NumberFormatException e) {
                write("ERROR: ID estoque/Quantidade incorreto.");
                return;
            }
        }
        aguardandoEnter();
    }

    private void registrarRetornoEstoque(Funcionario funcionario) {
        limparTela();
        write("═══════════════ REGISTRAR RETORNO ═══════════════");
        String cpfCliente = reader("CPF do cliente:");
        try {
            Cliente cliente = clienteService.buscarCliente(cpfCliente);
            List<Movimentacao> movimentacoes = livroService.listarEmprestimoAberto(cliente);

            while (true) {
                if (!movimentacoes.isEmpty()) {
                    write("Lista de emprestimos abertos:");

                    movimentacoes.forEach(m -> {
                        Estoque estoque = livroService.buscarEstoque(m.getId_estoque());
                        Livro livro = livroService.buscarLivro(estoque.getId_livro());

                        String log = String.format("[#%d] %s | DATA PREVISTA ENTREGA: %s | STATUS: %s",
                                m.getId(), livro.getNome(), m.getDataPrevista(), m.getStatus());

                        write(log);
                    });

                    Integer idMovimentacao = Integer.parseInt(reader("Digite o (#id) para devolucao:\n>"));
                    Movimentacao movimentacao = livroService.buscaMovimentacao(idMovimentacao);
                    Estoque estoque = livroService.buscarEstoque(movimentacao.getId_estoque());

                    String dataEntregaReal = reader("Data de entrega: (DIA/MES/ANO)\n>");
                    LocalDate dataFormatada = LocalDate.parse(dataEntregaReal, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    String confirmacao = reader("""
                            Confirme com (sim/s) para confirmar a operacao
                            ou digite (cancelar) para cancelar a operaccao:
                            >""").toUpperCase();

                    if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                        livroService.registrarRetorno(movimentacao, estoque, funcionario, cliente,
                                dataFormatada, movimentacao.getQuant());
                        write("SUCCESS: Operacao concluida com sucesso.");
                    }
                    if (confirmacao.equals("CANCELAR")) {
                        write("INFO: Operacao cancelada.");
                        break;
                    }

                    String novaDevo = reader("Deseja fazer uma nova devolucao?\n>").toUpperCase();
                    if (!(novaDevo.equals("SIM") || novaDevo.equals("S"))) {
                        break;
                    }
                } else {
                    write("ERROR: Este cliente nao possui nenhum emprestimo aberto.");
                    break;
                }
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void buscarMovimentacaoEstoque() {
        limparTela();
        write("═══════════════ BUSCA SIMPLES ═══════════════");
        try {
            int busca = Integer.parseInt(reader("Busque pelo: ID da movimentacao, do funcionario ou do cliente:\n>"));
            List<Movimentacao> resultado = livroService.buscaFiltradaMovimentacao(busca);
            if (!resultado.isEmpty()) {
                resultado
                        .forEach(m -> {
                            Estoque e = livroService.buscarEstoque(m.getId_estoque());
                            livroService.atualizarStatusMovimentacao(m, m.getDataPrevista());

                            write(String.format("[%s] ID: %d | ESTOQUE: %d | FUNC: %d | TIPO: %s | QUANT: %d | CLIENTE: %s | ENTREGA PREVISTA: %s | ENTREGUE EM: %s | STATUS: %s",
                                    m.getData(), m.getId(), m.getId_estoque(), m.getId_funcionario(),
                                    m.getTipo(), e.getQuantidade(), m.getId_cliente(), m.getDataPrevista(), m.getDataEntrega(), m.getStatus()));
                        });
            } else {
                write("ERROR: Nenhum registro encontrado.");
            }
        } catch (Exception e) {
            write("ERROR " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void listarMovimentacaoEstoque() {
        write("═══════════════ LISTA DE MOVIMENTACAO ═══════════════");
        try {
            List<Movimentacao> resultado = livroService.listarMovimentacao();
            if (!resultado.isEmpty()) {
                resultado
                        .forEach(m -> {
                            livroService.atualizarStatusMovimentacao(m, m.getDataPrevista());

                            write(String.format("[%s] ID: %d | ESTOQUE: %d | FUNC: %d | TIPO: %s | QUANT: %d | CLIENTE: %s | ENTREGA PREVISTA: %s | ENTREGUE EM: %s | STATUS: %s",
                                    m.getData(), m.getId(), m.getId_estoque(), m.getId_funcionario(), m.getTipo(), m.getQuant(),
                                    m.getId_cliente(), m.getDataPrevista(), m.getDataEntrega(), m.getStatus()));
                        });
            } else {
                write("ERROR: Nenhum livro no sistema encontrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    // — > Cabecarios de menu
    private void menuGerenciarEstoque() {
        write("""
                ╔════════════════════════════════════════╗
                ║          GERENCIAR ESTOQUE             ║
                ╠════════════════════════════════════════╣
                ║   1 - Registrar abastecimento          ║
                ║   2 - Registrar emprestimo             ║
                ║   3 - Registrar retorno                ║
                ║   4 - Buscar movimentacao              ║
                ║   5 - Listar movimentacoes             ║
                ║   0 - Sair                             ║
                ╚════════════════════════════════════════╝""");
    }

    // — > Utilitarios
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
