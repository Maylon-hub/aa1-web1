<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- Proteção: Redireciona para login se não for testador ou admin logado --%>
<c:if test="${empty sessionScope.usuarioLogado || (sessionScope.usuarioLogado.tipoPerfil != 'TESTADOR' && sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR')}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso restrito a testadores ou administradores."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Minhas Sessões de Teste</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 1000px; margin: auto; } /* Aumentado max-width para mais colunas */
        table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: top; }
        th { background-color: #f2f2f2; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .action-btn {
            margin-right: 5px; margin-bottom: 5px;
            padding: 5px 10px; font-size:0.9em;
            text-decoration: none; border-radius: 3px; color: white;
            display:inline-block; text-align:center; border: none; cursor:pointer;
        }
        .btn-cadastrar-nova { background-color: #007bff; margin-bottom:15px; padding: 8px 15px; } /* Azul para cadastrar nova */
        .btn-iniciar { background-color: #28a745; } /* Verde */
        .btn-finalizar { background-color: #dc3545; } /* Vermelho */
        .btn-registrar-bug { background-color: #ffc107; color: black; } /* Amarelo */
        .btn-ver-bugs { background-color: #17a2b8; } /* Azul Info */
        .btn-ver-estrategia { background-color: #6c757d; } /* Cinza */
        .navigation-links { margin-top: 20px; }
        .actions-column { min-width: 200px; } /* Para dar mais espaço aos botões de ação */
    </style>
</head>
<body>
<div class="container">
    <h1>Minhas Sessões de Teste</h1>

    <%-- Mensagens de feedback da sessão (ex: após iniciar/finalizar/registrar bug) --%>
    <c:if test="${not empty sessionScope.mensagemSucessoSessaoOperacao}">
        <p class="message success"><c:out value="${sessionScope.mensagemSucessoSessaoOperacao}"/></p>
        <c:remove var="mensagemSucessoSessaoOperacao" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.mensagemErroSessaoOperacao}">
        <p class="message error"><c:out value="${sessionScope.mensagemErroSessaoOperacao}"/></p>
        <c:remove var="mensagemErroSessaoOperacao" scope="session"/>
    </c:if>

    <%-- Mensagem de erro ao carregar a lista de sessões --%>
    <c:if test="${not empty mensagemErroMinhasSessoes}">
        <p class="message error"><c:out value="${mensagemErroMinhasSessoes}"/></p>
    </c:if>

    <a href="${pageContext.request.contextPath}/testador/cadastrarSessao" class="action-btn btn-cadastrar-nova">+ Cadastrar Nova Sessão</a>

    <c:choose>
        <c:when test="${not empty listaMinhasSessoes}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Projeto</th>
                    <th>Estratégia</th>
                    <th>Descrição</th>
                    <th>Tempo (min)</th>
                    <th>Status</th>
                    <th>Criação</th>
                    <th class="actions-column">Ações</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sessao" items="${listaMinhasSessoes}">
                    <tr>
                        <td><c:out value="${sessao.id}"/></td>
                        <td><c:out value="${sessao.projeto.nome}"/></td>
                        <td><c:out value="${sessao.estrategia.nome}"/></td>
                        <td><c:out value="${sessao.descricao}"/></td>
                        <td><c:out value="${sessao.tempoSessaoMinutos}"/></td>
                        <td><c:out value="${sessao.status}"/></td>
                        <td><fmt:formatDate value="${sessao.dataHoraCriacao}" pattern="dd/MM/yy HH:mm"/></td>
                        <td>
                            <c:if test="${sessao.status == 'CRIADO'}">
                                <a href="${pageContext.request.contextPath}/testador/iniciarSessao?id=${sessao.id}" class="action-btn btn-iniciar">Iniciar</a>
                            </c:if>
                            <c:if test="${sessao.status == 'EM_EXECUCAO'}">
                                <a href="${pageContext.request.contextPath}/testador/registrarBug?sessaoId=${sessao.id}" class="action-btn btn-registrar-bug">Registrar Bug</a>
                                <a href="${pageContext.request.contextPath}/testador/visualizarBugsSessao?sessaoId=${sessao.id}" class="action-btn btn-ver-bugs">Ver Bugs</a>
                                <a href="${pageContext.request.contextPath}/testador/finalizarSessao?id=${sessao.id}" class="action-btn btn-finalizar">Finalizar</a>
                                <a href="${pageContext.request.contextPath}/estrategias?id=${sessao.estrategiaId}" class="action-btn btn-ver-estrategia" target="_blank" title="Abre detalhes da estratégia em nova aba">Ver Estratégia</a>
                            </c:if>
                            <c:if test="${sessao.status == 'FINALIZADO'}">
                                <span>Concluída</span>
                                <a href="${pageContext.request.contextPath}/testador/visualizarBugsSessao?sessaoId=${sessao.id}" class="action-btn btn-ver-bugs">Ver Bugs</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <c:if test="${empty mensagemErroMinhasSessoes}">
                <p class="empty-list">Você ainda não cadastrou nenhuma sessão de teste.</p>
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