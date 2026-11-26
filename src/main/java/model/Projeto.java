package model;

import java.math.BigDecimal;
import java.util.List;

public class Projeto {

    public static final List<String> TIPOS = List.of(
            "Vinil", "CD", "Fita K7", "DVD/Blu-ray", "Pôster", "Camiseta",
            "Instrumento musical", "Outro");

    public static final List<String> CONDICOES = List.of(
            "Novo", "Excelente", "Bom", "Regular", "Ruim");

    private int id;
    private String nome;
    private String artista;
    private String tipo;
    private Integer anoLancamento;
    private String gravadora;
    private String condicao;
    private BigDecimal valorPago;
    private String observacoes;
    private String imagem;
    private int usuarioId;
    private Integer postagemId;

    public Projeto() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(Integer anoLancamento) { this.anoLancamento = anoLancamento; }

    public String getGravadora() { return gravadora; }
    public void setGravadora(String gravadora) { this.gravadora = gravadora; }

    public String getCondicao() { return condicao; }
    public void setCondicao(String condicao) { this.condicao = condicao; }

    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Integer getPostagemId() { return postagemId; }
    public void setPostagemId(Integer postagemId) { this.postagemId = postagemId; }
}
