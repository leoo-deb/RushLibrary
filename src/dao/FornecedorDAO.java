package dao;

import model.Fornecedor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class FornecedorDAO extends DAO<Fornecedor, Integer> {

    @Override
    public Integer insert(Fornecedor entity) {
        String sql = """
                INSERT INTO Fornecedor (id_fornc, titular_empresa, cnpj_empresa, codigo_contrato)
                VALUES (?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getTitular());
            ps.setString(3, entity.getCnpj());
            ps.setInt(4, entity.getCODIGO_CONTRATO());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("Nao foi possivel recuperar o ID.");
    }

    @Override
    public void update(Fornecedor entity) {
        String sql = """
                UPDATE Fornecedor
                SET nome_empresa = ?, titular_empresa = ?, cnpj_empresa = ?
                WHERE id_fornc = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getTitular());
            ps.setString(3, entity.getCnpj());
            ps.setInt(4, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE
                FROM Fornecedor
                WHERE id_fornc = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Fornecedor> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Fornecedor
                WHERE id_fornc = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Fornecedor fornecedor = new Fornecedor();

                    fornecedor.setId(rs.getInt(1));
                    fornecedor.setNome(rs.getString(2));
                    fornecedor.setTitular(rs.getString(3));
                    fornecedor.setCnpj(rs.getString(4));
                    fornecedor.setCODIGO_CONTRATO(rs.getInt(5));

                    return Optional.of(fornecedor);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Fornecedor> findByName(String name) {
        String sql = """
                SELECT *
                FROM Fornecedor
                WHERE nome_empresa = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Fornecedor fornecedor = new Fornecedor();

                    fornecedor.setId(rs.getInt(1));
                    fornecedor.setNome(rs.getString(2));
                    fornecedor.setTitular(rs.getString(3));
                    fornecedor.setCnpj(rs.getString(4));
                    fornecedor.setCODIGO_CONTRATO(rs.getInt(5));

                    return Optional.of(fornecedor);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Fornecedor> findByCnpj(String cnpj) {
        String sql = """
                SELECT *
                FROM Fornecedor
                WHERE cnpj_empresa = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, cnpj);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Fornecedor fornecedor = new Fornecedor();

                    fornecedor.setId(rs.getInt(1));
                    fornecedor.setNome(rs.getString(2));
                    fornecedor.setTitular(rs.getString(3));
                    fornecedor.setCnpj(rs.getString(4));
                    fornecedor.setCODIGO_CONTRATO(rs.getInt(5));

                    return Optional.of(fornecedor);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Fornecedor> findAll() {
        String sql = """
                SELECT *
                FROM Fornecedor""";
        List<Fornecedor> empresas = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    empresas.add(resultSetForncedor(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empresas;
    }

    private Fornecedor resultSetForncedor(ResultSet rs) throws SQLException {
        Fornecedor fornecedor = new Fornecedor();

        fornecedor.setId(rs.getInt(1));
        fornecedor.setNome(rs.getString(2));
        fornecedor.setTitular(rs.getString(3));
        fornecedor.setCnpj(rs.getString(4));
        fornecedor.setCODIGO_CONTRATO(rs.getInt(5));

        return fornecedor;
    }
}
