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
                INSERT INTO Contrato (tipo_contrato, vigencia_contrato, vencimento_contrato, id_empresa)
                VALUES (?, ?, ?, ?)""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getTipo());
            ps.setObject(2, entity.getVigencia());
            ps.setObject(3, entity.getVencimento());
            ps.setInt(4, entity.getId_empresa());
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
                SET vigencia_contrato = ?, vencimento_contrato = ?
                WHERE id_contrato = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setObject(1, entity.getVigencia());
            ps.setObject(2, entity.getVencimento());
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

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
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

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Contrato c = new Contrato();

                    c.setCodigo(rs.getInt(1));
                    c.setTipo(rs.getString(2));
                    c.setVigencia(rs.getObject(3, LocalDate.class));
                    c.setVencimento(rs.getObject(4, LocalDate.class));
                    c.setId_empresa(rs.getInt(5));

                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();

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
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }

         return contratos;
    }

    private Contrato resultSetContrato(ResultSet rs) throws SQLException {
        Contrato c = new Contrato();

        c.setCodigo(rs.getInt(1));
        c.setTipo(rs.getString(2));
        c.setVigencia(rs.getObject(3, LocalDate.class));
        c.setVencimento(rs.getObject(4, LocalDate.class));
        c.setId_empresa(rs.getInt(5));

        return c;
    }
}
