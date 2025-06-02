<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="pt" />
<%-- Define o bundle de mensagens que será usado na página --%>
<fmt:setBundle basename="message"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="dashboard.tester.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="page-container">
    <aside class="sidebar">
        <h2><fmt:message key="dashboard.tester.sidebar.systemName"/></h2>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/testador/dashboard.jsp" class="active"><fmt:message key="dashboard.tester.sidebar.link.dashboard"/></a></li>
                <li><a href="${pageContext.request.contextPath}/testador/sessoes?action=novo"><fmt:message key="dashboard.tester.sidebar.link.newSession"/></a></li>
                <li><a href="${pageContext.request.contextPath}/testador/minhasSessoes"><fmt:message key="dashboard.tester.sidebar.link.mySessions"/></a></li>
                <li><a href="${pageContext.request.contextPath}/testador/meusProjetos"><fmt:message key="dashboard.tester.sidebar.link.myProjects"/></a></li>
                <li><a href="#"><fmt:message key="dashboard.tester.sidebar.link.viewStrategies"/></a></li>
            </ul>
        </nav>
        <c:if test="${not empty sessionScope.usuarioLogado}">
            <div class="user-info">
                <p><strong><fmt:message key="dashboard.tester.userInfo.user"/></strong><br><c:out value="${sessionScope.usuarioLogado.nome}"/></p>
                <p><strong><fmt:message key="dashboard.tester.userInfo.email"/></strong><br><c:out value="${sessionScope.usuarioLogado.email}"/></p>
                <p><strong><fmt:message key="dashboard.tester.userInfo.profile"/></strong><br><c:out value="${sessionScope.usuarioLogado.tipoPerfil}"/></p>
            </div>
        </c:if>
    </aside>

    <main class="main-content">
        <div class="header">
            <h1><fmt:message key="dashboard.tester.header.title"/></h1>
            <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn"><fmt:message key="dashboard.tester.header.logout"/></a>
        </div>

        <p class="welcome-message">
            <fmt:message key="dashboard.tester.welcomeMessage">
                <fmt:param><strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong></fmt:param>
            </fmt:message>
        </p>

        <%-- As mensagens de sucesso/erro devem vir como CHAVES do Servlet --%>
        <c:if test="${not empty sessionScope.mensagemSucesso}">
            <div class="mensagem mensagem-sucesso">
                <fmt:message key="${sessionScope.mensagemSucesso}"/>
            </div>
            <c:remove var="mensagemSucesso" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErro}">
            <div class="mensagem mensagem-erro">
                <fmt:message key="${sessionScope.mensagemErro}"/>
            </div>
            <c:remove var="mensagemErro" scope="session"/>
        </c:if>

        <div class="dashboard-widgets">
            <section class="widget">
                <h3><fmt:message key="dashboard.tester.widget.myActivities.title"/></h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/testador/sessoes?action=novo"><fmt:message key="dashboard.tester.widget.myActivities.item1"/></a> (R7)</li>
                    <li><a href="${pageContext.request.contextPath}/testador/minhasSessoes"><fmt:message key="dashboard.tester.widget.myActivities.item2"/></a> (R8, R9)</li>
                </ul>
            </section>

            <section class="widget">
                <h3><fmt:message key="dashboard.tester.widget.resources.title"/></h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/testador/meusProjetos"><fmt:message key="dashboard.tester.widget.resources.item1"/></a> (R4)</li>
                    <li><a href="#"><fmt:message key="dashboard.tester.widget.resources.item2"/></a> (R6)</li>
                </ul>
            </section>

            <section class="widget">
                <h3><fmt:message key="dashboard.tester.widget.myProfile.title"/></h3>
                <ul>
                    <li><a href="#"><fmt:message key="dashboard.tester.widget.myProfile.item1"/></a></li>
                    <li><a href="#"><fmt:message key="dashboard.tester.widget.myProfile.item2"/></a></li>
                </ul>
            </section>
        </div>
    </main>
</div>

<footer class="footer">
    <jsp:useBean id="javaDate" class="java.util.Date" />
    <c:set var="currentYear" value="${1900 + javaDate.year}"/>
    <p>
        <fmt:message key="footer.copyright">
            <fmt:param value="${currentYear}"/>
        </fmt:message>
    </p>
</footer>

</body>
</html>