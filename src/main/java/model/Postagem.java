package model;

import java.sql.Timestamp;

public class Postagem {

    private int id;
    private String titulo;
    private String conteudo;
    private int projetoId;
    private int autorId;
    private Timestamp dataPublicacao;

    private String autorNome;
    private String itemNome;
    private String itemArtista;
    private String itemTipo;
    private Integer itemAno;
    private String itemCondicao;
    private String itemImagem;

    public Postagem() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public int getProjetoId() { return projetoId; }
    public void setProjetoId(int projetoId) { this.projetoId = projetoId; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public Timestamp getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(Timestamp dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public String getAutorNome() { return autorNome; }
    public void setAutorNome(String autorNome) { this.autorNome = autorNome; }

    public String getItemNome() { return itemNome; }
    public void setItemNome(String itemNome) { this.itemNome = itemNome; }

    public String getItemArtista() { return itemArtista; }
    public void setItemArtista(String itemArtista) { this.itemArtista = itemArtista; }

    public String getItemTipo() { return itemTipo; }
    public void setItemTipo(String itemTipo) { this.itemTipo = itemTipo; }

    public Integer getItemAno() { return itemAno; }
    public void setItemAno(Integer itemAno) { this.itemAno = itemAno; }

    public String getItemCondicao() { return itemCondicao; }
    public void setItemCondicao(String itemCondicao) { this.itemCondicao = itemCondicao; }

    public String getItemImagem() { return itemImagem; }
    public void setItemImagem(String itemImagem) { this.itemImagem = itemImagem; }
}
