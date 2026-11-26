package controller;

import dao.PostagemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/inicio"})
public class InicioController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(InicioController.class.getName());

    private final PostagemDAO postagemDao = new PostagemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("recentes", postagemDao.listarRecentes(6));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Não foi possível carregar as postagens recentes.", ex);
            request.setAttribute("erro", "Não foi possível carregar as postagens recentes no momento.");
        }
        request.getRequestDispatcher("/WEB-INF/views/inicio.jsp").forward(request, response);
    }
}
