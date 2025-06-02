<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Para formatação de datas --%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Visualizar Todas as Sessões de Teste - Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos básicos - ajuste conforme seu CSS principal */
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .action-btn { margin-right: 5px; padding: 3px 8px; font-size:0.9em; text-decoration: none; border-radius: 3px; }
        .btn-detalhes { background-color: #17a2b8; color: white; }
        .btn-editar { background-color: #ffc107; color: black; }
        .btn-excluir { background-color: #dc3545; color: white; }
        .navigation-links { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Todas as Sessões de Teste Registradas</h1>

    <%-- Para mensagens de feedback de ações futuras (ex: exclusão de sessão) --%>
    <c:if test="${not empty sessionScope.mensagemSucessoSessoesAdmin}">
        <p class="message success"><c:out value="${sessionScope.mensagemSucessoSessoesAdmin}"/></p>
        <c:remove var="mensagemSucessoSessoesAdmin" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.mensagemErroSessoesAdmin}">
        <p class="message error"><c:out value="${sessionScope.mensagemErroSessoesAdmin}"/></p>
        <c:remove var="mensagemErroSessoesAdmin" scope="session"/>
    </c:if>

    <c:if test="${not empty mensagemErroSessoes}">
        <p class="message error"><c:out value="${mensagemErroSessoes}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${not empty listaSessoes}">
            <table>
                <thead>
                <tr>
                    <th>ID Sessão</th>
                    <th>Projeto</th>
                    <th>Testador</th>
                    <th>Estratégia</th>
                    <th>Status</th>
                    <th>Tempo (min)</th>
                    <th>Criação</th>
                    <th>Início</th>
                    <th>Fim</th>
                    <th>Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sessao" items="${listaSessoes}">
                    <tr>
                        <td><c:out value="${sessao.id}"/></td>
                        <td><c:out value="${sessao.projeto.nome}"/> (ID: <c:out value="${sessao.projetoId}"/>)</td>
                        <td><c:out value="${sessao.testador.nome}"/> (ID: <c:out value="${sessao.testadorId}"/>)</td>
                        <td><c:out value="${sessao.estrategia.nome}"/> (ID: <c:out value="${sessao.estrategiaId}"/>)</td>
                        <td><c:out value="${sessao.status}"/></td>
                        <td><c:out value="${sessao.tempoSessaoMinutos}"/></td>
                        <td><fmt:formatDate value="${sessao.dataHoraCriacao}" pattern="dd/MM/yy HH:mm"/></td>
                        <td><fmt:formatDate value="${sessao.dataHoraInicio}" pattern="dd/MM/yy HH:mm"/></td>
                        <td><fmt:formatDate value="${sessao.dataHoraFim}" pattern="dd/MM/yy HH:mm"/></td>
                        <td>
                                <%-- R24: Admin pode editar/excluir sessões --%>
                            <a href="${pageContext.request.contextPath}/admin/editarSessao?id=${sessao.id}" class="action-btn btn-editar">Editar</a>
                            <a href="${pageContext.request.contextPath}/admin/excluirSessao?id=${sessao.id}" class="action-btn btn-excluir" onclick="return confirm('Tem certeza que deseja excluir esta sessão de teste?');">Excluir</a>
                                <%-- Poderia haver um link para ver detalhes/bugs da sessão --%>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <c:if test="${empty mensagemErroSessoes}">
                <p class="empty-list">Nenhuma sessão de teste encontrada no sistema.</p>
            </c:if>
        </c:otherwise>
    </c:choose>
    <br/>
    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
    </div>
</div>
</body>
</html>