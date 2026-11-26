<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Início" />
</jsp:include>

<%@ include file="/WEB-INF/views/mensagens.jspf" %>

<section class="hero text-center">
    <h1 class="display-5 fw-bold">Sua coleção de música, organizada</h1>
    <p class="lead">
        Cadastre seus vinis, CDs, fitas, pôsteres, camisetas e instrumentos musicais,
        e publique os seus achados para outras pessoas verem.
    </p>
    <c:choose>
        <c:when test="${not empty sessionScope.usuarioLogado}">
            <a class="btn btn-warning btn-lg" href="${ctx}/novoItem">Cadastrar um item</a>
            <a class="btn btn-outline-light btn-lg" href="${ctx}/itens">Ver meus itens</a>
        </c:when>
        <c:otherwise>
            <a class="btn btn-warning btn-lg" href="${ctx}/cadastroUsuario">Criar conta</a>
            <a class="btn btn-outline-light btn-lg" href="${ctx}/login">Entrar</a>
        </c:otherwise>
    </c:choose>
</section>

<h2 class="h4 mt-5 mb-3">Postagens recentes</h2>
<c:choose>
    <c:when test="${empty recentes}">
        <p class="text-muted">Ainda não há postagens. Seja o primeiro a publicar!</p>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach var="p" items="${recentes}">
                <div class="col">
                    <div class="card h-100 card-item">
                        <c:choose>
                            <c:when test="${not empty p.itemImagem}">
                                <img class="capa" src="${ctx}/arquivos/<c:out value='${p.itemImagem}' />" alt="Imagem de <c:out value='${p.itemNome}' />">
                            </c:when>
                            <c:otherwise>
                                <img class="capa capa-vazia" src="${ctx}/assets/img/sem-capa.svg" alt="Sem imagem">
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <span class="badge text-bg-dark"><c:out value="${p.itemTipo}" /></span>
                            <h3 class="h5 card-title mt-2"><c:out value="${p.titulo}" /></h3>
                            <p class="card-text text-muted mb-1">
                                <c:out value="${p.itemNome}" /> &mdash; <c:out value="${p.itemArtista}" />
                            </p>
                            <small class="text-muted">
                                por <c:out value="${p.autorNome}" /> em
                                <fmt:formatDate value="${p.dataPublicacao}" pattern="dd/MM/yyyy" />
                            </small>
                        </div>
                        <a class="stretched-link" href="${ctx}/postagem?id=${p.id}" aria-label="Abrir postagem"></a>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="text-center mt-4">
            <a class="btn btn-outline-dark" href="${ctx}/postagens">Ver todas as postagens</a>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/footer.jsp" />
