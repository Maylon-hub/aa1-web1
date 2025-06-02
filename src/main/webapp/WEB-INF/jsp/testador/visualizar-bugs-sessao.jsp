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
    <title>Bugs da Sessão de Teste - ID: <c:out value="${sessaoAtual.id}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 900px; margin: auto; }
        .session-details { background-color: #f8f9fa; padding: 15px; margin-bottom: 20px; border-radius: 5px; border: 1px solid #e3e3e3; }
        .session-details h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #e9ecef; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .navigation-links { margin-top: 20px; }
        .screenshot-link {word-break: break-all;}
    </style>
</head>
<body>
<div class="container">
    <c:if test="${not empty sessaoAtual}">
        <h1>Bugs Registrados para a Sessão de Teste ID: <c:out value="${sessaoAtual.id}"/></h1>
        <div class="session-details">
            <h2>Detalhes da Sessão</h2>
            <p><strong>Projeto:</strong> <c:out value="${sessaoAtual.projeto.nome}"/></p>
            <p><strong>Estratégia:</strong> <c:out value="${sessaoAtual.estrategia.nome}"/></p>
            <p><strong>Descrição da Sessão:</strong> <c:out value="${sessaoAtual.descricao}"/></p>
            <p><strong>Status:</strong> <c:out value="${sessaoAtual.status}"/></p>
        </div>

        <c:if test="${not empty mensagemErroBugs}">
            <p class="message error"><c:out value="${mensagemErroBugs}"/></p>
        </c:if>

        <c:choose>
            <c:when test="${not empty listaBugsDaSessao}">
                <h3>Lista de Bugs</h3>
                <table>
                    <thead>
                    <tr>
                        <th>ID Bug</th>
                        <th>Descrição</th>
                        <th>Severidade</th>
                        <th>Data Registro</th>
                        <th>Screenshot URL</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="bug" items="${listaBugsDaSessao}">
                        <tr>
                            <td><c:out value="${bug.id}"/></td>
                            <td><c:out value="${bug.descricao}"/></td>
                            <td><c:out value="${bug.severidade}"/></td>
                            <td><fmt:formatDate value="${bug.dataRegistro}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                            <td class="screenshot-link">
                                <c:if test="${not empty bug.screenshotUrl}">
                                    <a href="<c:out value='${bug.screenshotUrl}'/>" target="_blank"><c:out value="${bug.screenshotUrl}"/></a>
                                </c:if>
                                <c:if test="${empty bug.screenshotUrl}">
                                    N/A
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p class="empty-list">Nenhum bug registrado para esta sessão ainda.</p>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${empty sessaoAtual}">
        <p class="message error">Sessão de teste não encontrada ou inválida.</p>
    </c:if>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/testador/minhasSessoes">Voltar para Minhas Sessões</a>
    </div>
</div>
</body>
</html>