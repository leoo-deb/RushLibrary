package model;

import exceptions.FormatoIncorretoException;

public class Empresa {
    private Integer id;
    private String nome;
    private String titular;
    private String cnpj;

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
        if (!nome.matches("[A-Za-z\\d]{3,30}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }
        this.nome = nome;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        if (!titular.matches("[A-Za-z]\\s[A-Za-z]{3,30}")) {
            throw new FormatoIncorretoException("Titular invalido.");
        }
        this.titular = titular;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        if (!cnpj.matches("(\\./-)?\\d{14,50}")) {
            throw new FormatoIncorretoException("CNPJ invalida.");
        }
        this.cnpj = cnpj;
    }
}
