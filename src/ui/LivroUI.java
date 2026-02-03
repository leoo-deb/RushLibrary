package ui;

import com.mysql.cj.protocol.a.NumberValueEncoder;
import model.*;
import services.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.Input.reader;
import static io.Output.write;

public class LivroUI {
    private final LivroService livroService;
    private final FornecedorService fornecedorService;

    private final EstoqueUI estoqueUI;

    public LivroUI(LivroService livroService, ClienteService clienteService, FornecedorService fornecedorService) {
        this.livroService = livroService;
        this.fornecedorService = fornecedorService;

        this.estoqueUI = new EstoqueUI(livroService, clienteService);
    }

    public void gerenciarLivro(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuGerenciamentoLivro();
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) cadastrarLivro(funcionario);
                if (op == 2) removerLivro(funcionario);
                if (op == 3) atualizarLivro();
                if (op == 4) buscarLivro();
                if (op == 5) listarLivros();
                if (op == 6) estoqueUI.gerenciarEstoque(funcionario);
                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("ERROR: Digito incorreto.");
            }
        }

    }

    // — > CRUD dos livros
    private void cadastrarLivro(Funcionario funcionario) {
        livroService.validacaoGerente(funcionario);

        limparTela();
        write("═══════════════ CADASTRO DE LIVRO ═══════════════");
        String nome = reader("Nome:");
        String sinopse = reader("Sinope:");
        String autor = reader("Autor:");
        Integer isbn;
        try {
            isbn = Integer.parseInt(reader("ISBN:"));
        } catch (NumberFormatException e) {
            write("ERROR: Digito incorreto.");
            return;
        }
        String genero = reader("Genero:");
        String tipo;

        try {
            write("""
                    Selecione o cargo:
                    [1] Fisico
                    [2] Digital
                    [3] Ebook""");
            int escolha = Integer.parseInt(reader(">"));

            switch (escolha) {
                case 1 -> tipo = "FISICO";
                case 2 -> tipo = "DIGITAL";
                case 3 -> tipo = "EBOOK";
                default -> {
                    write("ERROR: Dado incorreto.");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            write("ERROR: Digito incorreto.");
            return;
        }

        Integer codeFornecedor;
        try {
            codeFornecedor = Integer.parseInt(reader("Codigo de Fornecedor:"));
        } catch (NumberFormatException e) {
            write("ERROR: Digito incorreto.");
            return;
        }

        try {
            Fornecedor fornecedor = fornecedorService.buscarFornecedor(codeFornecedor);

            Integer quantInicial;
            try {
                quantInicial = Integer.parseInt(reader("Quantidade inicial:"));
            } catch (NumberFormatException e) {
                write("ERROR: Digito incorreto.");
                return;
            }

            livroService.cadastrarLivro(funcionario, nome, sinopse, autor, isbn, genero, tipo, fornecedor, quantInicial);
            limparTela();

            write("═══════════════ LIVRO CADASTRADO ═══════════════");
            write(String.format("""
                    Informacoes cadastrais:
                    Nome: %s
                    Sinopse: %s
                    Autor: %s
                    ISBN: %d
                    Genero: %s
                    Tipo: %s
                    
                    Cod. de Fornecedor: #%d
                    Quant. inicial: %d""", nome, sinopse, autor, isbn, genero, tipo, codeFornecedor, quantInicial));
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void removerLivro(Funcionario funcionario) {
        livroService.validacaoGerente(funcionario);

        limparTela();
        write("═══════════════ MANUTENCAO ═══════════════");
//        Integer isbn = Integer.parseInt(reader("Digite o ISBN do livro:\n>"));
//        Livro l = livroService.buscarLivro(isbn);
//
//        write(String.format("""
//                Informacoes do livro:
//                Nome: %s
//                Sinopse: %s
//                Autor: %s
//                ISBN: %d
//                Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo()));
//
//        String confirmacao = reader("""
//                Deseja realmente excluir esse livro?
//                AVISO: Ao confirmar, todos os dados deste livro serao removidas.
//                >""").toUpperCase();
//
//        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
//            livroService.removerLivro(isbn);
//            estoqueService.removerEstoque(l.getId());
//            write("Operacao concluida.");
//            aguardandoEnter();
//        } else {
//            write("Operacao concelada.");
//            aguardandoEnter();
//        }
        aguardandoEnter();
    }

    private void atualizarLivro() {
        while (true) {
            limparTela();
            menuAtualizacaoLivro();
            try {
                int op = Integer.parseInt(reader(">"));

                if (op == 1) {
                    int isbn;
                    try {
                        isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                    } catch (NumberFormatException e) {
                        write("ERROR: Digito invalido.");
                        return;
                    }

                    try {
                        Livro l = livroService.buscarLivro(isbn);
                        write(String.format("""
                                    Informacoes do livro:
                                    Nome: %s
                                    Sinopse: %s
                                    Autor: %s
                                    ISBN: %d
                                    Genero: %s
                                    Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getGenero(), l.getTipo()));
                        aguardandoEnter();

                        String nome = reader("Digite o novo nome:\n>");

                        String confirmacao = reader("""
                                    Deseja realmente atualizar esse livro?
                                    >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            Livro livroAtualizado = livroService.atualizarLivro(l, nome, null, null);

                            write(String.format("""
                                                Informacoes do livro atualizado:
                                                Nome: %s
                                                Sinopse: %s
                                                Autor: %s
                                                ISBN: %d
                                                Genero: %s
                                                Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                                    livroAtualizado.getIsbn(), livroAtualizado.getGenero(), livroAtualizado.getTipo()));
                        } else {
                            write("Operacao concelada.");
                        }
                    } catch (Exception e) {
                        write("ERROR: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 2) {
                    int isbn;
                    try {
                        isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                    } catch (NumberFormatException e) {
                        write("ERROR: Digito invalido.");
                        return;
                    }
                    try {
                        Livro livro = livroService.buscarLivro(isbn);
                        write(String.format("""
                                        Informacoes do livro:
                                        Nome: %s
                                        Sinopse: %s
                                        Autor: %s
                                        ISBN: %d
                                        Genero: %s
                                        Tipo: %s""", livro.getNome(), livro.getSinopse(), livro.getAutor(), livro.getIsbn(),
                                livro.getGenero(), livro.getTipo()));
                        aguardandoEnter();

                        String sinopse = reader("Digite a nova sinopse:\n>");
                        String confirmacao = reader("""
                                Deseja realmente atualizar esse livro?
                                >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            Livro livroAtualizado = livroService.atualizarLivro(livro, null, sinopse, null);
                            write(String.format("""
                                            Informacoes do livro atualizado:
                                            Nome: %s
                                            Sinopse: %s
                                            Autor: %s
                                            ISBN: %d
                                            Genero: %s
                                            Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                                    livroAtualizado.getIsbn(), livroAtualizado.getGenero(), livroAtualizado.getTipo()));
                        } else {
                            write("Operacao concelada.");
                        }
                    } catch (Exception e) {
                        write("ERROR: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 3) {
                    int isbn;
                    try {
                        isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                    } catch (NumberFormatException e) {
                        write("ERROR: Digito incorreto.");
                        return;
                    }

                    try {
                        Livro livro = livroService.buscarLivro(isbn);
                        write(String.format("""
                                        Informacoes do livro:
                                        Nome: %s
                                        Sinopse: %s
                                        Autor: %s
                                        ISBN: %d
                                        Genero: %s
                                        Tipo: %s""", livro.getNome(), livro.getSinopse(), livro.getAutor(), livro.getIsbn(),
                                livro.getGenero(), livro.getTipo()));

                        String autor = reader("Digite o novo autor:\n>");
                        String confirmacao = reader("""
                                Deseja realmente atualizar esse livro?
                                >""").toUpperCase();

                        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                            Livro livroAtualizado = livroService.atualizarLivro(livro, null, null, autor);
                            write(String.format("""
                                            Informacoes do livro atualizado:
                                            Nome: %s
                                            Sinopse: %s
                                            Autor: %s
                                            ISBN: %d
                                            Genero: %s
                                            Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                                    livroAtualizado.getIsbn(), livroAtualizado.getGenero(), livroAtualizado.getTipo()));
                        } else {
                            write("Operacao concelada.");
                        }
                    } catch (Exception e) {
                        write("ERROR: " + e.getMessage());
                    }
                    aguardandoEnter();
                }

                if (op == 0) break;
            } catch (NumberFormatException e) {
                write("ERROR: Digito incorreto.");
            }
        }
    }

    private void buscarLivro() {
        limparTela();
        write("═══════════════ BUSCA DE LIVROS ═══════════════");
        String busca = reader("Busque pelo nome ou autor\n>");
        try {
            List<Livro> resultado = livroService.buscaFiltradaLivro(busca);

            if (!resultado.isEmpty()) {
                resultado
                        .forEach(l -> {
                            Estoque e = livroService.buscarEstoqueIdLivro(l.getId());
                            write(String.format("""
                                            Informacoes do livro:
                                            Nome: %s
                                            Sinopse: %s
                                            Autor: %s
                                            ISBN: %d
                                            Genero: %s
                                            Tipo: %s
                                            
                                            Code. Fornecedor: #%d
                                            Estoque: #%d
                                            Quantidade: %d
                                            ═══════════════════════════════════════════════""",
                                    l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(),
                                    l.getGenero(), l.getTipo(), l.getId_fornecedor(), e.getId(), e.getQuantidade()));
                        });
            } else {
                write("ERROR: Nenhum livro encontrado.");
            }
        } catch (Exception e) {
            write("ERROR: " + e.getMessage());
        }
        aguardandoEnter();
    }

    private void listarLivros() {
        if (!livroService.listarLivros().isEmpty()) {
            write("═══════════════ LISTA DE LIVROS ═══════════════");
            try {
                livroService
                        .listarLivros()
                        .forEach(l -> {
                            Estoque e = livroService.buscarEstoqueIdLivro(l.getId());
                            write(String.format("""
                                            Informacoes do livro:
                                            Nome: %s
                                            Sinopse: %s
                                            Autor: %s
                                            ISBN: %d
                                            Tipo: %s
                                            
                                            Estoque: #%d
                                            Quantidade: %d
                                            ═══════════════════════════════════════════════""",
                                    l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(),
                                    l.getTipo(), e.getId(), e.getQuantidade()));
                        });
            } catch (Exception e) {
                write("ERROR: " + e.getMessage());
            }
        } else {
            write("ERROR: Nenhum livro no sistema encontrado.");
        }
        aguardandoEnter();
    }

    // — > Menus de gerenciamento
    private void menuGerenciamentoLivro() {
        write("""
                ╔════════════════════════════════════════╗
                ║           GERENCIAR LIVRO              ║
                ╠════════════════════════════════════════╣
                ║   1 - Cadastrar livro                  ║
                ║   2 - Remover livro                    ║
                ║   3 - Atualizar livro                  ║
                ║   4 - Buscar livro                     ║
                ║   5 - Listar livros                    ║
                ║   6 - Gerenciar estoque                ║
                ║   0 - Sair                             ║
                ╚════════════════════════════════════════╝""");
    }

    private void menuAtualizacaoLivro() {
        write("""
                ╔════════════════════════════════════════╗
                ║          ATUALIZACAO DE LIVRO          ║
                ╠════════════════════════════════════════╣
                ║   1 - Atualizar nome                   ║
                ║   2 - Atualizar sinopse                ║
                ║   3 - Atualizar autor                  ║
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
