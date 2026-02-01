package dao;

import model.Contrato;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class ContradoDAO extends DAO<Contrato, Integer> {

    @Override
    public Integer insert(Contrato entity) {
        String sql = """
                INSERT INTO Contrato (tipo_contrato, status_contrato, vigencia_contrato, vencimento_contrato)
                VALUES (?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getTipo());
            ps.setString(2, entity.getStatus());
            ps.setObject(3, entity.getVigencia());
            ps.setObject(4, entity.getVencimento());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public void update(Contrato entity) {
        String sql = """
                UPDATE Contrato
                SET vigencia_contrato = ?, vencimento_contrato = ?, status_contrato = ?
                WHERE id_contrato = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setObject(1, entity.getVigencia());
            ps.setObject(2, entity.getVencimento());
            ps.setObject(3, entity.getStatus());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE FROM Contrato
                WHERE id_contrato = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Contrato> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Contrato
                WHERE codigo_contrato = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Contrato c = new Contrato();

                    c.setCodigo(rs.getInt(1));
                    c.setTipo(rs.getString(2));
                    c.setStatus(rs.getString(3));
                    c.setVigencia(rs.getObject(4, LocalDate.class));
                    c.setVencimento(rs.getObject(5, LocalDate.class));

                    return Optional.of(c);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Contrato> findAll() {
        String sql = """
                SELECT *
                FROM Contrato""";
        List<Contrato> contratos = new ArrayList<>();

         try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

             try (var rs = ps.executeQuery()) {
                 while (rs.next()) {
                     contratos.add(resultSetContrato(rs));
                 }
             }
             return contratos;
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
    }

    private Contrato resultSetContrato(ResultSet rs) throws SQLException {
        Contrato c = new Contrato();

        c.setCodigo(rs.getInt(1));
        c.setTipo(rs.getString(2));
        c.setStatus(rs.getString(3));
        c.setVigencia(rs.getObject(4, LocalDate.class));
        c.setVencimento(rs.getObject(5, LocalDate.class));

        return c;
    }
}
