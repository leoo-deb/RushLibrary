package services;

import dao.ContradoDAO;
import exceptions.AcessoNegadoException;
import model.Contrato;
import model.Empresa;
import model.Funcionario;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class ContratoService {
    private final ContradoDAO contradoDAO = new ContradoDAO();

    public Integer registrarContrato(Funcionario funcionario, String tipo, LocalDate vigencia,
                                     LocalDate vencimento, Empresa empresa) {

        if (!funcionario.getCargo().equals(Funcionario.Cargo.GERENTE)) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }

        Contrato c = new Contrato();
        c.setTipo(tipo);
        c.setVigencia(vigencia);
        c.setVencimento(vencimento);
        c.setId_empresa(empresa.getId());

        return contradoDAO.insert(c);
    }

    public Contrato buscarContrato(Integer code) {
        return contradoDAO.findById(code)
                .orElseThrow(() -> new NoSuchElementException("Contrato nao encontrado."));
    }

    public Contrato atualizarDatas(Funcionario funcionario, Integer code, LocalDate vigencia, LocalDate vencimento) {
        if (!funcionario.getCargo().equals(Funcionario.Cargo.GERENTE)) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }

        Contrato c = buscarContrato(code);
        c.setVigencia(vigencia);
        c.setVencimento(vencimento);

        contradoDAO.update(c);
        return c;
    }

    public void removerContrato(Funcionario funcionario, Integer code) {
        if (!funcionario.getCargo().equals(Funcionario.Cargo.GERENTE)) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }

        buscarContrato(code);
        contradoDAO.delete(code);
    }

    public List<Contrato> listarContratos(Funcionario funcionario) {
        if (!funcionario.getCargo().equals(Funcionario.Cargo.GERENTE)) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }

        return contradoDAO.findAll();
    }
}
