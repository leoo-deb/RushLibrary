package dao;

import model.Cliente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static utils.ConnectFactory.getConnection;

public class ClienteDAO extends DAO<Cliente, Integer> {

    @Override
    public Integer insert(Cliente entity) {
        String sql = """
                INSERT INTO Cliente (nome_cli, cpf_cli, email_cli, numero_cli, quantidade_livro)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (var ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getCpf());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getNumero());
            ps.setInt(5, entity.getQuantidadeLivro());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        throw new NoSuchElementException("ERROR: Nao foi possivel recuperar ID.");
    }

    @Override
    public void update(Cliente entity) {
        String sql = """
                UPDATE Cliente
                SET nome_cli = ?, cpf_cli = ?, email_cli = ?, numero_cli = ?, quantidade_livro = ?
                WHERE id_cli = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getCpf());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getNumero());
            ps.setInt(5, entity.getQuantidadeLivro());
            ps.setInt(6, entity.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer integer) {
        String sql = """
                DELETE
                FROM Cliente
                WHERE id_cli = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByCpf(String cpf) {
        String sql = """
                DELETE
                FROM Cliente
                WHERE cpf_cli = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, cpf);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cliente> findById(Integer integer) {
        String sql = """
                SELECT *
                FROM Cliente
                WHERE id_cli = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Cliente c = new Cliente();

                    c.setId(rs.getInt(1));
                    c.setNome(rs.getString(2));
                    c.setCpf(rs.getString(3));
                    c.setEmail(rs.getString(4));
                    c.setNumero(rs.getString(5));
                    c.setDataCadastro(rs.getObject(6, LocalDate.class));
                    c.setQuantidadeLivro(rs.getInt(7));

                    return Optional.of(c);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Cliente> findByCPF(String cpf) {
        String sql = """
                SELECT *
                FROM Cliente
                WHERE cpf_cli = ?""";

        try (var ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, cpf);
            try (var rs = ps.executeQuery()) {

                if (rs.next()) {
                    Cliente c = new Cliente();

                    c.setId(rs.getInt(1));
                    c.setNome(rs.getString(2));
                    c.setCpf(rs.getString(3));
                    c.setEmail(rs.getString(4));
                    c.setNumero(rs.getString(5));
                    c.setDataCadastro(rs.getObject(6, LocalDate.class));
                    c.setQuantidadeLivro(rs.getInt(7));

                    return Optional.of(c);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cliente> findAll() {
        String sql = """
                SELECT *
                FROM Cliente""";
        List<Cliente> clientes = new ArrayList<>();

        try (var ps = getConnection().prepareStatement(sql)) {

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(resultSetCliente(rs));
                }
            }
            return clientes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Cliente resultSetCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();

        c.setId(rs.getInt(1));
        c.setNome(rs.getString(2));
        c.setCpf(rs.getString(3));
        c.setEmail(rs.getString(4));
        c.setNumero(rs.getString(5));
        c.setDataCadastro(rs.getObject(6, LocalDate.class));
        c.setQuantidadeLivro(rs.getInt(7));

        return c;
    }
}