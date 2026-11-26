<%@ include file="/WEB-INF/views/base.jspf" %>
<c:set var="editando" value="${postagem.id > 0}" />
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="${editando ? 'Editar postagem' : 'Nova postagem'}" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <h1 class="h3 mb-3"><c:out value="${editando ? 'Editar postagem' : 'Nova postagem'}" /></h1>
        <%@ include file="/WEB-INF/views/mensagens.jspf" %>

        <form action="${ctx}/${editando ? 'editarPostagem' : 'novaPostagem'}" method="post">
            <c:if test="${editando}">
                <input type="hidden" name="id" value="${postagem.id}">
            </c:if>

            <div class="mb-3">
                <label class="form-label" for="projetoId">Item que será publicado *</label>
                <select class="form-select" id="projetoId" name="projetoId" required>
                    <option value="">Selecione...</option>
                    <c:forEach var="i" items="${itens}">
                        <option value="${i.id}" ${i.id == postagem.projetoId ? 'selected' : ''}>
                            <c:out value="${i.nome}" /> &mdash; <c:out value="${i.artista}" /> (<c:out value="${i.tipo}" />)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label" for="titulo">Título *</label>
                <input class="form-control" type="text" id="titulo" name="titulo" maxlength="180" required
                       value="<c:out value='${postagem.titulo}' />">
            </div>

            <div class="mb-3">
                <label class="form-label" for="conteudo">Texto *</label>
                <textarea class="form-control" id="conteudo" name="conteudo" rows="8" maxlength="5000" required><c:out value="${postagem.conteudo}" /></textarea>
            </div>

            <button class="btn btn-dark" type="submit"><c:out value="${editando ? 'Salvar alterações' : 'Publicar'}" /></button>
            <a class="btn btn-link" href="${ctx}/minhasPostagens">Cancelar</a>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer.jsp" />
