package services;

import dao.ClienteDAO;
import exceptions.AcessoNegadoException;
import exceptions.FormatoIncorretoException;
import exceptions.FuncionarioExistenteException;
import exceptions.FuncionarioInexistenteException;
import model.Cliente;
import model.Funcionario;

import javax.sql.rowset.serial.SerialStruct;
import java.util.List;
import java.util.NoSuchElementException;

public class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    public Integer cadastrarCliente(String nome, String cpf, String email, String numero) {
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

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCpf(newCpf);
        c.setEmail(email);
        c.setNumero(newNumero);

        return clienteDAO.insert(c);
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

    public void atualizarDados(String buscarCpf, String nome, String email, String numero) {
        Cliente c = buscarCliente(buscarCpf);

        if (nome != null) {
            if (!nome.matches("[A-Za-z]{3,20}+\\s+[A-Za-z]{3,20}")) {
                throw new FormatoIncorretoException("Nome invalido.");
            }

            c.setNome(nome);
        }
        if (email != null) {
            if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
                throw new FormatoIncorretoException("Email invalido.");
            }
            c.setEmail(email);
        }
        if (numero != null) {
            if (!numero.matches("[\\d.]{11,25}")) {
                throw new FormatoIncorretoException("Numero invalido.");
            }
            String newNumero = numero.replaceAll("\\D", "");
            c.setNumero(newNumero);
        }

        clienteDAO.update(c);
    }

    public void removerCliente(String cpf) {
        if (!cpf.matches("\\d{11,25}")) throw new FormatoIncorretoException("CPF invalido.");

        buscarCliente(cpf);
        clienteDAO.deleteByCpf(cpf);
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
