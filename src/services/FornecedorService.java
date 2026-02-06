package services;

import dao.AuditoriaDAO;
import dao.ContradoDAO;
import dao.FornecedorDAO;
import exceptions.AcessoNegadoException;
import exceptions.EmpresaExistenteException;
import exceptions.FormatoIncorretoException;
import model.Auditoria;
import model.Contrato;
import model.Fornecedor;
import model.Funcionario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import static utils.ConnectFactory.getConnection;

public class FornecedorService {
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final ContradoDAO contradoDAO = new ContradoDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public Integer registrarFornecedor(Funcionario funcionario, String nome, String titular, String cnpj,
                                       String tipoContrato, LocalDate vigencia, LocalDate vencimento) {
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

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                Contrato c = new Contrato();
                c.setTipo(tipoContrato);
                c.setVigencia(vigencia);
                c.setVencimento(vencimento);
                c.setStatus("VIGENTE");
                Integer idContrato = contradoDAO.insert(c);

                Fornecedor f = new Fornecedor();
                f.setNome(nome);
                f.setTitular(titular);
                f.setCnpj(cnpj);
                f.setCODIGO_CONTRATO(idContrato);

                Auditoria a = new Auditoria();
                a.setTipo("REGISTRO_FORNECEDOR");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(f.getCODIGO_CONTRATO());

                auditoriaDAO.insert(a);
                return fornecedorDAO.insert(f);
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Contrato atualizarVencimento(Funcionario funcionario, Fornecedor fornecedor, LocalDate vencimento) {
        if (vencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Nao e possivel por vencimentos passados.");
        }

        try (var conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                Contrato contrato = buscarContrato(fornecedor);
                if (contrato.getStatus().equals("VENCIDO") || contrato.getStatus().equals("RESCINDIDO")) {
                    throw new IllegalArgumentException("Este contrato esta vencido/rescindido.");
                }

                Auditoria a = new Auditoria();
                a.setTipo("ATUALIZACAO_CONTRATO");
                a.setID_FUNCIONARIO(funcionario.getId());
                a.setID_TIPO(fornecedor.getCODIGO_CONTRATO());

                contrato.setVencimento(vencimento);
                auditoriaDAO.insert(a);
                contradoDAO.update(contrato);
                return contrato;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erro na transferência. Transação cancelada.", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Fornecedor buscarFornecedor(Integer id) {
        return fornecedorDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Fornecedor nao encontrado."));
    }

    public Contrato buscarContrato(Fornecedor fornecedor) {
        return contradoDAO.findById(fornecedor.getCODIGO_CONTRATO())
                .orElseThrow(() -> new NoSuchElementException("Contrato nao encontrado."));
    }

    public void atualizarStatusContrato(Contrato c) {
        if (c.getStatus().equals("RESCINDIDO")) {
            return;
        }

        if (LocalDate.now().equals(c.getVencimento())) {
            c.setStatus("VENCIDO");
            return;
        }

        c.setStatus("VIGENTE");
        contradoDAO.update(c);
    }

    public List<Fornecedor> buscaFiltradaFornecedor(String busca) {
        return fornecedorDAO
                .findAll()
                .stream()
                .filter(f -> f.getNome().contains(busca)
                        || f.getTitular().contains(busca)
                        || f.getCnpj().contains(busca))
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
