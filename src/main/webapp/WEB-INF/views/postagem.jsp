<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="${postagem.titulo}" />
</jsp:include>

<%@ include file="/WEB-INF/views/mensagens.jspf" %>

<a class="btn btn-link px-0 mb-2" href="${ctx}/postagens">&larr; Voltar às postagens</a>

<article class="row g-4">
    <div class="col-md-4">
        <c:choose>
            <c:when test="${not empty postagem.itemImagem}">
                <img class="img-fluid rounded border w-100" src="${ctx}/arquivos/<c:out value='${postagem.itemImagem}' />"
                     alt="Imagem de <c:out value='${postagem.itemNome}' />">
            </c:when>
            <c:otherwise>
                <img class="img-fluid rounded border w-100 capa-vazia" src="${ctx}/assets/img/sem-capa.svg" alt="Sem imagem">
            </c:otherwise>
        </c:choose>

        <ul class="list-group list-group-flush mt-3">
            <li class="list-group-item"><strong>Item:</strong> <c:out value="${postagem.itemNome}" /></li>
            <li class="list-group-item"><strong>Artista/Marca:</strong> <c:out value="${postagem.itemArtista}" /></li>
            <li class="list-group-item"><strong>Tipo:</strong> <c:out value="${postagem.itemTipo}" /></li>
            <c:if test="${not empty postagem.itemAno}">
                <li class="list-group-item"><strong>Ano:</strong> <c:out value="${postagem.itemAno}" /></li>
            </c:if>
            <li class="list-group-item"><strong>Estado:</strong> <c:out value="${postagem.itemCondicao}" /></li>
        </ul>
    </div>

    <div class="col-md-8">
        <h1 class="h2"><c:out value="${postagem.titulo}" /></h1>
        <p class="text-muted">
            por <strong><c:out value="${postagem.autorNome}" /></strong> em
            <fmt:formatDate value="${postagem.dataPublicacao}" pattern="dd/MM/yyyy 'às' HH:mm" />
        </p>
        <div class="texto-postagem"><c:out value="${postagem.conteudo}" /></div>

        <c:if test="${not empty sessionScope.usuarioLogado and sessionScope.usuarioLogado.id == postagem.autorId}">
            <div class="mt-4 d-flex gap-2">
                <a class="btn btn-outline-dark" href="${ctx}/editarPostagem?id=${postagem.id}">Editar</a>
                <button type="button" class="btn btn-outline-danger"
                        data-bs-toggle="modal" data-bs-target="#modalExcluir"
                        data-id="${postagem.id}" data-nome="<c:out value='${postagem.titulo}' />"
                        data-action="${ctx}/excluirPostagem">Excluir</button>
            </div>
            <%@ include file="/WEB-INF/views/modalExcluir.jspf" %>
        </c:if>
    </div>
</article>

<jsp:include page="/WEB-INF/views/footer.jsp" />
