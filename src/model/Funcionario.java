package model;

import java.time.LocalDate;
import java.util.Date;

public class Funcionario {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private Cargo cargo;
    private LocalDate adimissao;

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

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public LocalDate getAdimissao() {
        return adimissao;
    }

    public void setAdimissao(LocalDate adimissao) {
        this.adimissao = adimissao;
    }

    public enum Cargo {
        GERENTE, COMUM
    }

}
