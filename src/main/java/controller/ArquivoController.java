package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Arquivos;

@WebServlet("/arquivos/*")
public class ArquivoController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ArquivoController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caminho = request.getPathInfo();   // ex.: "/3f2a...c1.png"
        if (caminho == null || caminho.length() <= 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nome = caminho.substring(1);
        Path arquivo = Arquivos.resolver(nome);
        String tipo = Arquivos.tipoConteudo(nome);

        if (arquivo == null || tipo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            response.setContentType(tipo);
            response.setContentLengthLong(Files.size(arquivo));
            response.setHeader("Cache-Control", "public, max-age=86400");
            response.setHeader("X-Content-Type-Options", "nosniff");
            Files.copy(arquivo, response.getOutputStream());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Falha ao enviar a imagem " + nome, ex);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
