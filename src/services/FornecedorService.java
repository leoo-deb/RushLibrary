package services;

import dao.ContradoDAO;
import dao.FornecedorDAO;
import exceptions.AcessoNegadoException;
import exceptions.EmpresaExistenteException;
import exceptions.FormatoIncorretoException;
import model.Contrato;
import model.Fornecedor;
import model.Funcionario;

import javax.xml.transform.sax.SAXResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import static utils.ConnectFactory.getConnection;

public class FornecedorService {
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final ContradoDAO contradoDAO = new ContradoDAO();

    public Integer registrarFornecedor(String nome, String titular, String cnpj, String tipoContrato,
                                       LocalDate vigencia, LocalDate vencimento) {
        // — > Validações de entrada
        if (!nome.matches(".{3,30}")) {
            throw new FormatoIncorretoException("Nome invalido.");
        }

        if (!titular.matches("[A-Za-z]+\\s+[A-Za-z]{3,30}")) {
            throw new FormatoIncorretoException("Titular invalido.");
        }

        if (!cnpj.matches("(\\./-)?\\d{14,50}")) {
            throw new FormatoIncorretoException("CNPJ invalida.");
        }

        if (fornecedorDAO.findByName(nome).isPresent()) {
            throw new EmpresaExistenteException("Este nome ja esta associado a um fornecedor.");
        }

        if (vigencia.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Nao e possivel por vigencias futuras.");
        }

        if (vencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Nao e possivel por vencimentos passados.");
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                Contrato c = new Contrato();
                c.setTipo(tipoContrato);
                c.setVigencia(vigencia);
                c.setVencimento(vencimento);
                Integer idContrato = contradoDAO.insert(c);

                Fornecedor e = new Fornecedor();
                e.setNome(nome);
                e.setTitular(titular);
                e.setCnpj(cnpj);
                e.setCODIGO_CONTRATO(idContrato);

                return fornecedorDAO.insert(e);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Contrato atualizarVencimento(Fornecedor fornecedor, LocalDate vencimento) {
        if (vencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Nao e possivel por vencimentos passados.");
        }

        Contrato c = contradoDAO.findById(fornecedor.getCODIGO_CONTRATO()).orElseThrow();
        c.setVencimento(vencimento);

        contradoDAO.update(c);
        return c;
    }

    public Fornecedor buscarFornecedor(Integer id) {
        return fornecedorDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa nao encontrada."));
    }

    public Contrato buscarContrato(Fornecedor fornecedor) {
        return contradoDAO.findById(fornecedor.getCODIGO_CONTRATO())
                .orElseThrow(() -> new NoSuchElementException("Contrato nao encontrado."));
    }

    public List<Fornecedor> buscaFiltradaFornecedor(String busca) {
        return fornecedorDAO
                .findAll()
                .stream()
                .filter(f -> busca.contains(f.getNome())
                        || busca.contains(f.getTitular())
                        || busca.contains(f.getCnpj()))
                .toList();
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorDAO.findAll();
    }

    public void validacaoGerente(Funcionario funcionario) {
        if (!funcionario.getCargo().equals("GERENTE")) {
            throw new AcessoNegadoException("Voce nao possui permissao esta acao.");
        }
    }
}
