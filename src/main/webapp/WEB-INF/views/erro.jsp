<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="titulo" value="Ops!" />
</jsp:include>

<div class="text-center py-5">
    <h1 class="display-5">Ops! Algo deu errado</h1>
    <c:choose>
        <c:when test="${not empty requestScope.mensagemErro}">
            <p class="lead"><c:out value="${requestScope.mensagemErro}" /></p>
        </c:when>
        <c:when test="${codigo == 404}">
            <p class="lead">A página ou o registro que você procura não foi encontrado.</p>
        </c:when>
        <c:when test="${codigo == 400}">
            <p class="lead">A requisição não pôde ser entendida. Confira os dados e tente novamente.</p>
        </c:when>
        <c:otherwise>
            <p class="lead">Ocorreu um erro inesperado. Tente novamente em instantes.</p>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty codigo}">
        <p class="text-muted">Código do erro: <c:out value="${codigo}" /></p>
    </c:if>
    <a class="btn btn-dark" href="${ctx}/inicio">Voltar ao início</a>
</div>

<jsp:include page="/WEB-INF/views/footer.jsp" />
