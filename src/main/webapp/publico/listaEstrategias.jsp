<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="mensagens"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="strategies.list.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Seus estilos específicos permanecem aqui */
        .container-publico { max-width: 900px; margin: 30px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .estrategia-item { border-bottom: 1px solid #eee; padding: 15px 0; }
        .estrategia-item:last-child { border-bottom: none; }
        .estrategia-item h3 { margin-top: 0; color: #007bff; }
        .estrategia-item p { margin-bottom: 8px; line-height: 1.6; }
        .breadcrumb-nav { margin-bottom: 20px; font-size: 0.9em; }
        .breadcrumb-nav a { color: #007bff; text-decoration: none; }
        .breadcrumb-nav a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container-publico">
    <nav aria-label="breadcrumb" class="breadcrumb-nav">
        <ol style="list-style:none; padding:0; margin:0; display:flex;">
            <li style="margin-right:5px;"><a href="${pageContext.request.contextPath}/index.jsp"><fmt:message key="strategies.list.breadcrumb.home"/></a></li>
            <li class="active" aria-current="page" style="color:#6c757d;">&nbsp;/ <fmt:message key="strategies.list.breadcrumb.strategies"/></li>
        </ol>
    </nav>

    <h1><fmt:message key="strategies.list.header"/></h1>
    <p><fmt:message key="strategies.list.intro"/></p>

    <%-- O Servlet deve passar a CHAVE da mensagem de erro, não o texto. --%>
    <c:if test="${not empty mensagemErro}">
        <div class="mensagem mensagem-erro"><fmt:message key="${mensagemErro}"/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty listaEstrategias}">
            <p><fmt:message key="strategies.list.empty"/></p>
        </c:when>
        <c:otherwise>
            <c:forEach var="estrategia" items="${listaEstrategias}">
                <article class="estrategia-item">
                    <h3><c:out value="${estrategia.nome}"/></h3>
                    <p><strong><fmt:message key="strategies.list.label.description"/></strong> <c:out value="${estrategia.descricao}"/></p>
                    <c:if test="${not empty estrategia.exemplos}">
                        <p><strong><fmt:message key="strategies.list.label.examples"/></strong> <c:out value="${estrategia.exemplos}"/></p>
                    </c:if>
                    <c:if test="${not empty estrategia.dicas}">
                        <p><strong><fmt:message key="strategies.list.label.tips"/></strong> <c:out value="${estrategia.dicas}"/></p>
                    </c:if>
                </article>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary"><fmt:message key="strategies.list.button.restrictedArea"/></a>
    </div>
</div>

<%-- Incluindo rodapé Publico --%>
<jsp:include page="/WEB-INF/jsp/footerPublico.jspf" />

</body>
</html>
