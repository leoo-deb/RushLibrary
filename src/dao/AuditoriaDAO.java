package dao;

import com.mysql.cj.xdevapi.ExprUnparser;
import model.Auditoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class AuditoriaDAO extends DAO<Auditoria, Integer> {

    @Override
    public Integer insert(Auditoria entity) {
        String sql = """
                INSERT INTO Auditoria (tipo_aud, id_func, id_tipo)
                VALUES (?, ?, ?)""";
        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getTipo());
            ps.setInt(2, entity.getID_FUNCIONARIO());
            ps.setInt(3, entity.getID_TIPO());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new NoSuchElementException("Nao foi possivel recuperar ID.");
    }

    @Override
    public Optional<Auditoria> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Auditoria
                WHERE id_aud = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Auditoria a = new Auditoria();

                    a.setId(rs.getInt(1));
                    a.setTipo(rs.getString(2));
                    a.setID_FUNCIONARIO(rs.getInt(3));
                    a.setID_TIPO(rs.getInt(4));

                    return Optional.of(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Auditoria> findAll() {
        String sql = """
                SELECT *
                FROM Auditoria""";
        List<Auditoria> lista = new ArrayList<>();
        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(resultSetAuditoria(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    private Auditoria resultSetAuditoria(ResultSet rs) throws SQLException {
        Auditoria a = new Auditoria();

        a.setId(rs.getInt(1));
        a.setTipo(rs.getString(2));
        a.setID_TIPO(rs.getInt(3));
        a.setID_TIPO(rs.getInt(4));

        return a;
    }
}
