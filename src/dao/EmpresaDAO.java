package dao;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import model.Empresa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class EmpresaDAO extends DAO<Empresa, Integer> {

    @Override
    public Integer insert(Empresa entity) {
        String sql = """
                INSERT INTO Empresa (nome_empresa, titular_empresa, cnpj_empresa)
                VALUES (?, ?, ?)""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getTitular());
            ps.setString(3, entity.getCnpj());
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
    public void update(Empresa entity) {
        String sql = """
                UPDATE Empresa
                SET nome_empresa = ?, titular_empresa = ?, cnpj_empresa = ?
                WHERE id_empresa = ?""";

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
                FROM Empresa
                WHERE id_empresa = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Empresa> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Empresa
                WHERE id_empresa = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Empresa empresa = new Empresa();

                    empresa.setId(rs.getInt(1));
                    empresa.setNome(rs.getString(2));
                    empresa.setTitular(rs.getString(3));
                    empresa.setCnpj(rs.getString(4));

                    return Optional.of(empresa);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<Empresa> findByName(String name) {
        String sql = """
                SELECT *
                FROM Empresa
                WHERE nome_empresa = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Empresa empresa = new Empresa();

                    empresa.setId(rs.getInt(1));
                    empresa.setNome(rs.getString(2));
                    empresa.setTitular(rs.getString(3));
                    empresa.setCnpj(rs.getString(4));

                    return Optional.of(empresa);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Empresa> findAll() {
        String sql = """
                SELECT *
                FROM Empresa""";
        List<Empresa> empresas = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    empresas.add(resultSetEmpresa(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empresas;
    }

    private Empresa resultSetEmpresa(ResultSet rs) throws SQLException {
        Empresa e = new Empresa();
        e.setId(rs.getInt(1));
        e.setNome(rs.getString(2));
        e.setTitular(rs.getString(3));
        e.setCnpj(rs.getString(4));
        return e;
    }
}
