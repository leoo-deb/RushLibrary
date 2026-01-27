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
                INSERT INTO Registros (id_estoque, id_func, desc_reg, tipo_reg, data_reg, id_cli, dataentrega_reg)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getId_estoque());
            ps.setInt(2, entity.getId_funcionario());
            ps.setString(3, entity.getDescricao());
            ps.setObject(4, entity.getTipo());
            ps.setObject(5, entity.getData());
            ps.setInt(6, entity.getId_cliente());
            ps.setObject(7, entity.getDataEntrega());
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
        Registros r = new Registros();

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            var rs = ps.executeQuery();

            while (rs.next()) {
                r.setId(rs.getInt(1));
                r.setId_estoque(rs.getInt(2));
                r.setId_funcionario(rs.getInt(3));
                r.setDescricao(rs.getString(4));
                r.setTipo(rs.getObject(5, Registros.Tipo.class));
                r.setData(rs.getObject(6, LocalDate.class));
                r.setId_cliente(rs.getInt(7));
                r.setDataEntrega(rs.getObject(7, LocalDate.class));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.of(r);
    }

    @Override
    public List<Registros> findAll() {
        String sql = """
                SELECT *
                FROM Registros""";
        List<Registros> registros = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            var rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(resultSetRegistros(rs));
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
        r.setTipo(rs.getObject(5, Registros.Tipo.class));
        r.setData(rs.getObject(6, LocalDate.class));
        r.setId_cliente(rs.getInt(7));
        r.setDataEntrega(rs.getObject(7, LocalDate.class));

        return r;
    }
}
