package dao;

import model.Estoque;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static utils.ConnectFactory.getConnection;

public class EstoqueDAO extends DAO<Estoque, Integer> {

    @Override
    public Integer insert(Estoque entity) {
        String sql = """
                INSERT INTO Estoque (id_livro, qunt_estoque)
                VALUES (?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getId_livro());
            ps.setInt(2, entity.getQuantidade());
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
    public void update(Estoque entity) {
        String sql = """
                UPDATE Estoque
                SET qunt_estoque = ?
                WHERE id_estoque = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, entity.getQuantidade());
            ps.setInt(2, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE FROM Estoque
                WHERE id_livro = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Estoque> findById(Integer integer) {
        String sql = """
                SELECT *
                FORM Estoque
                WHERE id_estoque = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Estoque e = new Estoque();

                    e.setId(rs.getInt(1));
                    e.setId_livro(rs.getInt(2));
                    e.setQuantidade(rs.getInt(3));

                    return Optional.of(e);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public Optional<Estoque> findByIdLivro(Integer integer) {
        String sql = """
                SELECT *
                FORM Estoque
                WHERE id_livro = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Estoque e = new Estoque();

                    e.setId(rs.getInt(1));
                    e.setId_livro(rs.getInt(2));
                    e.setQuantidade(rs.getInt(3));

                    return Optional.of(e);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Estoque> findAll() {
        String sql = """
                SELECT *
                FROM Estoque""";
        List<Estoque> estoques = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    estoques.add(resultSetEstoque(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return estoques;
    }

    private Estoque resultSetEstoque(ResultSet rs) throws SQLException {
        Estoque e = new Estoque();

        e.setId(rs.getInt(1));
        e.setId_livro(rs.getInt(2));
        e.setQuantidade(rs.getInt(3));

        return e;
    }
}
