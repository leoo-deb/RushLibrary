package ui;

import model.*;
import services.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import static io.Input.reader;
import static io.Output.write;

public record LivroUI(LivroService livroService, EstoqueService estoqueService, ClienteService clienteService,
                      CategoriaService categoriaService, ContratoService contratoService) {

    public void gerenciarLivro(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuGerenciamentoLivro();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) cadastrarLivro(funcionario);
            if (op == 2) removerLivro(funcionario);
            if (op == 3) atualizarLivro();
            if (op == 4) buscarLivro();
            if (op == 5) listarLivros();
            if (op == 6) gerenciarCategoria(funcionario);
            if (op == 7) gerenciarEstoque(funcionario);
            if (op == 0) break;
        }

    }

    private void cadastrarLivro(Funcionario funcionario) {
        livroService.validacaoGerente(funcionario);

        limparTela();
        write("═══════════════ CADASTRO DE LIVRO ═══════════════");
        String nome = reader("Nome:");
        String sinopse = reader("Sinope:");
        String autor = reader("Autor:");
        Integer isbn = Integer.parseInt(reader("ISBN:"));
        String tipo = reader("Tipo:");

        Integer idCategoria = Integer.parseInt(reader("ID categoria:"));
        Integer codeContrato = Integer.parseInt(reader("Codigo de contrato:"));
        Integer quantInicial = Integer.parseInt(reader("Quantidade inicial:"));

        String desc = reader("Descricao:");

        categoriaService.buscarCategoria(idCategoria);
        contratoService.buscarContrato(codeContrato);

        Integer idLivroCadastrado = livroService.cadastrarLivro(nome, sinopse, autor, isbn,
                tipo, idCategoria, codeContrato);
        Integer idEstoqueCadastrado = estoqueService.cadastrarEstoque(idLivroCadastrado, quantInicial);

        estoqueService.registrarCadastro(idEstoqueCadastrado, funcionario.getId(), desc);

        limparTela();
        write("═══════════════ LIVRO CADASTRADO ═══════════════");
        write(String.format("""
                Informacoes cadastrais:
                Nome: %s
                Sinopse: %s
                Autor: %s
                ISBN: %d
                Tipo: %s
                
                Categoria: %d
                Cod. de Contrato: %d
                Quant. inicial: %d""", nome, sinopse, autor, isbn, tipo, idCategoria, codeContrato, quantInicial));

        aguardandoEnter();
    }

    private void removerLivro(Funcionario funcionario) {
        livroService.validacaoGerente(funcionario);

        limparTela();
        write("═══════════════ REMOCAO DE LIVRO ═══════════════");
        Integer isbn = Integer.parseInt(reader("Digite o ISBN do livro:\n>"));
        Livro l = livroService.buscarLivro(isbn);

        write(String.format("""
                Informacoes do livro:
                Nome: %s
                Sinopse: %s
                Autor: %s
                ISBN: %d
                Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo()));

        String confirmacao = reader("""
                Deseja realmente excluir esse livro?
                AVISO: Ao confirmar, todos os dados deste livro serao removidas.
                >""").toUpperCase();

        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
            livroService.removerLivro(isbn);
            estoqueService.removerEstoque(l.getId());
            write("Operacao concluida.");
            aguardandoEnter();
        } else {
            write("Operacao concelada.");
            aguardandoEnter();
        }
    }

    private void atualizarLivro() {
        while (true) {
            limparTela();
            menuAtualizacaoLivro();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) {
                Integer isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                Livro l = livroService.buscarLivro(isbn);

                write(String.format("""
                        Informacoes do livro:
                        Nome: %s
                        Sinopse: %s
                        Autor: %s
                        ISBN: %d
                        Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo()));
                aguardandoEnter();

                String nome = reader("Digite o novo nome:\n>");

                String confirmacao = reader("""
                        Deseja realmente atualizar esse livro?
                        >""").toUpperCase();

                if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                    Livro livroAtualizado = livroService.atualizarLivro(isbn, nome, null, null);

                    write(String.format("""
                                    Informacoes do livro atualizado:
                                    Nome: %s
                                    Sinopse: %s
                                    Autor: %s
                                    ISBN: %d
                                    Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                            livroAtualizado.getIsbn(), livroAtualizado.getTipo()));
                    aguardandoEnter();
                } else {
                    write("Operacao concelada.");
                    aguardandoEnter();
                }
            }

            if (op == 2) {
                Integer isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                Livro l = livroService.buscarLivro(isbn);

                write(String.format("""
                        Informacoes do livro:
                        Nome: %s
                        Sinopse: %s
                        Autor: %s
                        ISBN: %d
                        Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo()));
                aguardandoEnter();

                String sinopse = reader("Digite a nova sinopse:\n>");

                String confirmacao = reader("""
                        Deseja realmente atualizar esse livro?
                        >""").toUpperCase();

                if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                    Livro livroAtualizado = livroService.atualizarLivro(isbn, null, sinopse, null);

                    write(String.format("""
                                    Informacoes do livro atualizado:
                                    Nome: %s
                                    Sinopse: %s
                                    Autor: %s
                                    ISBN: %d
                                    Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                            livroAtualizado.getIsbn(), livroAtualizado.getTipo()));
                    aguardandoEnter();
                } else {
                    write("Operacao concelada.");
                    aguardandoEnter();
                }
            }

            if (op == 3) {
                Integer isbn = Integer.parseInt(reader("Digite a ISBN do livro:\n>"));
                Livro l = livroService.buscarLivro(isbn);

                write(String.format("""
                        Informacoes do livro:
                        Nome: %s
                        Sinopse: %s
                        Autor: %s
                        ISBN: %d
                        Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo()));
                aguardandoEnter();

                String autor = reader("Digite o novo autor:\n>");

                String confirmacao = reader("""
                        Deseja realmente atualizar esse livro?
                        >""").toUpperCase();

                if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
                    Livro livroAtualizado = livroService.atualizarLivro(isbn, null, null, autor);

                    write(String.format("""
                                    Informacoes do livro atualizado:
                                    Nome: %s
                                    Sinopse: %s
                                    Autor: %s
                                    ISBN: %d
                                    Tipo: %s""", livroAtualizado.getNome(), livroAtualizado.getSinopse(), livroAtualizado.getAutor(),
                            livroAtualizado.getIsbn(), livroAtualizado.getTipo()));
                    aguardandoEnter();
                } else {
                    write("Operacao concelada.");
                    aguardandoEnter();
                }
            }

            if (op == 0) break;
        }
    }

    private void buscarLivro() {
        limparTela();
        write("═══════════════ BUSCA DE LIVROS ═══════════════");
        String busca = reader("Busque pelo nome, autor ou ISBN\n>");

        livroService
                .listarLivros()
                .stream()
                .filter(l -> l.getIsbn() == Integer.parseInt(busca)
                        || l.getNome().contains(busca)
                        || l.getAutor().contains(busca))
                .forEach(l -> {
                    Estoque e = estoqueService.buscarIdLivro(l.getId());
                    write(String.format("""
                                Informacoes do livro:
                                ID: %d
                                Nome: %s
                                Sinopse: %s
                                Autor: %s
                                ISBN: %d
                                Tipo: %s
                                Estoque: %d""", l.getId(), l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo(), e.getQuantidade()));
                });

        if (!livroService.listarLivros().isEmpty()) {
            livroService
                    .listarLivros()
                    .stream()
                    .filter(l -> l.getIsbn() == Integer.parseInt(busca)
                            || l.getNome().contains(busca)
                            || l.getAutor().contains(busca))
                    .forEach(l -> write(String.format("""
                                Informacoes do livro:
                                Nome: %s
                                Sinopse: %s
                                Autor: %s
                                ISBN: %d
                                Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo())));
            aguardandoEnter();
        } else {
            write("ERROR: Nenhum livro encontrado.");
            aguardandoEnter();
        }
    }

    private void listarLivros() {
        if (!livroService.listarLivros().isEmpty()) {
            write("═══════════════ LISTA DE LIVROS ═══════════════");
            livroService
                    .listarLivros()
                    .forEach(l -> write(String.format("""
                            Informacoes do livro:
                            Nome: %s
                            Sinopse: %s
                            Autor: %s
                            ISBN: %d
                            Tipo: %s""", l.getNome(), l.getSinopse(), l.getAutor(), l.getIsbn(), l.getTipo())));
            aguardandoEnter();
        } else {
            write("ERROR: Nenhum livro no sistema encontrado.");
            aguardandoEnter();
        }
    }

    private void gerenciarCategoria(Funcionario funcionario) {
        categoriaService.validacaoGerente(funcionario);

        while (true) {
            limparTela();
            menuGerenciarCategoria();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) cadastrarCategoria();
            if (op == 2) removerCategoria();
            if (op == 3) buscarCategoria();
            if (op == 4) listarCategoria();
            if (op == 0) break;
        }
    }

    private void cadastrarCategoria() {
        limparTela();
        write("═══════════════ REGISTRAR CATEGORIA ═══════════════");
        String tipo = reader("Tipo da categoria:");
        String genero = reader("Generos:");

        Integer idCategoria = categoriaService.registrarCategoria(tipo, genero);
        Categoria c = categoriaService.buscarCategoria(idCategoria);

        write(String.format("""
                Informacoes da categoria:
                ID: %d
                Tipo: %s
                Genero: %s
                """, c.getId(), c.getTipo(), c.getGenero()));
        aguardandoEnter();

    }

    private void removerCategoria() {
        limparTela();
        write("═══════════════ REMOVER CATEGORIA ═══════════════");
        Integer idCategoria = Integer.parseInt(reader("Digite o ID da categoria:\n>"));
        Categoria c = categoriaService.buscarCategoria(idCategoria);

        write(String.format("""
                Informacoes da categoria:
                ID: %d
                Tipo: %s
                Genero: %s
                """, c.getId(), c.getTipo(), c.getGenero()));

        String confirmacao = reader("""
                Deseja realmente excluir essa categoria?
                AVISO: Ao confirmar, todos os dados desta categoria serao removidas.
                >""").toUpperCase();

        if (confirmacao.equals("S") || confirmacao.equals("SIM")) {
            categoriaService.removerCategoria(idCategoria);
            write("Operacao concluida.");
            aguardandoEnter();
        } else {
            write("Operacao concelada.");
            aguardandoEnter();
        }
    }

    private void buscarCategoria() {
        limparTela();
        write("═══════════════ BUSCAR CATEGORIA ═══════════════");
        String buscaCategoria = reader("Busque pelo: ID da categoria, nome do tipo ou genero\n>");

        if (!categoriaService.listarCategoria().isEmpty()) {
            categoriaService
                    .listarCategoria()
                    .stream()
                    .filter( c -> Integer.parseInt(buscaCategoria) == c.getId()
                            || c.getTipo().contains(buscaCategoria)
                            || c.getGenero().contains(buscaCategoria))
                    .forEach(c -> write(String.format("""
                    Informacoes da categoria:
                    ID: %d
                    Tipo: %s
                    Genero: %s
                    """, c.getId(), c.getTipo(), c.getGenero())));
            aguardandoEnter();
        } else {
            write("ERROR: Nenhuma categoria entrada.");
            aguardandoEnter();
        }
    }

    private void listarCategoria() {
        if (!livroService.listarLivros().isEmpty()) {
            write("═══════════════ LISTA DE CATEGORIAS ═══════════════");
            categoriaService
                    .listarCategoria()
                    .forEach(c -> write(String.format("""
                            Informacoes do livro:
                            ID: %d
                            Tipo: %s
                            Genero: %s""", c.getId(), c.getTipo(), c.getGenero())));
            aguardandoEnter();
        } else {
            write("ERROR: Nenhum livro no sistema encontrado.");
            aguardandoEnter();
        }
    }

    private void gerenciarEstoque(Funcionario funcionario) {
        while (true) {
            limparTela();
            menuGerenciarEstoque();
            int op = Integer.parseInt(reader(">"));

            if (op == 1) registrarAbastecimentoEstoque(funcionario);
            if (op == 2) registrarEmprestimoEstoque(funcionario);
            if (op == 3) buscarRegistroEstoque();
            if (op == 4) listarRegistrosEstoque();;
            if (op == 0) break;
        }
    }

    private void registrarAbastecimentoEstoque(Funcionario funcionario) {
        limparTela();
        write("═══════════════ REGISTRAR ABASTECIMENTO ═══════════════");
        Integer idEstoque = Integer.parseInt(reader("ID estoque:"));
        Integer quant = Integer.parseInt(reader("Quantidade do abastecimento:"));
        String desc = reader("Descricao:");

        estoqueService.registrarAbastecimento(idEstoque, funcionario.getId(), desc, quant);
        aguardandoEnter();
    }

    private void registrarEmprestimoEstoque(Funcionario funcionario) {
        limparTela();
        write("═══════════════ REGISTRAR EMPRESTIMO ═══════════════");
        int idEstoque = Integer.parseInt(reader("ID estoque:"));
        int quant = Integer.parseInt(reader("Quantidade de livros:"));
        String cpfCliente = reader("CPF do cliente:");

        int diaEntrega = Integer.parseInt(reader("Dia para entrega (Somente numeros)\n>:"));
        int mesEntrega = Integer.parseInt(reader("Mes para entrega (Somente numeros)\n>:"));

        String desc = reader("Descricao:");

        Cliente c = clienteService.buscarCliente(cpfCliente);
        LocalDate entrega = LocalDate.of(diaEntrega, mesEntrega, Year.now().getValue());

        estoqueService.registrarEmprestimo(idEstoque, funcionario.getId(), desc, c.getId(), entrega, quant);
        aguardandoEnter();
    }

    private void buscarRegistroEstoque() {
        limparTela();
        write("═══════════════ BUSCA SIMPLES ═══════════════");
        List<String> registers = new ArrayList<>();
        int idRegistro = Integer.parseInt(reader("Busque pelo: ID do registro, funcionario ou cliente:\n>"));

        for (Registros r : estoqueService.listarRegistros()) {
            if (idRegistro == r.getId()
                    || idRegistro == r.getId_funcionario()
                    || idRegistro == r.getId_cliente()) {

                String body = String.format("[%s] ID: %d | ESTOQUE: %d | FUNC: %d | TIPO: %s | CLIENTE: %s | ENTREGA: %s",
                        r.getData(), r.getId(), r.getId_estoque(), r.getId_funcionario(),
                        r.getTipo(), r.getId_cliente(), r.getDataEntrega());

                registers.add((body));
            }
        }

        if (!registers.isEmpty()) {
            registers.forEach(System.out::println);
            aguardandoEnter();
        } else {
            write("ERRO: Registro nao encontrado");
            aguardandoEnter();
        }
    }

    private void listarRegistrosEstoque() {
        if (!estoqueService.listarRegistros().isEmpty()) {
            write("═══════════════ LISTA DE REGISTROS ═══════════════");
            estoqueService
                    .listarRegistros()
                    .forEach(r -> write(String.format("[%s] ID: %d | ESTOQUE: %d | FUNC: %d | TIPO: %s | CLIENTE: %s | ENTREGA: %s",
                            r.getData(), r.getId(), r.getId_estoque(), r.getId_funcionario(),
                            r.getTipo(), r.getId_cliente(), r.getDataEntrega())));
            aguardandoEnter();
        } else {
            write("ERROR: Nenhum livro no sistema encontrado.");
            aguardandoEnter();
        }
    }

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
                ║   6 - Gerenciar categoria              ║
                ║   7 - Gerenciar estoque                ║
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

    private void menuGerenciarCategoria() {
        write("""
                ╔════════════════════════════════════════╗
                ║         GERENCIAR CATEGORIAS           ║
                ╠════════════════════════════════════════╣
                ║   1 - Cadastrar nova categoria         ║
                ║   2 - Remover categoria                ║         ║
                ║   3 - Buscar categoria                 ║
                ║   4 - Listar categorias                ║
                ║   0 - Sair                             ║
                ╚════════════════════════════════════════╝""");
    }

    private void menuGerenciarEstoque() {
        write("""
                ╔════════════════════════════════════════╗
                ║          GERENCIAR ESTOQUE             ║
                ╠════════════════════════════════════════╣
                ║   1 - Registrar abastecimento          ║
                ║   2 - Registrar emprestimo             ║
                ║   3 - Buscar registro                  ║
                ║   4 - Listar registros                 ║
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
