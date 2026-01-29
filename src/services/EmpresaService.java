package services;

import dao.EmpresaDAO;
import exceptions.AcessoNegadoException;
import exceptions.EmpresaExistenteException;
import exceptions.FormatoIncorretoException;
import model.Empresa;
import model.Funcionario;
import javax.swing.*;
import java.util.List;
import java.util.NoSuchElementException;

public class EmpresaService {
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    public Integer registrarEmpresa(String nome, String titular, String cnpj) {

        if (empresaDAO.findByName(nome).isPresent()) {
            throw new EmpresaExistenteException("Este nome ja esta associado a uma empresa.");
        }

        Empresa e = new Empresa();
        e.setNome(nome);
        e.setTitular(titular);
        e.setCnpj(cnpj);

        return empresaDAO.insert(e);
    }

    public Empresa buscarEmpresa(Integer id) {
        return empresaDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa nao encontrada."));
    }

    public void removerEmpresa(Integer id) {

        buscarEmpresa(id);
        empresaDAO.delete(id);
    }

    public List<Empresa> listarEmpresas() {
        return empresaDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
