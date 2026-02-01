package dao;

import model.Movimentacao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class MovimentacaoDAO extends DAO<Movimentacao, Integer> {

    @Override
    public Integer insert(Movimentacao entity) {
        String sql = """
                INSERT INTO Movimentacao (id_estoque, id_func, quant_movi, tipo_movi, id_cli, dataprevista_reg, dataentrega_reg, status_entrega)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getId_estoque());
            ps.setInt(2, entity.getId_funcionario());

            ps.setInt(3, entity.getQuant());
            ps.setString(4, entity.getTipo());

            ps.setInt(5, entity.getId_cliente());
            ps.setObject(6, entity.getDataPrevista());
            ps.setObject(7, entity.getDataEntrega());
            ps.setString(8, entity.getStatus());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public void update(Movimentacao entity) {
        String sql = """
                UPDATE Movimentacao
                SET status_entrega = ?
                WHERE id_movi = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, entity.getStatus());
            ps.setInt(2, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Movimentacao> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Movimentacao
                WHERE id_movi = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Movimentacao m = new Movimentacao();

                    m.setId(rs.getInt(1));
                    m.setId_estoque(rs.getInt(2));
                    m.setId_funcionario(rs.getInt(3));

                    m.setQuant(rs.getInt(4));
                    m.setTipo(rs.getString(5));
                    m.setData(rs.getObject(6, LocalDateTime.class));

                    m.setId_cliente(rs.getInt(7));
                    m.setDataPrevista(rs.getObject(8, LocalDate.class));
                    m.setDataEntrega(rs.getObject(9, LocalDate.class));
                    m.setStatus(rs.getString(10));

                    return Optional.of(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Movimentacao> findAll() {
        String sql = """
                SELECT *
                FROM Movimentacao""";
        List<Movimentacao> movimentacoes = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    movimentacoes.add(resultSetRegistros(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return movimentacoes;
    }

    private Movimentacao resultSetRegistros(ResultSet rs) throws SQLException {
        Movimentacao m = new Movimentacao();

        m.setId(rs.getInt(1));
        m.setId_estoque(rs.getInt(2));
        m.setId_funcionario(rs.getInt(3));

        m.setQuant(rs.getInt(4));
        m.setTipo(rs.getString(5));
        m.setData(rs.getObject(6, LocalDateTime.class));

        m.setId_cliente(rs.getInt(7));
        m.setDataPrevista(rs.getObject(8, LocalDate.class));
        m.setDataEntrega(rs.getObject(9, LocalDate.class));
        m.setStatus(rs.getString(10));

        return m;
    }
}
