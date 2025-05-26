<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Painel do Administrador</title>
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
    <h1>Painel do Administrador</h1>
    <c:if test="${not empty sessionScope.usuarioLogado}">
        <div class="user-info">
            Logado como: ${sessionScope.usuarioLogado.nome} (${sessionScope.usuarioLogado.email}) - ${sessionScope.usuarioLogado.tipoPerfil}
        </div>
    </c:if>
    <a href="${pageContext.request.contextPath}/login?action=logout" class="logout-btn">Logout</a>
</div>

<p>Bem-vindo(a) ao painel de administração, ${sessionScope.usuarioLogado.nome}!</p>

<h2>Funcionalidades do Administrador:</h2>
<ul>
    <li><a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios">Gerenciar Usuários</a> (R1, R2)</li>
    <li><a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Gerenciar Projetos</a> (R3)</li>
    <li>
        Gerenciar Estratégias (R5):
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/cadastrarEstrategia">Cadastrar Nova Estratégia</a></li>
            <li><a href="${pageContext.request.contextPath}/estrategias">Listar Todas as Estratégias</a></li>
            <%-- Futuramente: Editar/Excluir Estratégias --%>
        </ul>
    </li>
    <li>Visualizar/Editar/Excluir Sessões de Teste (R9)</li>
</ul>

</body>
</html>