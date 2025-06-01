<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %> &lt;%&ndash; URI ATUALIZADA &ndash;%&gt;--%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel do Administrador - Game Tester System</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="page-container">
    <aside class="sidebar">
        <h2>Game Tester Sys</h2>
        <nav>
            <ul>
                <%-- Adicione 'active' à classe do link da página atual se quiser destacar --%>
                <li><a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/estrategias?action=listar">Gerenciar Estratégias</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/listar-projetos.jsp">Gerenciar Projetos</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios">Gerenciar Usuários</a></li>
                <li><a href="#">Sessões de Teste (Admin)</a></li> <%-- Placeholder --%>
            </ul>
        </nav>
        <c:if test="${not empty sessionScope.usuarioLogado}">
            <div class="user-info">
                <p><strong>Usuário:</strong><br><c:out value="${sessionScope.usuarioLogado.nome}"/></p>
                <p><strong>Email:</strong><br><c:out value="${sessionScope.usuarioLogado.email}"/></p>
                <p><strong>Perfil:</strong><br><c:out value="${sessionScope.usuarioLogado.tipoPerfil}"/></p>
            </div>
        </c:if>
    </aside>

    <main class="main-content">
        <div class="header">
            <h1>Painel do Administrador</h1>
            <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn">Logout</a>
        </div>

        <p class="welcome-message">
            Bem-vindo(a) de volta, <strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong>!
        </p>

        <c:if test="${not empty sessionScope.mensagemSucesso}">
            <div style="padding: 10px; margin-bottom: 15px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; border-radius: 4px;">
                    ${sessionScope.mensagemSucesso}
            </div>
            <c:remove var="mensagemSucesso" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErro}">
            <div style="padding: 10px; margin-bottom: 15px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 4px;">
                    ${sessionScope.mensagemErro}
            </div>
            <c:remove var="mensagemErro" scope="session"/>
        </c:if>

        <div class="dashboard-widgets">
            <section class="widget">
                <h3>Gerenciamento Principal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/estrategias?action=listar">Gerenciar Estratégias</a> (R5)</li>
                    <li><a href="${pageContext.request.contextPath}/admin/listar-projetos.jsp">Gerenciar Projetos</a> (R3, R4)</li>
                    <li><a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios">Gerenciar Usuários</a> (R1, R2)</li>
                </ul>
            </section>

            <section class="widget">
                <h3>Sessões de Teste</h3>
                <ul>
                    <li><a href="#">Visualizar Todas as Sessões</a> (R9)</li>
                    <li><a href="#">Configurações de Teste</a></li>
                </ul>
            </section>

            <section class="widget">
                <h3>Sistema</h3>
                <ul>
                    <li><a href="#">Logs da Aplicação</a></li>
                    <li><a href="#">Configurações Globais</a></li>
                </ul>
            </section>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; <jsp:useBean id="javaDate" class="java.util.Date" /><c:set var="currentYear"><jsp:getProperty name="javaDate" property="year" /></c:set><c:out value="${currentYear + 1900}"/> Game Tester System. Todos os direitos reservados.</p>
</footer>

</body>
</html>