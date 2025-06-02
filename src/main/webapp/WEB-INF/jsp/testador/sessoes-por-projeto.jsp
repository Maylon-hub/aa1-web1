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
    <title>Sessões do Projeto: <c:out value="${projeto.nome}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao minhas-sessoes.jsp e visualizar-sessoes-admin.jsp */
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 1000px; margin: auto; }
        .projeto-info { background-color: #f8f9fa; padding: 15px; margin-bottom: 20px; border-radius: 5px; border: 1px solid #e3e3e3; }
        .projeto-info h2 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: top; }
        th { background-color: #e9ecef; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .action-btn { margin-right: 5px; margin-bottom: 5px; padding: 5px 10px; font-size:0.9em; text-decoration: none; border-radius: 3px; color: white; display:inline-block; text-align:center; border:none; cursor:pointer; }
        .btn-iniciar { background-color: #28a745; }
        .btn-finalizar { background-color: #dc3545; }
        .btn-registrar-bug { background-color: #ffc107; color: black; }
        .btn-ver-bugs { background-color: #17a2b8; }
        .btn-ver-estrategia { background-color: #6c757d; }
        .navigation-links { margin-top: 20px; }
        .actions-column { min-width: 200px; }
    </style>
</head>
<body>
<div class="container">
    <c:if test="${not empty projeto}">
        <h1>Sessões de Teste do Projeto: <c:out value="${projeto.nome}"/></h1>
        <div class="projeto-info">
            <p><strong>ID do Projeto:</strong> <c:out value="${projeto.id}"/></p>
            <p><strong>Descrição do Projeto:</strong> <c:out value="${projeto.descricao}"/></p>
        </div>

        <%-- Mensagens da sessão para operações futuras nesta página --%>
        <c:if test="${not empty sessionScope.mensagemSucessoSessaoProjeto}">
            <p class="message success"><c:out value="${sessionScope.mensagemSucessoSessaoProjeto}"/></p>
            <c:remove var="mensagemSucessoSessaoProjeto" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErroSessaoProjeto}">
            <p class="message error"><c:out value="${sessionScope.mensagemErroSessaoProjeto}"/></p>
            <c:remove var="mensagemErroSessaoProjeto" scope="session"/>
        </c:if>

        <c:if test="${not empty mensagemErroCarregarSessoes}"> <%-- Erro vindo do servlet ao carregar --%>
            <p class="message error"><c:out value="${mensagemErroCarregarSessoes}"/></p>
        </c:if>


        <c:choose>
            <c:when test="${not empty listaSessoesDoProjeto}">
                <h3>Sessões Registradas</h3>
                <table>
                    <thead>
                    <tr>
                        <th>ID Sessão</th>
                        <th>Testador</th>
                        <th>Estratégia</th>
                        <th>Descrição da Sessão</th>
                        <th>Status</th>
                        <th>Criação</th>
                        <th class="actions-column">Ações (para suas sessões)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="sessao" items="${listaSessoesDoProjeto}">
                        <tr>
                            <td><c:out value="${sessao.id}"/></td>
                            <td><c:out value="${sessao.testador.nome}"/></td>
                            <td><c:out value="${sessao.estrategia.nome}"/></td>
                            <td><c:out value="${sessao.descricao}"/></td>
                            <td><c:out value="${sessao.status}"/></td>
                            <td><fmt:formatDate value="${sessao.dataHoraCriacao}" pattern="dd/MM/yy HH:mm"/></td>
                            <td>
                                    <%-- Ações aparecem apenas se a sessão pertencer ao usuário logado --%>
                                <c:if test="${sessao.testadorId == sessionScope.usuarioLogado.id}">
                                    <c:if test="${sessao.status == 'CRIADO'}">
                                        <a href="${pageContext.request.contextPath}/testador/iniciarSessao?id=${sessao.id}&projetoId=${projeto.id}" class="action-btn btn-iniciar">Iniciar</a>
                                    </c:if>
                                    <c:if test="${sessao.status == 'EM_EXECUCAO'}">
                                        <a href="${pageContext.request.contextPath}/testador/registrarBug?sessaoId=${sessao.id}" class="action-btn btn-registrar-bug">Registrar Bug</a>
                                        <a href="${pageContext.request.contextPath}/testador/visualizarBugsSessao?sessaoId=${sessao.id}" class="action-btn btn-ver-bugs">Ver Bugs</a>
                                        <a href="${pageContext.request.contextPath}/testador/finalizarSessao?id=${sessao.id}&projetoId=${projeto.id}" class="action-btn btn-finalizar">Finalizar</a>
                                        <a href="${pageContext.request.contextPath}/estrategias?id=${sessao.estrategiaId}" class="action-btn btn-ver-estrategia" target="_blank">Ver Estratégia</a>
                                    </c:if>
                                    <c:if test="${sessao.status == 'FINALIZADO'}">
                                        <span>Concluída</span>
                                        <a href="${pageContext.request.contextPath}/testador/visualizarBugsSessao?sessaoId=${sessao.id}" class="action-btn btn-ver-bugs">Ver Bugs</a>
                                    </c:if>
                                </c:if>
                                    <%-- Se a sessão não for do usuário logado, nenhuma ação é mostrada (ou poderia mostrar "Visualizar Detalhes") --%>
                                <c:if test="${sessao.testadorId != sessionScope.usuarioLogado.id}">
                                    <span>-</span>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <c:if test="${empty mensagemErroCarregarSessoes}">
                    <p class="empty-list">Nenhuma sessão de teste encontrada para este projeto.</p>
                </c:if>
            </c:otherwise>
        </c:choose>

    </c:if>
    <c:if test="${empty projeto}">
        <p class="message error">Projeto não encontrado ou ID inválido.</p>
    </c:if>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/testador/meusProjetos">Voltar para Meus Projetos</a>
        <a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Painel do Testador</a>
    </div>
</div>
</body>
</html>