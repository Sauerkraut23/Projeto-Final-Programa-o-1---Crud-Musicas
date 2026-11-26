<%@ include file="/WEB-INF/views/base.jspf" %>
<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${empty param.titulo ? 'Colecionáveis de Música' : param.titulo}" /> | Colecionáveis de Música</title>
    <link rel="icon" href="${ctx}/assets/img/sem-capa.svg" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/assets/css/estilos.css">
    <script src="${ctx}/assets/js/bootstrap.bundle.min.js" defer></script>
    <script src="${ctx}/assets/js/confirmacao.js" defer></script>
</head>
<body>
<header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${ctx}/inicio">&#9835; Colecionáveis de Música</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#menuPrincipal" aria-controls="menuPrincipal"
                    aria-expanded="false" aria-label="Abrir menu">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="menuPrincipal">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/inicio">Início</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/postagens">Postagens</a></li>
                    <c:if test="${not empty sessionScope.usuarioLogado}">
                        <li class="nav-item"><a class="nav-link" href="${ctx}/itens">Meus itens</a></li>
                        <li class="nav-item"><a class="nav-link" href="${ctx}/minhasPostagens">Minhas postagens</a></li>
                    </c:if>
                </ul>
                <ul class="navbar-nav align-items-lg-center">
                    <c:choose>
                        <c:when test="${not empty sessionScope.usuarioLogado}">
                            <li class="nav-item">
                                <span class="navbar-text me-lg-3">Olá, <c:out value="${sessionScope.usuarioLogado.nome}" /></span>
                            </li>
                            <li class="nav-item">
                                <a class="btn btn-outline-light btn-sm" href="${ctx}/logout">Sair</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item"><a class="nav-link" href="${ctx}/login">Entrar</a></li>
                            <li class="nav-item">
                                <a class="btn btn-warning btn-sm" href="${ctx}/cadastroUsuario">Criar conta</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
</header>
<main class="container py-4">
