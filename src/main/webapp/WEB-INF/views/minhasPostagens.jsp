<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Minhas postagens" />
</jsp:include>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h1 class="h3 m-0">Minhas postagens</h1>
    <a class="btn btn-dark" href="${ctx}/novaPostagem">+ Nova postagem</a>
</div>

<%@ include file="/WEB-INF/views/mensagens.jspf" %>

<c:choose>
    <c:when test="${empty postagens}">
        <div class="alert alert-secondary">
            Você ainda não publicou nada. <a href="${ctx}/novaPostagem">Crie sua primeira postagem.</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-striped align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Título</th>
                        <th>Item</th>
                        <th>Publicada em</th>
                        <th class="text-end">Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${postagens}">
                        <tr>
                            <td><a href="${ctx}/postagem?id=${p.id}"><c:out value="${p.titulo}" /></a></td>
                            <td><c:out value="${p.itemNome}" /> <span class="text-muted">(<c:out value="${p.itemTipo}" />)</span></td>
                            <td><fmt:formatDate value="${p.dataPublicacao}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td class="text-end text-nowrap">
                                <a class="btn btn-sm btn-outline-dark" href="${ctx}/editarPostagem?id=${p.id}">Editar</a>
                                <button type="button" class="btn btn-sm btn-outline-danger"
                                        data-bs-toggle="modal" data-bs-target="#modalExcluir"
                                        data-id="${p.id}" data-nome="<c:out value='${p.titulo}' />"
                                        data-action="${ctx}/excluirPostagem">Excluir</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/modalExcluir.jspf" %>

<jsp:include page="/WEB-INF/views/footer.jsp" />
