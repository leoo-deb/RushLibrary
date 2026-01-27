package dao;

import model.Categoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class CategoriaDAO extends DAO<Categoria, Integer> {

    @Override
    public Integer insert(Categoria entity) {
        String sql = """
                INSERT INTO Categoria (tipo_categoria, genero_categoria)
                VALUES (?, ?)""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getTipo());
            ps.setString(2, entity.getGenero());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public void update(Categoria entity) {
        String sql = """
                UPDATE Categoria
                SET tipo_categoria = ?, genero_categoria = ?
                WHERE id_categoria = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, entity.getTipo());
            ps.setString(2, entity.getGenero());
            ps.setInt(3, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE FROM Categoria
                WHERE id_categoria = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Categoria> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Categoria
                WHERE id_categoria = ?""";
        Categoria c = new Categoria();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                c.setId(rs.getInt(1));
                c.setTipo(rs.getString(2));
                c.setGenero(rs.getString(3));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(c);
    }

    @Override
    public List<Categoria> findAll() {
        String sql = """
                SELECT *
                FROM Categoria""";
        List<Categoria> categorias = new ArrayList<>();

        try(PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                categorias.add(resusltSetCategoria(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categorias;
    }

    private Categoria resusltSetCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();

        c.setId(rs.getInt(1));
        c.setTipo(rs.getString(2));
        c.setTipo(rs.getString(3));

        return c;
    }
}
