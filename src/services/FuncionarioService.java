package services;

import dao.FuncionarioDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.FuncionarioExistenteException;
import model.Funcionario;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class FuncionarioService {
    private final FuncionarioDAO funcionarioDAO;

    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAO();
    }

    public Integer cadastrarFuncionario(String nome, String cpf, String email,
                                            String cargo, LocalDate admissao) {

        if (funcionarioDAO.findByCPF(cpf).isPresent()) {
            throw new FuncionarioExistenteException("Este CPF ja esta associado a um funcionario.");
        }

        Funcionario f = new Funcionario();
        f.setNome(nome);
        f.setCpf(cpf);
        f.setEmail(email);
        f.setCargo(cargo);
        f.setAdimissao(admissao);

        return funcionarioDAO.insert(f);
    }

    public Funcionario buscarFuncionario(String cpf) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        String newCpf = cpf.replaceAll("\\D", "");

        return funcionarioDAO.findByCPF(newCpf)
                .orElseThrow(() -> new NoSuchElementException("CPF nao encontrado."));
    }

    public Funcionario buscarFuncionario(Integer id) {
        return funcionarioDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("CPF nao encontrado."));
    }

    public void atualizarDados(String cpf, String nome, String email) {
        if (cpf.matches("\\d{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        Funcionario f = buscarFuncionario(cpf);
        if (!nome.isEmpty()) f.setNome(nome);
        if (!email.isEmpty()) f.setEmail(email);

        funcionarioDAO.update(f);
    }

    public void realizarPromocao(String cpf, String cargo) {
        if (cpf.matches("\\d{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        Funcionario f = buscarFuncionario(cpf);
        if (f.getCargo().equals(cargo)) throw new IllegalArgumentException("Este funcionario ja esta com este cargo atribuido.");
        f.setCargo(cargo);

        funcionarioDAO.update(f);
    }

    public void removerFuncionario(String cpf) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        buscarFuncionario(cpf);
        funcionarioDAO.deleteByCPF(cpf);
    }

    public List<Funcionario> listarFuncionario() {
        return funcionarioDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
