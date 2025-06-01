<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <title>Erro no Sistema</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="container-publico" style="text-align:center;">
    <h1>Ocorreu um Erro Inesperado</h1>
    <p>Não foi possível carregar as informações solicitadas no momento. Por favor, tente novamente mais tarde.</p>
    <p><i>Detalhe do erro (para debug): ${mensagemErro}</i></p>
    <br>
    <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Página Inicial</a>
</div>
</body>
</html>

<jsp:include page="/WEB-INF/jsp/footerPublico.jspf" />