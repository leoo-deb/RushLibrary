package dao;

import model.Registros;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class RegistrosDAO extends DAO<Registros, Integer> {

    @Override
    public Integer insert(Registros entity) {
        String sql = """
                INSERT INTO Registros (id_estoque, id_func, desc_reg, tipo_reg, id_cli, dataentrega_reg)
                VALUES (?, ?, ?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getId_estoque());
            ps.setInt(2, entity.getId_funcionario());
            ps.setString(3, entity.getDescricao());
            ps.setString(4, entity.getTipo());
            ps.setInt(5, entity.getId_cliente());
            ps.setObject(6, entity.getDataEntrega());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public Optional<Registros> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Registros
                WHERE id_regi = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Registros r = new Registros();

                    r.setId(rs.getInt(1));
                    r.setId_estoque(rs.getInt(2));
                    r.setId_funcionario(rs.getInt(3));
                    r.setDescricao(rs.getString(4));
                    r.setTipo(rs.getString(5));
                    r.setData(rs.getObject(6, LocalDate.class));
                    r.setId_cliente(rs.getInt(7));
                    r.setDataEntrega(rs.getObject(7, LocalDate.class));

                    return Optional.of(r);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Registros> findAll() {
        String sql = """
                SELECT *
                FROM Registros""";
        List<Registros> registros = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    registros.add(resultSetRegistros(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return registros;
    }

    private Registros resultSetRegistros(ResultSet rs) throws SQLException {
        Registros r = new Registros();

        r.setId(rs.getInt(1));
        r.setId_estoque(rs.getInt(2));
        r.setId_funcionario(rs.getInt(3));
        r.setDescricao(rs.getString(4));
        r.setTipo(rs.getString(5));
        r.setData(rs.getObject(6, LocalDate.class));
        r.setId_cliente(rs.getInt(7));
        r.setDataEntrega(rs.getObject(7, LocalDate.class));

        return r;
    }
}
