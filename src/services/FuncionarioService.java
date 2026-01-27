package services;

import dao.FuncionarioDAO;
import exceptions.FormatoIncorretoException;
import exceptions.FuncionarioExistenteException;
import model.Funcionario;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public Integer cadastrarFuncionario(String nome, String cpf, String email,
                                            Funcionario.Cargo cargo, LocalDate admissao) {
        String justNumber = cpf.replaceAll("\\D", "");

        if (nome.matches("[A-Za-z]\\s[A-Za-z]{3,}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }

        if (justNumber.matches("\\d{11}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        if (email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}")) {
            throw new FormatoIncorretoException("Email invalido.");
        }

        if (funcionarioDAO.findByCPF(cpf).isPresent()) {
            throw new FuncionarioExistenteException("Ja possui um cadastro neste CPF.");
        }

        Funcionario f = new Funcionario();
        f.setNome(nome);
        f.setCpf(justNumber);
        f.setEmail(email);
        f.setCargo(cargo);
        f.setAdimissao(admissao);

        return funcionarioDAO.insert(f);
    }

    public Funcionario buscarFuncionario(String cpf) {
        if (cpf.matches("\\d{11}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        return funcionarioDAO.findByCPF(cpf).orElseThrow();
    }

    public void atualizarDados(Funcionario f, String nome, String email, Funcionario.Cargo cargo) {

        if (!nome.isEmpty()) {
            if (!nome.matches("[A-Za-z]\\s[A-Za-z]{3,}")) {
                throw new FormatoIncorretoException("Nome invalido.");
            }

            f.setNome(nome);
        }

        if (!email.isEmpty()) {
            if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                throw new FormatoIncorretoException("Email invalido.");
            }

            f.setEmail(email);
        }

        f.setCargo(cargo);
        funcionarioDAO.update(f);
    }

    public void removerFuncionario(String cpf) {
        funcionarioDAO.deleteByCPF(cpf);
    }

    public List<Funcionario> listarFuncionario() {
        return funcionarioDAO.findAll();
    }
}
