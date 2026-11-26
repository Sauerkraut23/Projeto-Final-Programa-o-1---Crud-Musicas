package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import model.Usuario;

@WebServlet(urlPatterns = {"/login", "/cadastroUsuario", "/logout"})
public class UsuarioController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UsuarioController.class.getName());
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioDAO usuarioDao = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rota = request.getServletPath();

        switch (rota) {
            case "/login" -> encaminhar(request, response, "login");
            case "/cadastroUsuario" -> encaminhar(request, response, "cadastroUsuario");
            case "/logout" -> {
                HttpSession sessao = request.getSession(false);
                if (sessao != null) {
                    sessao.invalidate();   
                }
                response.sendRedirect(request.getContextPath() + "/login");
            }
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rota = request.getServletPath();

        try {
            if ("/login".equals(rota)) {
                entrar(request, response);
            } else if ("/cadastroUsuario".equals(rota)) {
                cadastrar(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            request.setAttribute("erro", ex.getMessage());
            encaminhar(request, response, rota.substring(1));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro de banco de dados na rota " + rota, ex);
            request.setAttribute("erro", "Não foi possível acessar o banco de dados. Tente novamente em instantes.");
            encaminhar(request, response, rota.substring(1));
        }
    }

    private void entrar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        request.setAttribute("email", email);

        if (vazio(email) || vazio(senha)) {
            throw new IllegalArgumentException("Informe o e-mail e a senha.");
        }

        Usuario usuario = usuarioDao.autenticar(email, senha);
        if (usuario == null) {
            throw new IllegalArgumentException("E-mail ou senha inválidos.");
        }

        HttpSession sessao = request.getSession();
        request.changeSessionId();
        sessao.setAttribute("usuarioLogado", usuario);

        String destino = (String) sessao.getAttribute("destinoLogin");
        sessao.removeAttribute("destinoLogin");
        response.sendRedirect(destino != null ? destino : request.getContextPath() + "/itens");
    }

    private void cadastrar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmacao = request.getParameter("confirmacao");
        request.setAttribute("nome", nome);
        request.setAttribute("email", email);

        if (vazio(nome) || nome.trim().length() < 2 || nome.trim().length() > 100) {
            throw new IllegalArgumentException("Informe um nome com 2 a 100 caracteres.");
        }
        if (vazio(email) || email.trim().length() > 150 || !EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            
            throw new IllegalArgumentException("A senha é muito longa (máximo de 72 caracteres).");
        }
        if (!senha.equals(confirmacao)) {
            throw new IllegalArgumentException("A confirmação de senha não confere.");
        }

        usuarioDao.cadastrar(new Usuario(nome.trim(), email.trim(), senha));

        request.getSession().setAttribute("msgSucesso", "Cadastro realizado! Faça login para continuar.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void encaminhar(HttpServletRequest request, HttpServletResponse response, String pagina)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + pagina + ".jsp").forward(request, response);
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
