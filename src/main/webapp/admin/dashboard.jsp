<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %> &lt;%&ndash; URI ATUALIZADA &ndash;%&gt;--%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel do Administrador - Game Tester System</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            background-color: #f4f7f6; /* Fundo cinza claro */
            color: #333;
            display: flex;
            flex-direction: column; /* Para o rodapé ficar no final */
            min-height: 100vh;
        }

        .page-container {
            display: flex;
            flex: 1; /* Faz o container principal ocupar o espaço disponível */
        }

        .sidebar {
            width: 260px;
            background-color: #343a40; /* Cor escura para a sidebar */
            color: #fff;
            padding: 20px;
            box-shadow: 2px 0 5px rgba(0,0,0,0.1);
        }

        .sidebar h2 {
            font-size: 1.4em;
            margin-top: 0;
            padding-bottom: 10px;
            border-bottom: 1px solid #495057;
            text-align: center;
        }

        .sidebar ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .sidebar ul li a {
            display: block;
            padding: 12px 15px;
            color: #adb5bd; /* Cinza claro para links */
            text-decoration: none;
            border-radius: 5px;
            margin-bottom: 8px;
            transition: background-color 0.3s ease, color 0.3s ease;
        }

        .sidebar ul li a:hover, .sidebar ul li a.active {
            background-color: #007bff; /* Azul para hover/ativo */
            color: #fff;
        }

        .sidebar .user-info {
            font-size: 0.9em;
            color: #adb5bd;
            margin-top: 30px;
            padding-top: 15px;
            border-top: 1px solid #495057;
        }
        .sidebar .user-info p { margin: 5px 0; }


        .main-content {
            flex: 1; /* Ocupa o restante do espaço */
            padding: 30px;
            background-color: #ffffff; /* Fundo branco para conteúdo principal */
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-bottom: 15px;
            border-bottom: 2px solid #e9ecef; /* Linha separadora mais suave */
            margin-bottom: 30px;
        }

        .header h1 {
            font-size: 2em;
            color: #007bff; /* Azul */
            margin: 0;
        }

        .logout-btn {
            padding: 10px 20px;
            background-color: #dc3545; /* Vermelho para logout */
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .logout-btn:hover {
            background-color: #c82333; /* Vermelho mais escuro no hover */
        }

        .welcome-message {
            font-size: 1.2em;
            margin-bottom: 30px;
            color: #555;
        }

        .dashboard-widgets {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
        }

        .widget {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #e9ecef;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .widget h3 {
            margin-top: 0;
            color: #007bff;
        }
        .widget ul {
            padding-left: 20px;
            margin-bottom: 0;
        }
        .widget ul li {
            margin-bottom: 8px;
        }
        .widget ul li a {
            color: #0056b3; /* Azul escuro para links dentro dos widgets */
            text-decoration: none;
        }
        .widget ul li a:hover {
            text-decoration: underline;
        }

        .footer {
            text-align: center;
            padding: 20px;
            background-color: #343a40;
            color: #adb5bd;
            font-size: 0.9em;
            margin-top: auto; /* Empurra o rodapé para baixo se o conteúdo for curto */
        }

    </style>
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
                <li><a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Gerenciar Projetos</a></li>
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
                    <li><a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Gerenciar Projetos</a> (R3)</li>
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