package dao;

import model.Funcionario;
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

public class FuncionarioDAO extends DAO<Funcionario, Integer> {

    @Override
    public Integer insert(Funcionario entity) {
        String sql = "INSERT INTO Funcionario (nome_func, cpf_func, email_func, cargo_func, dataadimissao_func) VALUES (?, ?, ?, ?, ?))";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getCpf());
            ps.setString(3, entity.getEmail());
            ps.setObject(4, entity.getCargo());
            ps.setObject(5, entity.getAdimissao());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            throw new RuntimeException("Nao foi possivel inserir o funcionario.", e);
        }

        throw new NoSuchElementException("Nao foi possivel recuperar ID.");
    }

    @Override
    public Optional<Funcionario> findById(Integer integer) {
        String sql = "SELECT * FROM Funcionario WHERE id_func = ?";
        Funcionario funcionario = new Funcionario();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setInt(1, integer);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                funcionario.setId(rs.getInt(1));
                funcionario.setNome(rs.getString(2));
                funcionario.setCpf(rs.getString(3));
                funcionario.setEmail(rs.getString(4));
                funcionario.setCargo(rs.getString(5));
                funcionario.setAdimissao(rs.getObject(6, LocalDate.class));
            }

        } catch (Exception e) {
            throw new RuntimeException("Nao foi possivel encontrar o funcionario.", e);
        }

        return Optional.of(funcionario);
    }

    public Optional<Funcionario> findByCPF(String cpf) {
        String sql = "SELECT * FROM Funcionario WHERE cpf_func = ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();

                    funcionario.setId(rs.getInt(1));
                    funcionario.setNome(rs.getString(2));
                    funcionario.setCpf(rs.getString(3));
                    funcionario.setEmail(rs.getString(4));
                    funcionario.setCargo(rs.getString(5));
                    funcionario.setAdimissao(rs.getObject(6, LocalDate.class));

                    return Optional.of(funcionario);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel encontrar o funcionario.", e);
        }
    }

    @Override
    public List<Funcionario> findAll() {
        String sql = "SELECT * FROM Funcionario";
        List<Funcionario> funcionarios = new ArrayList<>();

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                funcionarios.add(resultSetFuncionario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel criar a lista.", e);
        }
        return funcionarios;
    }

    private Funcionario resultSetFuncionario(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
            funcionario.setId(rs.getInt(1));
            funcionario.setNome(rs.getString(2));
            funcionario.setCpf(rs.getString(3));
            funcionario.setEmail(rs.getString(4));
            funcionario.setCargo(rs.getString(5));
            funcionario.setAdimissao(rs.getObject(6, LocalDate.class));
        return funcionario;
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM Funcionario WHERE cpf_func = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            Funcionario funcionario = new Funcionario();

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Nao foi possivel realizar a remocao.", e);
        }
    }

    public void deleteByCPF(String cpf) {
        String sql = "DELETE FROM Funcionario WHERE cpf_func = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            Funcionario funcionario = new Funcionario();

            ps.setString(1, cpf);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel realizar a remocao.", e);
        }
    }

    @Override
    public void update(Funcionario entity) {
        String sql = """
                UPDATE Funcionario
                SET nome_func = ?, email_func = ?, cargo_func = ?
                WHERE id_func = ?""";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, entity.getNome());
            ps.setString(2, entity.getEmail());
            ps.setObject(3, entity.getCargo());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Nao foi possivel realizar a atualizacao.", e);
        }
    }
}
