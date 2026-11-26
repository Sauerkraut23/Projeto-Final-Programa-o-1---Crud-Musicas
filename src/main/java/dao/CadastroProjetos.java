package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import model.Projeto;

public class CadastroProjetos {

    private static final String COLUNAS = """
            id, nome, artista, tipo, ano_lancamento, gravadora, condicao,
            valor_pago, observacoes, imagem, usuario_id,
            (SELECT po.id FROM postagem po WHERE po.projeto_id = projeto.id LIMIT 1) AS postagem_id
            """;

    public boolean cadastrar(Projeto p) throws SQLException {
        String sql = """
                INSERT INTO projeto
                (nome, artista, tipo, ano_lancamento, gravadora, condicao,
                 valor_pago, observacoes, imagem, usuario_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            preencher(st, p);
            st.setInt(10, p.getUsuarioId());
            return st.executeUpdate() > 0;
        }
    }

    public List<Projeto> listarPorUsuario(int usuarioId, String busca) throws SQLException {
        boolean comBusca = busca != null && !busca.isBlank();
        String sql = "SELECT " + COLUNAS + " FROM projeto WHERE usuario_id = ? "
                + (comBusca ? "AND (nome ILIKE ? OR artista ILIKE ?) " : "")
                + "ORDER BY nome";

        List<Projeto> itens = new ArrayList<>();
        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, usuarioId);
            if (comBusca) {
                String padrao = "%" + busca.trim() + "%";
                st.setString(2, padrao);
                st.setString(3, padrao);
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapear(rs));
                }
            }
        }
        return itens;
    }

    public List<Projeto> listarDisponiveisParaPostagem(int usuarioId, int projetoAtualId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM projeto WHERE usuario_id = ? "
                + "AND (id = ? OR NOT EXISTS (SELECT 1 FROM postagem x WHERE x.projeto_id = projeto.id)) "
                + "ORDER BY nome";

        List<Projeto> itens = new ArrayList<>();
        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, usuarioId);
            st.setInt(2, projetoAtualId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapear(rs));
                }
            }
        }
        return itens;
    }

    public Projeto buscarPorIdEUsuario(int id, int usuarioId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM projeto WHERE id = ? AND usuario_id = ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, id);
            st.setInt(2, usuarioId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean atualizar(Projeto p) throws SQLException {
        String sql = """
                UPDATE projeto
                   SET nome = ?, artista = ?, tipo = ?, ano_lancamento = ?, gravadora = ?,
                       condicao = ?, valor_pago = ?, observacoes = ?, imagem = ?
                 WHERE id = ? AND usuario_id = ?
                """;

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            preencher(st, p);
            st.setInt(10, p.getId());
            st.setInt(11, p.getUsuarioId());
            return st.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id, int usuarioId) throws SQLException {
        String sql = "DELETE FROM projeto WHERE id = ? AND usuario_id = ?";

        try (Connection conexao = ConexaoDB.getConexao();
             PreparedStatement st = conexao.prepareStatement(sql)) {
            st.setInt(1, id);
            st.setInt(2, usuarioId);
            return st.executeUpdate() > 0;
        }
    }

    private void preencher(PreparedStatement st, Projeto p) throws SQLException {
        st.setString(1, p.getNome());
        st.setString(2, p.getArtista());
        st.setString(3, p.getTipo());
        if (p.getAnoLancamento() == null) {
            st.setNull(4, Types.INTEGER);
        } else {
            st.setInt(4, p.getAnoLancamento());
        }
        st.setString(5, p.getGravadora());
        st.setString(6, p.getCondicao());
        if (p.getValorPago() == null) {
            st.setNull(7, Types.NUMERIC);
        } else {
            st.setBigDecimal(7, p.getValorPago());
        }
        st.setString(8, p.getObservacoes());
        st.setString(9, p.getImagem());
    }

    private Projeto mapear(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setArtista(rs.getString("artista"));
        p.setTipo(rs.getString("tipo"));
        int ano = rs.getInt("ano_lancamento");
        p.setAnoLancamento(rs.wasNull() ? null : ano);
        p.setGravadora(rs.getString("gravadora"));
        p.setCondicao(rs.getString("condicao"));
        BigDecimal valor = rs.getBigDecimal("valor_pago");
        p.setValorPago(valor);
        p.setObservacoes(rs.getString("observacoes"));
        p.setImagem(rs.getString("imagem"));
        p.setUsuarioId(rs.getInt("usuario_id"));
        int postagem = rs.getInt("postagem_id");
        p.setPostagemId(rs.wasNull() ? null : postagem);
        return p;
    }
}
