<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="pt" />
<%-- Define o bundle de mensagens que será usado na página --%>
<fmt:setBundle basename="mensagens"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <title><fmt:message key="error.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="container-publico" style="text-align:center;">
    <h1><fmt:message key="error.page.header"/></h1>
    <p><fmt:message key="error.page.message"/></p>

    <%-- Exibe a mensagem de erro para debug, se ela existir no request --%>
    <c:if test="${not empty mensagemErro}">
        <p>
            <i><fmt:message key="error.page.debugLabel"/> ${mensagemErro}</i>
        </p>
    </c:if>

    <br>
    <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
        <fmt:message key="error.page.homeLink"/>
    </a>
</div>

<%-- O include do rodapé continua funcionando normalmente --%>
<jsp:include page="/WEB-INF/jsp/footerPublico.jsp" />

</body>
</html>