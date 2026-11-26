package util;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Arquivos {

    private static final Logger LOGGER = Logger.getLogger(Arquivos.class.getName());

    public static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;

    private static final Map<String, String> TIPOS = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "webp", "image/webp");

    private static final Path DIRETORIO = definirDiretorio();

    private Arquivos() {
    }

    private static Path definirDiretorio() {
        String pasta = System.getProperty("uploads.dir");
        if (pasta == null || pasta.isBlank()) {
            pasta = System.getenv("UPLOADS_DIR");
        }
        if (pasta == null || pasta.isBlank()) {
            pasta = System.getProperty("user.home") + "/colecionaveis-uploads";
        }
        return Paths.get(pasta).toAbsolutePath().normalize();
    }

    public static Path getDiretorio() {
        return DIRETORIO;
    }

    public static String salvarImagem(Part parte) throws IOException {
        if (parte == null || parte.getSize() == 0) {
            return null;
        }
        if (parte.getSize() > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 5 MB.");
        }

        String extensao = extensao(parte.getSubmittedFileName());
        if (!TIPOS.containsKey(extensao)) {
            throw new IllegalArgumentException("A imagem deve ser JPG, PNG, GIF ou WEBP.");
        }

        byte[] bytes;
        try (InputStream entrada = parte.getInputStream()) {
            bytes = entrada.readAllBytes();
        }

        if (!assinaturaValida(bytes, extensao)) {
            throw new IllegalArgumentException("O arquivo enviado não é uma imagem válida.");
        }

        Files.createDirectories(DIRETORIO);
        String nome = UUID.randomUUID() + "." + extensao;
        Files.write(DIRETORIO.resolve(nome), bytes);
        return nome;
    }

    public static void excluir(String nome) {
        Path arquivo = resolver(nome);
        if (arquivo == null) {
            return;
        }
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Não foi possível excluir a imagem " + nome, e);
        }
    }

    public static Path resolver(String nome) {
        if (nome == null || nome.isBlank()) {
            return null;
        }
        try {
            String limpo = Paths.get(nome).getFileName().toString();
            Path arquivo = DIRETORIO.resolve(limpo).normalize();
            if (!arquivo.startsWith(DIRETORIO) || !Files.isRegularFile(arquivo)) {
                return null;
            }
            return arquivo;
        } catch (InvalidPathException e) {
            return null;
        }
    }

    public static String tipoConteudo(String nome) {
        return TIPOS.get(extensao(nome));
    }

    private static String extensao(String nome) {
        if (nome == null) {
            return "";
        }
        int ponto = nome.lastIndexOf('.');
        return ponto < 0 ? "" : nome.substring(ponto + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean assinaturaValida(byte[] b, String extensao) {
        if (b.length < 12) {
            return false;
        }
        return switch (extensao) {
            case "jpg", "jpeg" -> (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
            case "png" -> (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
            case "gif" -> b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8';
            case "webp" -> b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                    && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
            default -> false;
        };
    }
}
