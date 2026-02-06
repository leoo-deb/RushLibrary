package services;

import dao.AuditoriaDAO;
import dao.ClienteDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.FuncionarioExistenteException;
import model.Auditoria;
import model.Cliente;
import model.Funcionario;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static utils.ConnectFactory.getConnection;

public class ClienteService {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public Integer cadastrarCliente(Funcionario funcionario, String nome, String cpf, String email, String numero) {
        if (clienteDAO.findByCPF(cpf).isPresent()) {
            throw new FuncionarioExistenteException("Este CPF ja esta associado a um cliente.");
        }

        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        String newCpf = cpf.replaceAll("\\D", "");

        if (!nome.matches("[A-Za-z]{3,20}+\\s+[A-Za-z]{3,20}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }

        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
            throw new FormatoIncorretoException("Email invalido.");
        }

        if (!numero.matches("[\\d.]{11,25}")) {
            throw new FormatoIncorretoException("Numero invalido.");
        }
        String newNumero = numero.replaceAll("\\D", "");

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Cliente c = new Cliente();
                c.setNome(nome);
                c.setCpf(newCpf);
                c.setEmail(email);
                c.setNumero(newNumero);

                Auditoria a = new Auditoria();
                a.setTipo("CADASTRO_CLIENTE");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(c.getId());

                auditoriaDAO.insert(a);
                return clienteDAO.insert(c);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Cliente buscarCliente(String cpf) {
        if (!cpf.matches("[\\d(-.)?]{11,25}")) {
            throw new FormatoIncorretoException("CPF invalido.");
        }
        String newCpf = cpf.replaceAll("\\D", "");

        return clienteDAO.findByCPF(newCpf)
                .orElseThrow(() -> new NoSuchElementException("Cliente nao encontrado."));
    }

    public Cliente buscarCliente(Integer id) {
        return clienteDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente nao encontrado."));
    }

    public void atualizarDados(Funcionario funcionario, Cliente cliente, String nome, String email, String numero) {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (nome != null) {
                    if (!nome.matches("[A-Za-z]{3,20}+\\s+[A-Za-z]{3,20}")) {
                        throw new FormatoIncorretoException("Nome invalido.");
                    }
                    cliente.setNome(nome);
                }
                if (email != null) {
                    if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                        throw new FormatoIncorretoException("Email invalido.");
                    }
                    cliente.setEmail(email);
                }
                if (numero != null) {
                    if (!numero.matches("[\\d.]{11,25}")) {
                        throw new FormatoIncorretoException("Numero invalido.");
                    }
                    String newNumero = numero.replaceAll("\\D", "");
                    cliente.setNumero(newNumero);
                }

                Auditoria a = new Auditoria();
                a.setTipo("ATUALIZACAO_CLIENTE");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(cliente.getId());

                auditoriaDAO.insert(a);
                clienteDAO.update(cliente);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removerCliente(Funcionario funcionario, Cliente cliente) {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                Auditoria a = new Auditoria();
                a.setTipo("REMOCAO_CLIENTE");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(cliente.getId());

                auditoriaDAO.insert(a);
                clienteDAO.deleteByCpf(cliente.getCpf());
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Cliente> buscaFiltradaCliente(String busca) {
        return clienteDAO
                .findAll()
                .stream()
                .filter(c -> c.getNome().equals(busca)
                        || c.getCpf().equals(busca)
                        || c.getEmail().equals(busca))
                .toList();
    }

    public List<Cliente> listaClientes() {
        return clienteDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
