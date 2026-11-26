-- PARTE
CREATE DATABASE projeto_final_programacao_1
    ENCODING 'UTF8'
    TEMPLATE template0;

-- PARTE 2
CREATE TABLE usuario (
    id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    senha         VARCHAR(255) NOT NULL,
    data_cadastro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projeto (
    id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome           VARCHAR(150) NOT NULL,
    artista        VARCHAR(150) NOT NULL,   -- artista, banda ou marca (instrumentos)
    tipo           VARCHAR(30)  NOT NULL,
    ano_lancamento INTEGER,
    gravadora      VARCHAR(100),            -- gravadora ou fabricante
    condicao       VARCHAR(20)  NOT NULL,
    valor_pago     NUMERIC(10,2),
    observacoes    TEXT,
    imagem         VARCHAR(255),            -- nome do arquivo da capa/foto
    usuario_id     INTEGER      NOT NULL,
    data_cadastro  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_projeto_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT ck_projeto_tipo CHECK (tipo IN
        ('Vinil', 'CD', 'Fita K7', 'DVD/Blu-ray', 'Pôster', 'Camiseta',
         'Instrumento musical', 'Outro')),
    CONSTRAINT ck_projeto_condicao CHECK (condicao IN
        ('Novo', 'Excelente', 'Bom', 'Regular', 'Ruim')),
    CONSTRAINT ck_projeto_ano CHECK (ano_lancamento IS NULL OR ano_lancamento BETWEEN 1800 AND 2100),
    CONSTRAINT ck_projeto_valor CHECK (valor_pago IS NULL OR valor_pago >= 0)
);

CREATE TABLE postagem (
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo          VARCHAR(180) NOT NULL,
    conteudo        TEXT         NOT NULL,
    projeto_id      INTEGER      NOT NULL,
    autor_id        INTEGER      NOT NULL,
    data_publicacao TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_postagem_projeto UNIQUE (projeto_id),
    CONSTRAINT fk_postagem_projeto
        FOREIGN KEY (projeto_id) REFERENCES projeto (id) ON DELETE CASCADE,
    CONSTRAINT fk_postagem_autor
        FOREIGN KEY (autor_id) REFERENCES usuario (id) ON DELETE CASCADE
);

CREATE INDEX idx_projeto_usuario  ON projeto (usuario_id);
CREATE INDEX idx_postagem_autor   ON postagem (autor_id);
CREATE INDEX idx_postagem_data    ON postagem (data_publicacao DESC);
