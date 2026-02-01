package model;

import exceptions.FormatoIncorretoException;

public class Fornecedor {
    private Integer id;
    private String nome;
    private String titular;
    private String cnpj;
    private Integer CODIGO_CONTRATO;

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
        this.nome = nome;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Integer getCODIGO_CONTRATO() {
        return CODIGO_CONTRATO;
    }

    public void setCODIGO_CONTRATO(Integer CODIGO_CONTRATO) {
        this.CODIGO_CONTRATO = CODIGO_CONTRATO;
    }
}
