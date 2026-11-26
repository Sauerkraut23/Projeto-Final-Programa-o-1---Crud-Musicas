<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Criar conta" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <h1 class="h3 mb-3">Criar conta</h1>
        <%@ include file="/WEB-INF/views/mensagens.jspf" %>

        <form action="${ctx}/cadastroUsuario" method="post">
            <div class="mb-3">
                <label class="form-label" for="nome">Nome</label>
                <input class="form-control" type="text" id="nome" name="nome" maxlength="100"
                       value="<c:out value='${requestScope.nome}' />" required autofocus>
            </div>
            <div class="mb-3">
                <label class="form-label" for="email">E-mail</label>
                <input class="form-control" type="email" id="email" name="email" maxlength="150"
                       value="<c:out value='${requestScope.email}' />" required>
            </div>
            <div class="mb-3">
                <label class="form-label" for="senha">Senha (mínimo 6 caracteres)</label>
                <input class="form-control" type="password" id="senha" name="senha" minlength="6" required>
            </div>
            <div class="mb-3">
                <label class="form-label" for="confirmacao">Confirmar senha</label>
                <input class="form-control" type="password" id="confirmacao" name="confirmacao" minlength="6" required>
            </div>
            <button class="btn btn-dark" type="submit">Cadastrar</button>
            <a class="btn btn-link" href="${ctx}/login">Já tenho conta</a>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer.jsp" />
