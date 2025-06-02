<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gerenciar Estratégias - Painel do Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css"> <%-- Se você tiver um CSS principal --%>
    <style>
        /* Estilos básicos para a tabela e botões - ajuste conforme seu CSS principal */
        body { font-family: Arial, sans-serif; } /* Adicionado para consistência */
        .container { padding: 20px; }
        .action-btn { margin-right: 5px; padding: 5px 10px; text-decoration: none; border-radius: 3px; }
        .btn-cadastrar { background-color: #28a745; color: white; display: inline-block; margin-bottom: 15px; }
        .btn-editar { background-color: #ffc107; color: black; }
        .btn-excluir { background-color: #dc3545; color: white; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; } /* Adicionado estilo para sucesso */
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .navigation-links { margin-top: 20px; } /* Adicionado para o link de voltar */
    </style>
</head>
<body>
<div class="container">
    <h1>Gerenciar Estratégias de Teste</h1>

    <%-- Bloco para exibir mensagens da SESSÃO (definidas pelo ExcluirEstrategiaServlet) --%>
    <c:if test="${not empty sessionScope.mensagemSucessoGerenciamento}">
        <p class="message success"><c:out value="${sessionScope.mensagemSucessoGerenciamento}"/></p>
        <c:remove var="mensagemSucessoGerenciamento" scope="session"/> <%-- Remove a mensagem da sessão após exibir --%>
    </c:if>
    <c:if test="${not empty sessionScope.mensagemErroGerenciamento}">
        <p class="message error"><c:out value="${sessionScope.mensagemErroGerenciamento}"/></p>
        <c:remove var="mensagemErroGerenciamento" scope="session"/> <%-- Remove a mensagem da sessão após exibir --%>
    </c:if>

    <%-- Mensagem de erro vinda da REQUISIÇÃO (definida pelo GerenciarEstrategiasServlet ao carregar a lista) --%>
    <c:if test="${not empty mensagemErro}">
        <p class="message error"><c:out value="${mensagemErro}"/></p>
    </c:if>

    <a href="${pageContext.request.contextPath}/admin/cadastrarEstrategia" class="action-btn btn-cadastrar">Cadastrar Nova Estratégia</a>

    <c:choose>
        <c:when test="${not empty listaEstrategias}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Descrição</th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="estrategia" items="${listaEstrategias}">
                    <tr>
                        <td><c:out value="${estrategia.id}"/></td>
                        <td><c:out value="${estrategia.nome}"/></td>
                        <td><c:out value="${estrategia.descricao}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/editarEstrategia?id=${estrategia.id}" class="action-btn btn-editar">Editar</a>
                            <a href="${pageContext.request.contextPath}/admin/excluirEstrategia?id=${estrategia.id}" class="action-btn btn-excluir" onclick="return confirm('Tem certeza que deseja excluir esta estratégia?');">Excluir</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="empty-list">Nenhuma estratégia cadastrada no sistema.</p>
        </c:otherwise>
    </c:choose>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
    </div>

</div>
</body>
</html>