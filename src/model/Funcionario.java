package model;

import exceptions.FormatoIncorretoException;

import java.time.LocalDate;
import java.util.Date;

public class Funcionario {
    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String cargo;
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
        if (nome.matches("[A-Za-z]\\s[A-Za-z]{3,45}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (!cpf.matches("\\d{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}")) {
            throw new FormatoIncorretoException("Email invalido.");
        }
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getAdimissao() {
        return adimissao;
    }

    public void setAdimissao(LocalDate adimissao) {
        this.adimissao = adimissao;
    }

    public enum Cargo {
        GERENTE ("GERENTE"),
        COMUM ("COMUM");

        private final String cargo;

        Cargo(String cargo) {
            this.cargo = cargo;
        }

        @Override
        public String toString() {
            return cargo;
        }
    }

}
