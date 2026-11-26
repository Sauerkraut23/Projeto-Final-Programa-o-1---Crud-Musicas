package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/itens", "/novoItem", "/editarItem", "/excluirItem",
        "/minhasPostagens", "/novaPostagem", "/editarPostagem", "/excluirPostagem"
})
public class Autenticacao implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        HttpSession sessao = request.getSession(false);
        boolean logado = sessao != null && sessao.getAttribute("usuarioLogado") != null;

        if (logado) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession nova = request.getSession();
        if ("GET".equals(request.getMethod())) {
            String destino = request.getRequestURI();
            if (request.getQueryString() != null) {
                destino += "?" + request.getQueryString();
            }
            nova.setAttribute("destinoLogin", destino);
        }
        nova.setAttribute("msgErro", "Faça login para acessar esta página.");
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
