<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel do Testador - Game Tester System</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="page-container">
    <aside class="sidebar">
        <h2>Game Tester Sys</h2>
        <nav>
            <ul>
                <%-- Adicione 'active' à classe do link da página atual se quiser destacar --%>
                <li><a href="${pageContext.request.contextPath}/testador/dashboard.jsp" class="active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/testador/cadastrarSessao">Nova Sessão de Teste</a></li>
                <li><a href="${pageContext.request.contextPath}/testador/minhasSessoes">Minhas Sessões</a></li>
                <li><a href="${pageContext.request.contextPath}/testador/meusProjetos">Meus Projetos</a></li>
                <li><a href="${pageContext.request.contextPath}/estrategias">Estratégias</a></li>
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
            <h1>Painel do Testador</h1>
            <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn">Logout</a>
        </div>

        <p class="welcome-message">
            Bem-vindo(a) ao seu painel de testes, <strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong>!
        </p>

        <c:if test="${not empty sessionScope.mensagemSucesso}">
            <div class="mensagem mensagem-sucesso">
                    ${sessionScope.mensagemSucesso}
            </div>
            <c:remove var="mensagemSucesso" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErro}">
            <div class="mensagem mensagem-erro">
                    ${sessionScope.mensagemErro}
            </div>
            <c:remove var="mensagemErro" scope="session"/>
        </c:if>

        <div class="dashboard-widgets">
            <section class="widget">
                <h3>Minhas Atividades</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/testador/cadastrarSessao">Nova Sessão de Teste</a> (R7)</li>
                    <li><a href="${pageContext.request.contextPath}/testador/minhasSessoes">Gerenciar Minhas Sessões de Teste</a> (R8, R9)</li>
                </ul>
            </section>

            <section class="widget">
                <h3>Recursos</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/testador/meusProjetos">Visualizar Projetos Atribuídos</a> (R4)</li>
                    <li><a href="${pageContext.request.contextPath}/estrategias">Listar Estratégias</a> (R6)</li>

                </ul>
            </section>

            <section class="widget">
                <h3>Minha Conta</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/perfil/editar">Editar Meu Cadastro</a></li>
                    <li><a href="${pageContext.request.contextPath}/perfil/alterarSenha">Alterar Senha</a></li>
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