package services;

import dao.*;
import model.*;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class EstoqueService {
    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private final RegistrosDAO registrosDAO = new RegistrosDAO();

    public Integer cadastrarEstoque(Integer idLivro, Integer quantidade) {
        Estoque e = new Estoque();
        e.setId_livro(idLivro);
        e.setQuantidade(quantidade);

        return estoqueDAO.insert(e);
    }

    public Estoque buscarEstoque(Integer id) {
        return estoqueDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque nao encontrado."));
    }

    public Estoque buscarIdLivro(Integer id) {
        return estoqueDAO.findByIdLivro(id)
                .orElseThrow(() -> new NoSuchElementException("Estoque nao encontrado."));
    }

    public void removerEstoque(Integer idLivro) {
        estoqueDAO.delete(idLivro);
    }

    public List<Estoque> listarEstoques() {
        return estoqueDAO.findAll();
    }

    public void registrarEmprestimo(Integer idEstoque, Integer idFuncionario, String desc,
                                    Integer idCliente, LocalDate entrega, Integer quant) {
        buscarEstoque(idEstoque).saida(quant);

        Registros r = new Registros();
        r.setId_estoque(idEstoque);
        r.setId_funcionario(idFuncionario);
        r.setDescricao(desc);
        r.setTipo("EMPRESTIMO");
        r.setId_cliente(idCliente);
        r.setDataEntrega(entrega);

        registrosDAO.insert(r);
    }

    public void registrarAbastecimento(Integer idEstoque, Integer idFuncionario, String desc, Integer quant) {
        buscarEstoque(idEstoque).entrada(quant);

        Registros r = new Registros();
        r.setId_estoque(idEstoque);
        r.setId_funcionario(idFuncionario);
        r.setDescricao(desc);
        r.setTipo("ABASTECIMENTO");
        r.setId_cliente(0);
        r.setDataEntrega(LocalDate.now());
        registrosDAO.insert(r);
    }

    public void registrarCadastro(Integer idEstoque, Integer idFuncionario, String desc) {
        Registros r = new Registros();
        r.setId_estoque(idEstoque);
        r.setId_funcionario(idFuncionario);
        r.setDescricao(desc);
        r.setTipo("CADASTRO");
        r.setId_cliente(0);
        r.setDataEntrega(LocalDate.now());

        registrosDAO.insert(r);
    }

    public List<Registros> listarRegistros() {
        return registrosDAO.findAll();
    }
}
