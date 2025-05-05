package org.sam.repository.funcionario;

import org.sam.configuracoes.PostgresDatabaseConnect;
import raven.alerts.MessageAlerts;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioRepository {
    Connection conexao = PostgresDatabaseConnect.connect();

    public void adicionarFuncionario(FuncionarioRequest request){
        String sql = """
    insert into funcionario (nome, data_admissao, salario, status)
    VALUES (?,?,?,?);
    """;
        try (PreparedStatement pessoaStatement = conexao.prepareStatement(sql)) {
            pessoaStatement.setString(1, request.nome());
            pessoaStatement.setTimestamp(2, Timestamp.valueOf(request.dataAdmissao()));
            pessoaStatement.setFloat(3, request.salario());
            pessoaStatement.setBoolean(4, request.status());
            pessoaStatement.executeUpdate();
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Nao foi possivel adicionar o funcionario",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public List<FuncionarioResponse> listarFuncionarios() {
        List<FuncionarioResponse> funcionarios = new ArrayList<>();
        String sql = "SELECT id, nome, data_admissao, salario, status FROM funcionario";

        try (
                Statement statement = conexao.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                FuncionarioResponse funcionario = new FuncionarioResponse(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getTimestamp("data_admissao").toLocalDateTime(),
                        resultSet.getFloat("salario"),
                        resultSet.getBoolean("status")
                );
                funcionarios.add(funcionario);
            }
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao listar funcionários",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR
                    );
            throw new RuntimeException(e);
        }
        return funcionarios;
    }
}
