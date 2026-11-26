<%@ include file="/WEB-INF/views/base.jspf" %>
<c:set var="editando" value="${item.id > 0}" />
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="${editando ? 'Editar item' : 'Novo item'}" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <h1 class="h3 mb-3"><c:out value="${editando ? 'Editar item' : 'Novo item colecionável'}" /></h1>
        <%@ include file="/WEB-INF/views/mensagens.jspf" %>

        <form action="${ctx}/${editando ? 'editarItem' : 'novoItem'}" method="post" enctype="multipart/form-data">
            <c:if test="${editando}">
                <input type="hidden" name="id" value="${item.id}">
            </c:if>

            <div class="row g-3">
                <div class="col-md-8">
                    <label class="form-label" for="nome">Nome do item *</label>
                    <input class="form-control" type="text" id="nome" name="nome" maxlength="150" required
                           placeholder="Ex.: Abbey Road, Stratocaster 1978..."
                           value="<c:out value='${item.nome}' />">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="tipo">Tipo *</label>
                    <select class="form-select" id="tipo" name="tipo" required>
                        <c:forEach var="t" items="${tipos}">
                            <option value="<c:out value='${t}' />" ${t == item.tipo ? 'selected' : ''}><c:out value="${t}" /></option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-6">
                    <label class="form-label" for="artista">Artista / Banda / Marca *</label>
                    <input class="form-control" type="text" id="artista" name="artista" maxlength="150" required
                           value="<c:out value='${item.artista}' />">
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="gravadora">Gravadora / Fabricante</label>
                    <input class="form-control" type="text" id="gravadora" name="gravadora" maxlength="100"
                           value="<c:out value='${item.gravadora}' />">
                </div>

                <div class="col-md-4">
                    <label class="form-label" for="anoLancamento">Ano</label>
                    <input class="form-control" type="number" id="anoLancamento" name="anoLancamento" min="1800" max="2100"
                           value="<c:out value='${item.anoLancamento}' />">
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="condicao">Estado de conservação *</label>
                    <select class="form-select" id="condicao" name="condicao" required>
                        <c:forEach var="cd" items="${condicoes}">
                            <option value="<c:out value='${cd}' />" ${cd == item.condicao ? 'selected' : ''}><c:out value="${cd}" /></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label" for="valorPago">Valor pago (R$)</label>
                    <input class="form-control" type="text" id="valorPago" name="valorPago" inputmode="decimal"
                           placeholder="Ex.: 150,00"
                           value="<c:out value='${item.valorPago}' />">
                </div>

                <div class="col-12">
                    <label class="form-label" for="observacoes">Observações</label>
                    <textarea class="form-control" id="observacoes" name="observacoes" rows="3" maxlength="2000"><c:out value="${item.observacoes}" /></textarea>
                </div>

                <div class="col-12">
                    <label class="form-label" for="imagem">Capa / foto (JPG, PNG, GIF ou WEBP, até 5 MB)</label>
                    <input class="form-control" type="file" id="imagem" name="imagem"
                           accept=".jpg,.jpeg,.png,.gif,.webp,image/*">
                    <c:if test="${editando and not empty item.imagem}">
                        <div class="form-text">
                            Imagem atual abaixo. Envie outra para substituir; se não enviar, ela é mantida.
                        </div>
                        <img class="mt-2 rounded border" style="max-height: 140px"
                             src="${ctx}/arquivos/<c:out value='${item.imagem}' />" alt="Imagem atual">
                    </c:if>
                </div>
            </div>

            <div class="mt-4">
                <button class="btn btn-dark" type="submit"><c:out value="${editando ? 'Salvar alterações' : 'Cadastrar item'}" /></button>
                <a class="btn btn-link" href="${ctx}/itens">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer.jsp" />
