<%@ include file="/WEB-INF/views/base.jspf" %>
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Entrar" />
</jsp:include>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <h1 class="h3 mb-3">Entrar</h1>
        <%@ include file="/WEB-INF/views/mensagens.jspf" %>

        <form action="${ctx}/login" method="post">
            <div class="mb-3">
                <label class="form-label" for="email">E-mail</label>
                <input class="form-control" type="email" id="email" name="email" maxlength="150"
                       value="<c:out value='${requestScope.email}' />" required autofocus>
            </div>
            <div class="mb-3">
                <label class="form-label" for="senha">Senha</label>
                <input class="form-control" type="password" id="senha" name="senha" required>
            </div>
            <button class="btn btn-dark" type="submit">Entrar</button>
            <a class="btn btn-link" href="${ctx}/cadastroUsuario">Criar conta</a>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer.jsp" />
