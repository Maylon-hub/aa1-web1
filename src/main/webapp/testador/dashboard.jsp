<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Painel do Testador</title>
    <style>
        body { font-family: sans-serif; padding: 20px; }
        .header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 10px; border-bottom: 1px solid #ccc; margin-bottom: 20px; }
        .user-info { font-style: italic; }
        .logout-btn { padding: 8px 15px; background-color: #dc3545; color: white; text-decoration: none; border-radius: 4px; }
        .logout-btn:hover { background-color: #c82333; }
    </style>
</head>
<body>
<div class="header">
    <h1>Painel do Testador</h1>
    <c:if test="${not empty sessionScope.usuarioLogado}">
        <div class="user-info">
            Logado como: ${sessionScope.usuarioLogado.nome} (${sessionScope.usuarioLogado.email}) - ${sessionScope.usuarioLogado.tipoPerfil}
        </div>
    </c:if>
    <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn">Logout</a>
</div>

<p>Bem-vindo(a) ao seu painel, ${sessionScope.usuarioLogado.nome}!</p>

<h2>Funcionalidades do Testador:</h2>
<ul>
    <li><a href="${pageContext.request.contextPath}/testador/meusProjetos">Visualizar Projetos Atribuídos</a> (parte do R4)</li>
    <li><a href="${pageContext.request.contextPath}/testador/novaSessao">Iniciar Nova Sessão de Teste</a> (Cadastro de Sessões - R7)</li>
    <li><a href="${pageContext.request.contextPath}/testador/minhasSessoes">Gerenciar Minhas Sessões de Teste</a> (Ciclo de vida da sessão - R8, Listagem - R9)</li>
    <li>Visualizar Estratégias (R6 - já pode estar acessível sem login, mas útil aqui também)</li>
</ul>

</body>
</html>