package services;

import dao.CategoriaDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import model.Categoria;
import model.Funcionario;

import java.util.List;
import java.util.NoSuchElementException;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public Integer registrarCategoria(String tipo, String genero) {

        Categoria c = new Categoria();
        c.setTipo(tipo);
        c.setGenero(genero);

        return categoriaDAO.insert(c);
    }

    public Categoria buscarCategoria(Integer id) {
        return categoriaDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoria nao encontrada."));
    }

    public void atualizarCategoria(Integer id, String tipo, String genero) {
        Categoria c = buscarCategoria(id);

        c.setTipo(tipo);
        c.setGenero(genero);
        categoriaDAO.update(c);
    }

    public void removerCategoria(Integer id) {

        buscarCategoria(id);
        categoriaDAO.delete(id);
    }

    public List<Categoria> listarCategoria() {
        return categoriaDAO.findAll();
    }


    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
