<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado || (sessionScope.usuarioLogado.tipoPerfil != 'TESTADOR' && sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR')}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso restrito."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Meus Projetos Atribuídos</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 900px; margin: auto; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        th a { text-decoration: none; color: inherit; }
        th a:hover { text-decoration: underline; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .navigation-links { margin-top: 20px; }
        .action-btn { margin-right: 5px; padding: 5px 10px; font-size:0.9em; text-decoration: none; border-radius: 3px; color: white; display:inline-block; text-align:center; border: none; cursor:pointer; }
        .btn-ver-sessoes { background-color: #17a2b8; }
    </style>
</head>
<body>
<div class="container">
    <h1>Meus Projetos Atribuídos</h1>

    <c:if test="${not empty mensagemErroMeusProjetos}">
        <p class="message error"><c:out value="${mensagemErroMeusProjetos}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${not empty listaMeusProjetos}">
            <table>
                <thead>
                <tr>
                    <th>
                        <a href="${pageContext.request.contextPath}/testador/meusProjetos?sort=id&order=${(currentSortField == 'id' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
                            ID ${currentSortField == 'id' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
                        </a>
                    </th>
                    <th>
                        <a href="${pageContext.request.contextPath}/testador/meusProjetos?sort=nome&order=${(currentSortField == 'nome' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
                            Nome do Projeto ${currentSortField == 'nome' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
                        </a>
                    </th>
                    <th>Descrição</th>
                    <th>
                        <a href="${pageContext.request.contextPath}/testador/meusProjetos?sort=data_criacao&order=${(currentSortField == 'data_criacao' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
                            Data de Criação ${currentSortField == 'data_criacao' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
                        </a>
                    </th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="projeto" items="${listaMeusProjetos}">
                    <tr>
                        <td><c:out value="${projeto.id}"/></td>
                        <td><c:out value="${projeto.nome}"/></td>
                        <td><c:out value="${projeto.descricao}"/></td>
                        <td><fmt:formatDate value="${projeto.dataCriacao}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/testador/sessoesPorProjeto?projetoId=${projeto.id}" class="action-btn btn-ver-sessoes">Ver Sessões de Teste</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <c:if test="${empty mensagemErroMeusProjetos}">
                <p class="empty-list">Você não está atribuído a nenhum projeto no momento.</p>
            </c:if>
        </c:otherwise>
    </c:choose>
    <br/>
    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Painel do Testador</a>
    </div>
</div>
</body>
</html>