package services;

import dao.AuditoriaDAO;
import dao.FuncionarioDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.FuncionarioExistenteException;
import model.Auditoria;
import model.Funcionario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static io.Output.write;
import static utils.ConnectFactory.getConnection;

public class FuncionarioService {
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public Integer cadastrarFuncionario(Funcionario funcionario, String nome, String cpf, String email,
                                        String senha, String cargo, LocalDate admissao) {
        // — > Validações de entrada
        if (!nome.matches("[A-Za-z]+\\s+[A-Za-z]{3,45}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }

        if (funcionarioDAO.findByCPF(cpf).isPresent()) {
            throw new FuncionarioExistenteException("Este CPF ja esta associado a um funcionario.");
        }

        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        String newCpf = cpf.replaceAll("\\D", "");

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
            throw new FormatoIncorretoException("Email invalido.");
        }

        if (!senha.matches("\\d{6}")) {
            throw new FormatoIncorretoException("Senha invalida.");
        }

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // — > Criação do objeto Funcionario para o envio ao banco
                Funcionario f = new Funcionario();
                f.setNome(nome);
                f.setCpf(newCpf);
                f.setEmail(email);
                f.setSenha(senha);
                f.setCargo(cargo);
                f.setAdmissao(admissao);

                Auditoria a = new Auditoria();
                a.setTipo("CADASTRO_FUNCIONARIO");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(f.getId());

                // — > Insert do objeto Funcionario no banco de dados
                auditoriaDAO.insert(a);
                return funcionarioDAO.insert(f);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Funcionario buscarFuncionario(String cpf) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        String newCpf = cpf.replaceAll("\\D", "");

        return funcionarioDAO.findByCPF(newCpf)
                .orElseThrow(() -> new NoSuchElementException("CPF nao encontrado."));
    }

    public void removerFuncionario(Funcionario funcionario, String cpf) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }

        if (cpf.equals(funcionario.getCpf())) {
            throw new IllegalArgumentException("Nao e possivel fazer a remocao de si mesmo.");
        }

        try(var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Auditoria a = new Auditoria();
                a.setTipo("REMOCAO_FUNCIONARIO");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(Integer.parseInt(cpf));


                buscarFuncionario(cpf);
                auditoriaDAO.insert(a);
                funcionarioDAO.deleteByCPF(cpf);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void atualizarDados(Funcionario funcionario, String cpf, String nome, String email, String senha) {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Funcionario f = buscarFuncionario(cpf);
                if (nome != null) f.setNome(nome);
                if (email != null) f.setEmail(email);
                if (senha != null) f.setSenha(senha);

                Auditoria a = new Auditoria();
                a.setTipo("ATUALIZACAO_CLIENTE");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(Integer.parseInt(cpf));

                auditoriaDAO.insert(a);
                funcionarioDAO.update(f);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void realizarPromocao(Funcionario funcionario, String cpf, String cargo) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        String newCpf = cpf.replaceAll("\\D", "");

        if (cpf.equals(funcionario.getCpf())) {
            throw new IllegalArgumentException("Nao e possivel fazer a promocao de si mesmo.");
        }

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Funcionario f = buscarFuncionario(newCpf);
                if (f.getCargo().equals(cargo))
                    throw new IllegalArgumentException("Este funcionario ja esta com este cargo atribuido.");
                f.setCargo(cargo);

                Auditoria a = new Auditoria();
                a.setTipo("PROMOCAO_FUNCIONARIO");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(Integer.parseInt(cpf));

                auditoriaDAO.insert(a);
                funcionarioDAO.update(f);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Funcionario> buscaFiltradaFuncionario(String busca) {
        return listarFuncionario()
                .stream()
                .filter(f -> f.getNome().equals(busca)
                        || f.getCpf().equals(busca)
                        || f.getEmail().equals(busca))
                .toList();
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
