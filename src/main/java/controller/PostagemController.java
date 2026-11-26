package controller;

import dao.CadastroProjetos;
import dao.PostagemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Postagem;
import model.Projeto;
import model.Usuario;

@WebServlet(urlPatterns = {
        "/postagens", "/postagem", "/minhasPostagens",
        "/novaPostagem", "/editarPostagem", "/excluirPostagem"
})
public class PostagemController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PostagemController.class.getName());

    private final PostagemDAO postagemDao = new PostagemDAO();
    private final CadastroProjetos projetoDao = new CadastroProjetos();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rota = request.getServletPath();

        try {
            switch (rota) {
                case "/postagens" -> listarPublicas(request, response);
                case "/postagem" -> detalhar(request, response);
                case "/minhasPostagens" -> listarMinhas(request, response);
                case "/novaPostagem" -> novaPostagemFormulario(request, response);
                case "/editarPostagem" -> editarFormulario(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (SQLException ex) {
            mostrarErro(request, response, ex, "Não foi possível acessar o banco de dados.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rota = request.getServletPath();

        try {
            switch (rota) {
                case "/novaPostagem" -> salvar(request, response, false);
                case "/editarPostagem" -> salvar(request, response, true);
                case "/excluirPostagem" -> excluir(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            request.getSession().setAttribute("msgErro", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/minhasPostagens");
        } catch (SQLException ex) {
            mostrarErro(request, response, ex, "Não foi possível salvar no banco de dados.");
        }
    }

    private void listarPublicas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String busca = request.getParameter("q");
        request.setAttribute("postagens", postagemDao.listarPublicadas(busca));
        request.setAttribute("busca", busca);
        request.getRequestDispatcher("/WEB-INF/views/postagens.jsp").forward(request, response);
    }

    private void detalhar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Postagem postagem = postagemDao.buscarPorId(lerId(request));
        if (postagem == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
            return;
        }
        request.setAttribute("postagem", postagem);
        request.getRequestDispatcher("/WEB-INF/views/postagem.jsp").forward(request, response);
    }

    private void listarMinhas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        request.setAttribute("postagens", postagemDao.listarPorAutor(usuarioLogado(request).getId()));
        request.getRequestDispatcher("/WEB-INF/views/minhasPostagens.jsp").forward(request, response);
    }

    private void novaPostagemFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Postagem postagem = new Postagem();

        String item = request.getParameter("item");
        if (item != null && item.matches("\\d{1,9}")) {
            int projetoId = Integer.parseInt(item);
            if (postagemDao.itemJaPublicado(projetoId, 0)) {
                request.setAttribute("erro", "Este item já está publicado. Escolha outro item.");
            } else {
                postagem.setProjetoId(projetoId);
            }
        }
        mostrarFormulario(request, response, postagem);
    }

    private void editarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Postagem postagem = postagemDao.buscarPorIdEAutor(lerId(request), usuarioLogado(request).getId());
        if (postagem == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
            return;
        }
        mostrarFormulario(request, response, postagem);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response, boolean edicao)
            throws ServletException, IOException, SQLException {
        Usuario usuario = usuarioLogado(request);
        Postagem postagem = new Postagem();
        postagem.setAutorId(usuario.getId());

        try {
            if (edicao) {
                postagem.setId(lerId(request));
                if (postagemDao.buscarPorIdEAutor(postagem.getId(), usuario.getId()) == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Postagem não encontrada.");
                    return;
                }
            }

            postagem.setTitulo(texto(request, "titulo"));
            postagem.setConteudo(texto(request, "conteudo"));
            postagem.setProjetoId(lerProjetoId(request));
            validar(postagem, usuario);

            boolean ok = edicao ? postagemDao.atualizar(postagem) : postagemDao.cadastrar(postagem);
            if (!ok) {
                throw new IllegalArgumentException("Não foi possível salvar a postagem.");
            }
        } catch (IllegalArgumentException ex) {
            request.setAttribute("erro", ex.getMessage());
            mostrarFormulario(request, response, postagem);
            return;
        }

        request.getSession().setAttribute("msgSucesso",
                edicao ? "Postagem atualizada com sucesso." : "Postagem publicada com sucesso.");
        response.sendRedirect(request.getContextPath() + "/minhasPostagens");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        int id = lerId(request);

        if (postagemDao.excluir(id, usuarioLogado(request).getId())) {
            request.getSession().setAttribute("msgSucesso", "Postagem excluída com sucesso.");
        } else {
            request.getSession().setAttribute("msgErro", "Postagem não encontrada ou sem permissão para excluir.");
        }
        response.sendRedirect(request.getContextPath() + "/minhasPostagens");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Postagem postagem)
            throws ServletException, IOException, SQLException {
        List<Projeto> itens = projetoDao.listarDisponiveisParaPostagem(
                usuarioLogado(request).getId(), postagem.getProjetoId());

        if (itens.isEmpty()) {
            request.getSession().setAttribute("msgErro",
                    "Você não tem itens disponíveis para publicar. Cada item só pode ter uma postagem: "
                    + "cadastre um novo item.");
            response.sendRedirect(request.getContextPath() + "/itens");
            return;
        }

        request.setAttribute("postagem", postagem);
        request.setAttribute("itens", itens);
        request.getRequestDispatcher("/WEB-INF/views/postagemForm.jsp").forward(request, response);
    }

    private void validar(Postagem postagem, Usuario usuario) throws SQLException {
        if (postagem.getTitulo().isEmpty() || postagem.getTitulo().length() > 180) {
            throw new IllegalArgumentException("Informe um título de até 180 caracteres.");
        }
        if (postagem.getConteudo().isEmpty() || postagem.getConteudo().length() > 5000) {
            throw new IllegalArgumentException("Escreva o texto da postagem (até 5000 caracteres).");
        }
        if (projetoDao.buscarPorIdEUsuario(postagem.getProjetoId(), usuario.getId()) == null) {
            throw new IllegalArgumentException("Escolha um dos seus itens para publicar.");
        }
        if (postagemDao.itemJaPublicado(postagem.getProjetoId(), postagem.getId())) {
            throw new IllegalArgumentException("Este item já está publicado. Edite ou exclua a postagem que já existe.");
        }
    }

    private int lerProjetoId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("projetoId"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Escolha o item que será publicado.");
        }
    }

    private int lerId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Identificador inválido.");
        }
    }

    private String texto(HttpServletRequest request, String campo) {
        String valor = request.getParameter(campo);
        return valor == null ? "" : valor.trim();
    }

    private Usuario usuarioLogado(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuarioLogado");
    }

    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, Exception ex, String mensagem)
            throws ServletException, IOException {
        LOGGER.log(Level.SEVERE, mensagem, ex);
        request.setAttribute("mensagemErro", mensagem);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(request, response);
    }
}
