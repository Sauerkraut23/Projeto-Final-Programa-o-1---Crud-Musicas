<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Meus itens" />
</jsp:include>
<fmt:setLocale value="pt_BR" />

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 m-0">Meus itens colecionáveis</h1>
    <a class="btn btn-dark" href="${ctx}/novoItem">+ Novo item</a>
</div>

<%@ include file="/WEB-INF/views/mensagens.jspf" %>

<form class="row g-2 mb-4" action="${ctx}/itens" method="get">
    <div class="col-sm-8 col-md-6">
        <input class="form-control" type="search" name="q" placeholder="Buscar por nome ou artista"
               value="<c:out value='${busca}' />">
    </div>
    <div class="col-auto">
        <button class="btn btn-outline-dark" type="submit">Buscar</button>
        <c:if test="${not empty busca}">
            <a class="btn btn-link" href="${ctx}/itens">Limpar</a>
        </c:if>
    </div>
</form>

<c:choose>
    <c:when test="${empty itens}">
        <div class="alert alert-secondary">
            Nenhum item encontrado. <a href="${ctx}/novoItem">Cadastre o primeiro!</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:forEach var="i" items="${itens}">
                <div class="col">
                    <div class="card h-100 card-item">
                        <c:choose>
                            <c:when test="${not empty i.imagem}">
                                <img class="capa" src="${ctx}/arquivos/<c:out value='${i.imagem}' />" alt="Imagem de <c:out value='${i.nome}' />">
                            </c:when>
                            <c:otherwise>
                                <img class="capa capa-vazia" src="${ctx}/assets/img/sem-capa.svg" alt="Sem imagem">
                            </c:otherwise>
                        </c:choose>
                        <div class="card-body">
                            <h2 class="h5 card-title"><c:out value="${i.nome}" /></h2>
                            <p class="card-text mb-2"><c:out value="${i.artista}" /></p>
                            <span class="badge text-bg-dark"><c:out value="${i.tipo}" /></span>
                            <span class="badge text-bg-secondary"><c:out value="${i.condicao}" /></span>
                            <c:if test="${not empty i.anoLancamento}">
                                <span class="badge text-bg-light border"><c:out value="${i.anoLancamento}" /></span>
                            </c:if>
                            <c:if test="${not empty i.gravadora}">
                                <p class="small text-muted mt-2 mb-0"><c:out value="${i.gravadora}" /></p>
                            </c:if>
                            <c:if test="${not empty i.valorPago}">
                                <p class="small text-muted mb-0">
                                    Pago: <fmt:formatNumber value="${i.valorPago}" type="currency" currencySymbol="R$ " />
                                </p>
                            </c:if>
                        </div>
                        <div class="card-footer bg-transparent d-flex gap-2 flex-wrap">
                            <a class="btn btn-sm btn-outline-dark" href="${ctx}/editarItem?id=${i.id}">Editar</a>
                            <c:choose>
                                <c:when test="${not empty i.postagemId}">
                                    <a class="btn btn-sm btn-success" href="${ctx}/postagem?id=${i.postagemId}">Publicado &#10003;</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="btn btn-sm btn-outline-success" href="${ctx}/novaPostagem?item=${i.id}">Publicar</a>
                                </c:otherwise>
                            </c:choose>
                            <button type="button" class="btn btn-sm btn-outline-danger ms-auto"
                                    data-bs-toggle="modal" data-bs-target="#modalExcluir"
                                    data-id="${i.id}" data-nome="<c:out value='${i.nome}' />"
                                    data-action="${ctx}/excluirItem">Excluir</button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/modalExcluir.jspf" %>

<jsp:include page="/WEB-INF/views/footer.jsp" />
