<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Postagens" />
</jsp:include>

<h1 class="h3 mb-3">Postagens da comunidade</h1>
<%@ include file="/WEB-INF/views/mensagens.jspf" %>

<form class="row g-2 mb-4" action="${ctx}/postagens" method="get">
    <div class="col-sm-8 col-md-6">
        <input class="form-control" type="search" name="q" placeholder="Buscar por título, item ou artista"
               value="<c:out value='${busca}' />">
    </div>
    <div class="col-auto">
        <button class="btn btn-outline-dark" type="submit">Buscar</button>
        <c:if test="${not empty busca}">
            <a class="btn btn-link" href="${ctx}/postagens">Limpar</a>
        </c:if>
    </div>
</form>

<c:choose>
    <c:when test="${empty postagens}">
        <div class="alert alert-secondary">Nenhuma postagem encontrada.</div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-2 g-4">
            <c:forEach var="p" items="${postagens}">
                <div class="col">
                    <div class="card h-100 card-item flex-row overflow-hidden">
                        <c:choose>
                            <c:when test="${not empty p.itemImagem}">
                                <img class="capa-lateral" src="${ctx}/arquivos/<c:out value='${p.itemImagem}' />" alt="Imagem de <c:out value='${p.itemNome}' />">
                            </c:when>
                            <c:otherwise>
                                <img class="capa-lateral capa-vazia" src="${ctx}/assets/img/sem-capa.svg" alt="Sem imagem">
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <span class="badge text-bg-dark"><c:out value="${p.itemTipo}" /></span>
                            <h2 class="h5 card-title mt-2"><c:out value="${p.titulo}" /></h2>
                            <p class="small text-muted mb-2">
                                <c:out value="${p.itemNome}" /> &mdash; <c:out value="${p.itemArtista}" />
                            </p>
                            <p class="card-text">
                                <c:choose>
                                    <c:when test="${fn:length(p.conteudo) > 140}"><c:out value="${fn:substring(p.conteudo, 0, 140)}" />&hellip;</c:when>
                                    <c:otherwise><c:out value="${p.conteudo}" /></c:otherwise>
                                </c:choose>
                            </p>
                            <small class="text-muted">
                                por <c:out value="${p.autorNome}" /> em
                                <fmt:formatDate value="${p.dataPublicacao}" pattern="dd/MM/yyyy HH:mm" />
                            </small>
                        </div>
                        <a class="stretched-link" href="${ctx}/postagem?id=${p.id}" aria-label="Abrir postagem"></a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/footer.jsp" />
