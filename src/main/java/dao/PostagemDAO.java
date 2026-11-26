package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Postagem;

public class PostagemDAO {

    private static final String VIOLACAO_UNICA = "23505";
    private static final String MSG_JA_PUBLICADO = "Este item já está publicado. Edite a postagem que já existe.";

    private static final String SELECT_BASE = """
            SELECT p.id, p.titulo, p.conteudo, p.projeto_id, p.autor_id, p.data_publicacao,
                   u.nome AS autor_nome,
                   j.nome AS item_nome, j.artista AS item_artista, j.tipo AS item_tipo,
                   j.ano_lancamento AS item_ano, j.condicao AS item_condicao, j.imagem AS item_imagem
              FROM postagem p
              JOIN usuario u ON u.id = p.autor_id
              JOIN projeto j ON j.id = p.projeto_id
            """;

    public boolean cadastrar(Postagem postagem) throws SQLException {
        String sql = "INSERT INTO postagem (titulo, conteudo, projeto_id, autor_id) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setString(1, postagem.getTitulo());
            st.setString(2, postagem.getConteudo());
            st.setInt(3, postagem.getProjetoId());
            st.setInt(4, postagem.getAutorId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            if (VIOLACAO_UNICA.equals(e.getSQLState())) {
                throw new IllegalArgumentException(MSG_JA_PUBLICADO, e);
            }
            throw e;
        }
    }

    public boolean itemJaPublicado(int projetoId, int ignorarPostagemId) throws SQLException {
        String sql = "SELECT 1 FROM postagem WHERE projeto_id = ? AND id <> ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, projetoId);
            st.setInt(2, ignorarPostagemId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Postagem> listarPublicadas(String busca) throws SQLException {
        if (busca == null || busca.isBlank()) {
            return consultar(SELECT_BASE + " ORDER BY p.data_publicacao DESC");
        }
        String padrao = "%" + busca.trim() + "%";
        return consultar(SELECT_BASE
                + " WHERE p.titulo ILIKE ? OR j.nome ILIKE ? OR j.artista ILIKE ?"
                + " ORDER BY p.data_publicacao DESC", padrao, padrao, padrao);
    }

    public List<Postagem> listarRecentes(int limite) throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY p.data_publicacao DESC LIMIT ?", limite);
    }

    public List<Postagem> listarPorAutor(int autorId) throws SQLException {
        return consultar(SELECT_BASE + " WHERE p.autor_id = ? ORDER BY p.data_publicacao DESC", autorId);
    }

    public Postagem buscarPorId(int id) throws SQLException {
        List<Postagem> lista = consultar(SELECT_BASE + " WHERE p.id = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public Postagem buscarPorIdEAutor(int id, int autorId) throws SQLException {
        List<Postagem> lista = consultar(SELECT_BASE + " WHERE p.id = ? AND p.autor_id = ?", id, autorId);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public boolean atualizar(Postagem postagem) throws SQLException {
        String sql = "UPDATE postagem SET titulo = ?, conteudo = ?, projeto_id = ? WHERE id = ? AND autor_id = ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setString(1, postagem.getTitulo());
            st.setString(2, postagem.getConteudo());
            st.setInt(3, postagem.getProjetoId());
            st.setInt(4, postagem.getId());
            st.setInt(5, postagem.getAutorId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            if (VIOLACAO_UNICA.equals(e.getSQLState())) {
                throw new IllegalArgumentException(MSG_JA_PUBLICADO, e);
            }
            throw e;
        }
    }

    public boolean excluir(int id, int autorId) throws SQLException {
        String sql = "DELETE FROM postagem WHERE id = ? AND autor_id = ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, id);
            st.setInt(2, autorId);
            return st.executeUpdate() > 0;
        }
    }

    private List<Postagem> consultar(String sql, Object... parametros) throws SQLException {
        List<Postagem> postagens = new ArrayList<>();

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                st.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    postagens.add(mapear(rs));
                }
            }
        }
        return postagens;
    }

    private Postagem mapear(ResultSet rs) throws SQLException {
        Postagem p = new Postagem();
        p.setId(rs.getInt("id"));
        p.setTitulo(rs.getString("titulo"));
        p.setConteudo(rs.getString("conteudo"));
        p.setProjetoId(rs.getInt("projeto_id"));
        p.setAutorId(rs.getInt("autor_id"));
        p.setDataPublicacao(rs.getTimestamp("data_publicacao"));
        p.setAutorNome(rs.getString("autor_nome"));
        p.setItemNome(rs.getString("item_nome"));
        p.setItemArtista(rs.getString("item_artista"));
        p.setItemTipo(rs.getString("item_tipo"));
        int ano = rs.getInt("item_ano");
        p.setItemAno(rs.wasNull() ? null : ano);
        p.setItemCondicao(rs.getString("item_condicao"));
        p.setItemImagem(rs.getString("item_imagem"));
        return p;
    }
}
