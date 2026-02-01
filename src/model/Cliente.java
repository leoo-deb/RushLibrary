package model;

import java.time.LocalDate;

public class Cliente {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String numero;
    private LocalDate dataCadastro;
    private Integer quantidadeLivro = 0;

    public void emprestimoLivro(int valor) {
        quantidadeLivro += valor;
    }

    public void retornoLivro(int valor) {
        quantidadeLivro -= valor;
    }

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getQuantidadeLivro() {
        return quantidadeLivro;
    }

    public void setQuantidadeLivro(Integer quantidadeLivro) {
        this.quantidadeLivro = quantidadeLivro;
    }
}
