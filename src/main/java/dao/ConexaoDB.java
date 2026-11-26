package dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConexaoDB {

    private static final Logger LOGGER = Logger.getLogger(ConexaoDB.class.getName());
    private static final Properties CONFIG = carregarConfiguracao();

    private ConexaoDB() {
    }

    private static Properties carregarConfiguracao() {
        Properties propriedades = new Properties();
        try (InputStream entrada = ConexaoDB.class.getResourceAsStream("/db.properties")) {
            if (entrada == null) {
                LOGGER.warning("Arquivo db.properties não encontrado. Usando valores padrão. "
                        + "Copie db.properties.example para db.properties e ajuste a senha.");
            } else {
                propriedades.load(new InputStreamReader(entrada, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Não foi possível ler o db.properties", e);
        }
        return propriedades;
    }

    public static Connection getConexao() throws SQLException {
        String url = CONFIG.getProperty("db.url", "jdbc:postgresql://localhost:5432/projeto_final_programacao_1");
        String usuario = CONFIG.getProperty("db.usuario", "postgres");
        String senha = CONFIG.getProperty("db.senha", "");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado (confira o pom.xml).", e);
        }

        return DriverManager.getConnection(url, usuario, senha);
    }
}
