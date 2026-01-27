package dao;

import model.Livro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class LivroDAO extends DAO<Livro, Integer> {

    @Override
    public Integer insert(Livro entity) {
        String sql = """
                INSERT INTO Livros (nome_livro, sinopse_livro, autor_livro, isbn_livro, tipo_livro, id_categoria, codigo_contrato)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getSinopse());
            ps.setString(3, entity.getAutor());
            ps.setInt(4, entity.getIsbn());
            ps.setString(5, entity.getTipo());
            ps.setInt(6, entity.getId_categoria());
            ps.setInt(7, entity.getId_contrato());
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public void update(Livro entity) {
        String sql = """
                UPDATE Livro
                SET nome_livro = ?, sinopse_livro = ?, autor_livro = ?
                WHERE id_livro = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getSinopse());
            ps.setString(3, entity.getAutor());
            ps.setInt(4, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE FROM Livro
                WHERE id_livro = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Livro> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Livro
                WHERE id_livro = ?""";
        Livro l = new Livro();

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            var rs = ps.executeQuery();

            while (rs.next()) {
                l.setId(rs.getInt(1));
                l.setNome(rs.getString(2));
                l.setSinopse(rs.getString(3));
                l.setAutor(rs.getString(4));
                l.setIsbn(rs.getInt(5));
                l.setId_categoria(rs.getInt(6));
                l.setId_contrato(rs.getInt(7));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(l);
    }

    @Override
    public List<Livro> findAll() {
        String sql = """
                SELECT *
                FROM Livro""";
        List<Livro> livros = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            var rs = ps.executeQuery();
            while (rs.next()) {
                livros.add(resultSetLivro(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return livros;
    }

    private Livro resultSetLivro(ResultSet rs) throws SQLException {
        Livro l = new Livro();

        l.setId(rs.getInt(1));
        l.setNome(rs.getString(2));
        l.setSinopse(rs.getString(3));
        l.setAutor(rs.getString(4));
        l.setIsbn(rs.getInt(5));
        l.setId_categoria(rs.getInt(6));
        l.setId_contrato(rs.getInt(7));

        return l;
    }
}
