package services;

import dao.LivroDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.LivroExistenteException;
import model.Funcionario;
import model.Livro;

import java.util.List;
import java.util.NoSuchElementException;

public class LivroService {
    private final LivroDAO livroDAO = new LivroDAO();

    public Integer cadastrarLivro(String nome, String sinopse, String autor,
                                  Integer isbn, String tipo, Integer idCategoria, Integer idContrato) {

        if (livroDAO.findByName(nome).isPresent() || livroDAO.findByIsbn(isbn).isPresent()) {
            throw new LivroExistenteException("Este nome/isbn ja esta associado a um livro.");
        }

        Livro l = new Livro();
        l.setNome(nome);
        l.setSinopse(sinopse);
        l.setAutor(autor);
        l.setIsbn(isbn);
        l.setTipo(tipo);
        l.setId_categoria(idCategoria);
        l.setId_contrato(idContrato);

        return livroDAO.insert(l);
    }

    public Livro buscarLivro(Integer isbn) {
        return livroDAO.findByIsbn(isbn)
                .orElseThrow(() -> new NoSuchElementException("Livro nao encontrado"));
    }

    public Livro buscarLivroId(Integer id) {
        return livroDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Livro nao encontrado"));
    }

    public Livro atualizarLivro(Integer isbn, String nome, String sinopse, String autor) {

        if (livroDAO.findByName(nome).isPresent()) {
            throw new LivroExistenteException("Este nome/isbn ja esta associado a um livro.");
        }

        Livro l = buscarLivro(isbn);
        if (nome != null) l.setNome(nome);
        if (sinopse != null) l.setSinopse(sinopse);
        if (autor != null) l.setAutor(autor);

        livroDAO.update(l);
        return l;
    }

    public void removerLivro(Integer isbn) {
        buscarLivro(isbn);
        livroDAO.delete(isbn);
    }

    public List<Livro> listarLivros() {
        return livroDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
