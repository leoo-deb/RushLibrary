package services;

import dao.ClienteDAO;
import dao.EstoqueDAO;
import dao.LivroDAO;
import dao.MovimentacaoDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.LivroExistenteException;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static utils.ConnectFactory.getConnection;

public class LivroService {
    private final LivroDAO livroDAO = new LivroDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    public void cadastrarLivro(Funcionario funcionario, String nome, String sinopse, String autor,
                               Integer isbn, String genero, String tipo, Fornecedor fornecedor, Integer quant) {
        // — > Validações de entrada
        if (livroDAO.findByName(nome).isPresent() || livroDAO.findByIsbn(isbn).isPresent()) {
            throw new LivroExistenteException("Este nome/isbn ja esta associado a um livro.");
        }

        if (!nome.matches(".{1,15}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }

        if (!sinopse.matches(".{3,200}")) {
            throw new IllegalArgumentException("Sinopse invalida.");
        }

        if (!autor.matches(".{3,20}")) {
            throw new IllegalArgumentException("Autor invalido.");
        }

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // — > Criação do objeto Livro para envio ao banco de dados
                Livro l = new Livro();
                l.setNome(nome);
                l.setSinopse(sinopse);
                l.setAutor(autor);
                l.setIsbn(isbn);
                l.setGenero(genero);
                l.setTipo(tipo);
                l.setId_fornecedor(fornecedor.getId());
                Integer idLivro = livroDAO.insert(l);

                // — > Criação do objeto Estoque com a recuperação do idLivro e o envio ao banco de dados
                Estoque e = new Estoque();
                e.setId_livro(idLivro);
                e.setQuantidade(quant);
                Integer idEstoque = estoqueDAO.insert(e);

                // — > Criação do objeto Movimentação e o envio de uma nova movimentação ao banco de dados
                Movimentacao m = new Movimentacao();
                m.setId_estoque(idEstoque);
                m.setId_funcionario(funcionario.getId());
                m.setQuant(quant);
                m.setTipo("ENTRADA_COMPRA");
                m.setId_cliente(1);

                movimentacaoDAO.insert(m);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registrarAbastecimento(Estoque estoque, Funcionario funcionario, Integer quant) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                // — > Criação do objeto para envio de uma nova movimentação ao banco de dados
                Movimentacao r = new Movimentacao();
                r.setId_estoque(estoque.getId());
                r.setId_funcionario(funcionario.getId());
                r.setQuant(quant);
                r.setTipo("ENTRADA_COMPRA");

                // — > Atualização da quantidade atual do estoque
                estoque.entrada(quant);

                // — > Update e insert dos dados dos objetos para o banco
                estoqueDAO.update(estoque);
                movimentacaoDAO.insert(r);

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro durante a transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registrarEmprestimo(Estoque estoque, Funcionario funcionario, Cliente cliente,
                                    LocalDate entregaPrevista, Integer quant) {
        // — > Validações de entrada
        if (entregaPrevista.isBefore(LocalDate.now())) {
            throw new FormatoIncorretoException("Nao e possivel por datas passadas.");
        }

        if (entregaPrevista.isAfter(LocalDate.now().plusMonths(1))) {
            throw new FormatoIncorretoException("Nao e possivel por datas que ultrapassam 1 mes.");
        }

        if (quant > 5) {
            throw new IllegalArgumentException("Limite de livro excedido. O maximo de livro por cliente e 5.");
        }

        if (cliente.getQuantidadeLivro() == 5) {
            throw new IllegalArgumentException("Este cliente ja possui 5 emprestimos. O maximo de emprestimo por cliente e 5.");
        }

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // — > Criação do objeto para envio de uma nova movimentação ao banco de dados
                Movimentacao r = new Movimentacao();
                r.setId_estoque(estoque.getId());
                r.setId_funcionario(funcionario.getId());
                r.setQuant(quant);
                r.setTipo("SAIDA_EMPRESTIMO");
                r.setId_cliente(cliente.getId());
                r.setDataPrevista(entregaPrevista);
                r.setStatus("ABERTO");

                // — > Atualização dos objetos
                estoque.saida(quant);
                cliente.emprestimoLivro(quant);

                // — > Update e insert dos dados dos objetos para o banco
                estoqueDAO.update(estoque);
                clienteDAO.update(cliente);
                movimentacaoDAO.insert(r);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro durante a transferência. Transação cancelada.", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registrarRetorno(Movimentacao movimentacao, Estoque estoque, Funcionario funcionario,
                                 Cliente cliente, LocalDate entregaReal, Integer quant) {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                estoque.entrada(quant);
                cliente.retornoLivro(quant);

                movimentacao.setId_funcionario(funcionario.getId());
                movimentacao.setDataEntrega(entregaReal);
                movimentacao.setStatus("DEVOLVIDO");

                Movimentacao novaMovi = new Movimentacao();
                novaMovi.setId_estoque(estoque.getId());
                novaMovi.setId_funcionario(funcionario.getId());
                novaMovi.setQuant(quant);
                novaMovi.setTipo("RETORNO_EMPRESTIMO");
                novaMovi.setId_cliente(cliente.getId());
                novaMovi.setDataPrevista(movimentacao.getDataPrevista());
                novaMovi.setDataEntrega(entregaReal);
                novaMovi.setStatus("DEVOLVIDO");

                estoqueDAO.update(estoque);
                clienteDAO.update(cliente);
                movimentacaoDAO.update(movimentacao);
                movimentacaoDAO.insert(novaMovi);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro durante a transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Livro atualizarLivro(Livro isbnLivro, String nome, String sinopse, String autor) {

        if (nome != null) isbnLivro.setNome(nome);
        if (sinopse != null) isbnLivro.setSinopse(sinopse);
        if (autor != null) isbnLivro.setAutor(autor);

        livroDAO.update(isbnLivro);
        return isbnLivro;
    }

    public void atualizarStatusMovimentacao(Movimentacao m, LocalDate dataEntregue) {
        if (dataEntregue == null || m.getDataPrevista() == null) {
            return;
        }

        if (m.getStatus().equals("DEVOLVIDO")) {
            return;
        }

        if (LocalDate.now().equals(m.getDataPrevista().plusDays(1))){
            m.setStatus("ATRASADO");
            return;
        }

        m.setStatus("ABERTO");
        movimentacaoDAO.update(m);
    }

    public boolean verificarEmprestimoAtrasado(Cliente cliente) {
        return listarMovimentacao()
                .stream()
                .anyMatch(m -> m.getStatus().equals("ATRASADO")
                        && Objects.equals(m.getId_cliente(), cliente.getId()));
    }

    public void removerLivro(Integer isbn) {
//TODO SISTEMA DE STATUS
//        Livro l = buscarLivro(isbn);
//        registrosDAO.delete(l.getId());
//        estoqueDAO.delete(l.getId());
//        livroDAO.delete(isbn);
    }

    public Livro buscarLivro(Integer id) {
        return livroDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Livro nao encontrado"));
    }

    public Estoque buscarEstoque(Integer id) {
        return estoqueDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque nao encontrado."));
    }

    public Estoque buscarEstoqueIdLivro(Integer id) {
        return estoqueDAO.findByIdLivro(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque nao encontrado."));
    }

    public Movimentacao buscaMovimentacao(Integer id) {
        return movimentacaoDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Movimentacao nao encontrado."));
    }

    public List<Livro> buscaFiltradaLivro(String busca) {
        return livroDAO
                .findAll()
                .stream()
                .filter(l -> l.getNome().contains(busca)
                        || l.getAutor().contains(busca))
                .toList();
    }

    public List<Movimentacao> buscaFiltradaMovimentacao(int busca) {
        return movimentacaoDAO
                .findAll()
                .stream()
                .filter(m -> busca == m.getId()
                        || busca == m.getId_funcionario()
                        || busca == m.getId_cliente()
                        || busca == m.getId_estoque())
                .toList();
    }

    public List<Movimentacao> listarEmprestimoAberto(Cliente cliente) {
        return listarMovimentacao()
                .stream()
                .filter(m -> Objects.equals(m.getId_cliente(), cliente.getId()))
                .filter(m -> m.getStatus().equals("ABERTO") || m.getStatus().equals("ATRASADO"))
                .toList();
    }

    public List<Livro> listarLivros() {
        return livroDAO.findAll();
    }

    public List<Movimentacao> listarMovimentacao() {
        return movimentacaoDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}