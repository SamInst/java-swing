package org.sam.repository.perfil;

import org.sam.configuracoes.PostgresDatabaseConnect;
import raven.alerts.MessageAlerts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Optional;

public class PerfilRepository {
    Connection conexao = PostgresDatabaseConnect.connect();

    public boolean criarPerfil(Perfil perfil) {
        if (usuarioOuEmailExiste(perfil.usuario(), perfil.email())) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao criar perfil",
                            "Usuário ou email já existe no sistema",
                            MessageAlerts.MessageType.ERROR);
            return false;
        }

        String sql = """
                INSERT INTO perfil (usuario, email, senha)
                VALUES (?, ?, ?);
                """;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, perfil.usuario());
            statement.setString(2, perfil.email());
            statement.setString(3, criptografarSenha(perfil.senha()));

            int linhasAfetadas = statement.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao criar perfil",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public Optional<Perfil> buscarPerfilPorId(Long id) {
        String sql = "SELECT usuario, email, senha FROM perfil WHERE id = ?";

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Perfil perfil = new Perfil(
                            resultSet.getString("usuario"),
                            resultSet.getString("email"),
                            resultSet.getString("senha")
                    );
                    return Optional.of(perfil);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao buscar perfil",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public Perfil autenticarUsuario(String email, String senha) {
        String sql = "SELECT usuario, email, senha FROM perfil WHERE email = ?";

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String senhaArmazenada = resultSet.getString("senha");
                    String senhaCriptografada = criptografarSenha(senha);

                    if (senhaCriptografada.equals(senhaArmazenada)) {
                        return new Perfil(
                                resultSet.getString("usuario"),
                                resultSet.getString("email"),
                                ""
                        );
                    }
                }
                throw new RuntimeException("Credenciais inválidas");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean usuarioOuEmailExiste(String usuario, String email) {
        String sql = "SELECT COUNT(*) FROM perfil WHERE usuario = ? OR email = ?";

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao verificar usuário/email",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }

    private String criptografarSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao criptografar senha",
                            "Algoritmo de criptografia não disponível",
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }
}
