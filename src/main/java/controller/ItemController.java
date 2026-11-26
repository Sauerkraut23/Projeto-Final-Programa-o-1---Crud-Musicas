package controller;

import dao.CadastroProjetos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Projeto;
import model.Usuario;
import util.Arquivos;

@WebServlet(urlPatterns = {"/itens", "/novoItem", "/editarItem", "/excluirItem"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class ItemController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ItemController.class.getName());

    private final CadastroProjetos projetoDao = new CadastroProjetos();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rota = request.getServletPath();

        try {
            switch (rota) {
                case "/itens" -> listar(request, response);
                case "/novoItem" -> mostrarFormulario(request, response, new Projeto());
                case "/editarItem" -> editarFormulario(request, response);
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
                case "/novoItem" -> salvar(request, response, false);
                case "/editarItem" -> salvar(request, response, true);
                case "/excluirItem" -> excluir(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException ex) {
            request.getSession().setAttribute("msgErro", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/itens");
        } catch (SQLException ex) {
            mostrarErro(request, response, ex, "Não foi possível salvar no banco de dados.");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String busca = request.getParameter("q");
        request.setAttribute("itens", projetoDao.listarPorUsuario(usuarioLogado(request).getId(), busca));
        request.setAttribute("busca", busca);
        request.getRequestDispatcher("/WEB-INF/views/itens.jsp").forward(request, response);
    }

    private void editarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Projeto item = projetoDao.buscarPorIdEUsuario(lerId(request), usuarioLogado(request).getId());
        if (item == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Item não encontrado.");
            return;
        }
        mostrarFormulario(request, response, item);
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response, boolean edicao)
            throws ServletException, IOException, SQLException {
        Usuario usuario = usuarioLogado(request);
        Projeto item = new Projeto();
        item.setUsuarioId(usuario.getId());

        try {
            gravar(request, response, usuario, item, edicao);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("erro", ex.getMessage());
            mostrarFormulario(request, response, item);
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.WARNING, "Envio recusado (provavelmente arquivo grande demais).", ex);
            request.setAttribute("erro", "Não foi possível processar o envio. A imagem deve ter no máximo 5 MB.");
            mostrarFormulario(request, response, item);
        }
    }

    private void gravar(HttpServletRequest request, HttpServletResponse response,
                        Usuario usuario, Projeto item, boolean edicao)
            throws ServletException, IOException, SQLException {

        Projeto atual = null;
        if (edicao) {
            item.setId(lerId(request));
            atual = projetoDao.buscarPorIdEUsuario(item.getId(), usuario.getId());
            if (atual == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Item não encontrado.");
                return;
            }
            item.setImagem(atual.getImagem());
        }

        lerCampos(request, item);

        String imagemNova = null;
        try {
            imagemNova = Arquivos.salvarImagem(request.getPart("imagem"));
            if (imagemNova != null) {
                item.setImagem(imagemNova);
            }

            boolean ok = edicao ? projetoDao.atualizar(item) : projetoDao.cadastrar(item);
            if (!ok) {
                throw new IllegalArgumentException("Não foi possível salvar o item.");
            }
        } catch (SQLException | RuntimeException | IOException ex) {
            Arquivos.excluir(imagemNova);
            throw ex;
        }

        if (edicao && imagemNova != null) {
            Arquivos.excluir(atual.getImagem());
        }

        request.getSession().setAttribute("msgSucesso",
                edicao ? "Item atualizado com sucesso." : "Item cadastrado com sucesso.");
        response.sendRedirect(request.getContextPath() + "/itens");
    }

    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        int usuarioId = usuarioLogado(request).getId();
        int id = lerId(request);

        Projeto atual = projetoDao.buscarPorIdEUsuario(id, usuarioId);
        if (atual == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Item não encontrado.");
            return;
        }

        if (projetoDao.excluir(id, usuarioId)) {
            Arquivos.excluir(atual.getImagem());
            request.getSession().setAttribute("msgSucesso", "Item excluído (as postagens dele também foram removidas).");
        } else {
            request.getSession().setAttribute("msgErro", "Não foi possível excluir o item.");
        }
        response.sendRedirect(request.getContextPath() + "/itens");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Projeto item)
            throws ServletException, IOException {
        request.setAttribute("item", item);
        request.setAttribute("tipos", Projeto.TIPOS);
        request.setAttribute("condicoes", Projeto.CONDICOES);
        request.getRequestDispatcher("/WEB-INF/views/itemForm.jsp").forward(request, response);
    }

    private void lerCampos(HttpServletRequest request, Projeto item) {
        item.setNome(texto(request, "nome"));
        item.setArtista(texto(request, "artista"));
        item.setTipo(texto(request, "tipo"));
        item.setGravadora(vazioParaNulo(texto(request, "gravadora")));
        item.setCondicao(texto(request, "condicao"));
        item.setObservacoes(vazioParaNulo(texto(request, "observacoes")));
        item.setAnoLancamento(lerAno(texto(request, "anoLancamento")));
        item.setValorPago(lerValor(texto(request, "valorPago")));

        if (item.getNome().isEmpty() || item.getNome().length() > 150) {
            throw new IllegalArgumentException("Informe o nome do item (até 150 caracteres).");
        }
        if (item.getArtista().isEmpty() || item.getArtista().length() > 150) {
            throw new IllegalArgumentException("Informe o artista, banda ou marca (até 150 caracteres).");
        }
        if (!Projeto.TIPOS.contains(item.getTipo())) {
            throw new IllegalArgumentException("Escolha um tipo válido.");
        }
        if (!Projeto.CONDICOES.contains(item.getCondicao())) {
            throw new IllegalArgumentException("Escolha um estado de conservação válido.");
        }
        if (item.getGravadora() != null && item.getGravadora().length() > 100) {
            throw new IllegalArgumentException("A gravadora/fabricante deve ter até 100 caracteres.");
        }
        if (item.getObservacoes() != null && item.getObservacoes().length() > 2000) {
            throw new IllegalArgumentException("As observações devem ter até 2000 caracteres.");
        }
    }

    private Integer lerAno(String texto) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            int ano = Integer.parseInt(texto);
            int maximo = Year.now().getValue() + 1;
            if (ano < 1800 || ano > maximo) {
                throw new IllegalArgumentException("O ano de lançamento deve estar entre 1800 e " + maximo + ".");
            }
            return ano;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ano de lançamento inválido.");
        }
    }

    /** Aceita "150,50", "1.234,50" ou "150.50". */
    private BigDecimal lerValor(String texto) {
        if (texto.isEmpty()) {
            return null;
        }
        try {
            String numero = texto.contains(",") ? texto.replace(".", "").replace(",", ".") : texto;
            BigDecimal valor = new BigDecimal(numero).setScale(2, RoundingMode.HALF_UP);
            if (valor.signum() < 0 || valor.compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new IllegalArgumentException("O valor pago deve estar entre 0 e 99.999.999,99.");
            }
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor pago inválido. Exemplo: 150,00");
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

    private String vazioParaNulo(String valor) {
        return valor.isEmpty() ? null : valor;
    }

    private Usuario usuarioLogado(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuarioLogado");
    }

    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, Exception ex, String mensagem)
            throws ServletException, IOException {
        LOGGER.log(Level.SEVERE, mensagem, ex);   // detalhe técnico só no log do servidor
        request.setAttribute("mensagemErro", mensagem);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.getRequestDispatcher("/WEB-INF/views/erro.jsp").forward(request, response);
    }
}
