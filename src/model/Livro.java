package model;

import exceptions.FormatoIncorretoException;

public class Livro {
    private Integer id;
    private String nome;
    private String sinopse;
    private String autor;
    private Integer isbn;
    private String tipo;

    private Integer id_categoria;
    private Integer id_contrato;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (!nome.matches(".{1,15}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }
        this.nome = nome;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        if (!sinopse.matches(".{3,200}")) {
            throw new IllegalArgumentException("Sinopse invalida.");
        }
        this.sinopse = sinopse;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        if (!autor.matches(".{3,20}")) {
            throw new IllegalArgumentException("Autor invalid.");
        }
        this.autor = autor;
    }

    public Integer getIsbn() {
        return isbn;
    }

    public void setIsbn(Integer isbn) {
        this.isbn = isbn;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (!tipo.matches(".{3,15}")) {
            throw new IllegalArgumentException("Autor invalid.");
        }
        this.tipo = tipo;
    }

    public Integer getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Integer id_categoria) {
        this.id_categoria = id_categoria;
    }

    public Integer getId_contrato() {
        return id_contrato;
    }

    public void setId_contrato(Integer id_contrato) {
        this.id_contrato = id_contrato;
    }
}
