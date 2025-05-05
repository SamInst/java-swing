package org.sam.repository.usuario;

import org.sam.configuracoes.PostgresDatabaseConnect;
import raven.alerts.MessageAlerts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HexFormat;

public class UsuarioRepository {
    Connection conexao = PostgresDatabaseConnect.connect();

    public boolean criarPerfil(Usuario usuario) {
        if (usuarioOuEmailExiste(usuario.usuario(), usuario.email())) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao criar usuario",
                            "Usuário ou email já existe no sistema",
                            MessageAlerts.MessageType.ERROR);
            return false;
        }

        String sql = """
                INSERT INTO usuario (usuario, email, senha)
                VALUES (?, ?, ?);
                """;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, usuario.usuario());
            statement.setString(2, usuario.email());
            statement.setString(3, criptografarSenha(usuario.senha()));

            int linhasAfetadas = statement.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            MessageAlerts.getInstance()
                    .showMessage(
                            "Erro ao criar usuario",
                            e.getMessage(),
                            MessageAlerts.MessageType.ERROR);
            throw new RuntimeException(e);
        }
    }

    public Usuario autenticarUsuario(String email, String senha) {
        String sql = "SELECT usuario, email, senha FROM usuario WHERE email = ?";

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String senhaArmazenada = resultSet.getString("senha");
                    String senhaCriptografada = criptografarSenha(senha);

                    if (senhaCriptografada.equals(senhaArmazenada)) {
                        return new Usuario(
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
        String sql = "SELECT COUNT(*) FROM usuario WHERE usuario = ? OR email = ?";

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
