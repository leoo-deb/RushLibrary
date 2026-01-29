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

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setCpf(cpf);
        c.setEmail(email);
        c.setNumero(numero);

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

    public Cliente atualizarDados(String buscarCpf, String nome, String cpf, String email, String numero) {
        Cliente c = buscarCliente(buscarCpf);

        if (!nome.isEmpty()) c.setNome(nome);
        if (!cpf.isEmpty()) c.setCpf(cpf);
        if (!email.isEmpty()) c.setEmail(email);
        if (!numero.isEmpty()) c.setNumero(numero);

        clienteDAO.update(c);
        return c;
    }

    public void removerCliente(String cpf, Funcionario funcionario) {
        if (!cpf.matches("\\d{11,25}")) throw new FormatoIncorretoException("CPF invalido.");

        buscarCliente(cpf);
        clienteDAO.deleteByCpf(cpf);
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
