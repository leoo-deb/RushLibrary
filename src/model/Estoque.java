package model;

import exceptions.FormatoIncorretoException;

public class Estoque {
    private Integer id;
    private Integer id_livro;
    private Integer quantidade;

    public void entrada(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor invalido.");
        quantidade += valor;
    }

    public void saida(int valor) {
        if (valor <= 0) throw new IllegalArgumentException("Valor invalido.");
        if (valor > quantidade) throw new IllegalArgumentException("Estoque insuficiente para saida.");
        quantidade -= valor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId_livro() {
        return id_livro;
    }

    public void setId_livro(Integer id_livro) {
        this.id_livro = id_livro;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        if (quantidade <= 0) throw new FormatoIncorretoException("Valor invalido.");
        this.quantidade = quantidade;
    }
}
