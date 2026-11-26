package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import model.Usuario;
import util.Senha;

public class UsuarioDAO {

    private static final String VIOLACAO_UNICA = "23505";

    public void cadastrar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setString(1, usuario.getNome().trim());
            st.setString(2, usuario.getEmail().trim().toLowerCase(Locale.ROOT));
            st.setString(3, Senha.gerarHash(usuario.getSenha()));
            st.executeUpdate();
        } catch (SQLException e) {
            if (VIOLACAO_UNICA.equals(e.getSQLState())) {
                throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.", e);
            }
            throw e;
        }
    }

    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, email, senha FROM usuario WHERE email = ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setString(1, email.trim().toLowerCase(Locale.ROOT));

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next() && Senha.conferir(senha, rs.getString("senha"))) {
                    return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("email"));
                }
            }
        }
        return null;
    }
}
