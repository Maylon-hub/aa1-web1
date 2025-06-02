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
    <title><fmt:message key="dashboard.admin.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="page-container">
    <aside class="sidebar">
        <h2><fmt:message key="dashboard.admin.sidebar.systemName"/></h2>
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="active"><fmt:message key="dashboard.admin.sidebar.link.dashboard"/></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/estrategias?action=listar"><fmt:message key="dashboard.admin.sidebar.link.manageStrategies"/></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/gerenciarProjetos"><fmt:message key="dashboard.admin.sidebar.link.manageProjects"/></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios"><fmt:message key="dashboard.admin.sidebar.link.manageUsers"/></a></li>
                <li><a href="#"><fmt:message key="dashboard.admin.sidebar.link.testSessions"/></a></li>
            </ul>
        </nav>
        <c:if test="${not empty sessionScope.usuarioLogado}">
            <div class="user-info">
                    <%-- Reutilizando chaves do painel de testador --%>
                <p><strong><fmt:message key="dashboard.tester.userInfo.user"/></strong><br><c:out value="${sessionScope.usuarioLogado.nome}"/></p>
                <p><strong><fmt:message key="dashboard.tester.userInfo.email"/></strong><br><c:out value="${sessionScope.usuarioLogado.email}"/></p>
                <p><strong><fmt:message key="dashboard.tester.userInfo.profile"/></strong><br><c:out value="${sessionScope.usuarioLogado.tipoPerfil}"/></p>
            </div>
        </c:if>
    </aside>

    <main class="main-content">
        <div class="header">
            <h1><fmt:message key="dashboard.admin.header.title"/></h1>
            <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn"><fmt:message key="dashboard.tester.header.logout"/></a>
        </div>

        <p class="welcome-message">
            <fmt:message key="dashboard.admin.welcomeMessage">
                <fmt:param><strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong></fmt:param>
            </fmt:message>
        </p>

        <%-- As mensagens de sucesso/erro devem vir como CHAVES do Servlet --%>
        <c:if test="${not empty sessionScope.mensagemSucesso}">
            <div style="padding: 10px; margin-bottom: 15px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; border-radius: 4px;">
                <fmt:message key="${sessionScope.mensagemSucesso}"/>
            </div>
            <c:remove var="mensagemSucesso" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErro}">
            <div style="padding: 10px; margin-bottom: 15px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 4px;">
                <fmt:message key="${sessionScope.mensagemErro}"/>
            </div>
            <c:remove var="mensagemErro" scope="session"/>
        </c:if>

        <div class="dashboard-widgets">
            <section class="widget">
                <h3><fmt:message key="dashboard.admin.widget.mainManagement.title"/></h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/estrategias?action=listar"><fmt:message key="dashboard.admin.widget.mainManagement.item1"/></a> (R5)</li>
                    <li><a href="${pageContext.request.contextPath}/admin/gerenciarProjetos"><fmt:message key="dashboard.admin.widget.mainManagement.item2"/></a> (R3)</li>
                    <li><a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios"><fmt:message key="dashboard.admin.widget.mainManagement.item3"/></a> (R1, R2)</li>
                </ul>
            </section>

            <section class="widget">
                <h3><fmt:message key="dashboard.admin.widget.testSessions.title"/></h3>
                <ul>
                    <li><a href="#"><fmt:message key="dashboard.admin.widget.testSessions.item1"/></a> (R9)</li>
                    <li><a href="#"><fmt:message key="dashboard.admin.widget.testSessions.item2"/></a></li>
                </ul>
            </section>

            <section class="widget">
                <h3><fmt:message key="dashboard.admin.widget.system.title"/></h3>
                <ul>
                    <li><a href="#"><fmt:message key="dashboard.admin.widget.system.item1"/></a></li>
                    <li><a href="#"><fmt:message key="dashboard.admin.widget.system.item2"/></a></li>
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