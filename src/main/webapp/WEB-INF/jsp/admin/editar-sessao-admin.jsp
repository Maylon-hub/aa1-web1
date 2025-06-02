<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Para formatação de datas --%>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso restrito."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Editar Sessão de Teste - Admin</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 750px; margin: auto; }
        label { display: block; margin-top: 10px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="number"], input[type="datetime-local"], textarea, select {
            width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc;
            border-radius: 4px; box-sizing: border-box;
        }
        textarea { resize: vertical; min-height: 80px; }
        .info-field { background-color: #e9ecef; padding: 8px; margin-bottom: 10px; border: 1px solid #ced4da; border-radius: 4px; }
        button[type="submit"] { padding: 10px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Editar Sessão de Teste (Admin)</h1>

    <c:if test="${not empty mensagemErroFormSessaoAdmin}">
        <p class="message error"><c:out value="${mensagemErroFormSessaoAdmin}"/></p>
    </c:if>

    <c:if test="${empty sessaoParaEditar}">
        <p class="message error">Sessão de teste não encontrada ou ID inválido para edição.</p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/sessoes">Voltar para Visualizar Sessões</a>
        </div>
    </c:if>

    <c:if test="${not empty sessaoParaEditar}">
        <form action="${pageContext.request.contextPath}/admin/editarSessao" method="post">
            <input type="hidden" name="sessaoId" value="<c:out value='${sessaoParaEditar.id}'/>">

            <div>
                <label>ID da Sessão:</label>
                <div class="info-field"><c:out value='${sessaoParaEditar.id}'/></div>
            </div>

            <div>
                <label for="projetoId">Projeto:</label>
                <select id="projetoId" name="projetoId" required>
                    <c:forEach var="projeto" items="${todosProjetos}">
                        <option value="${projeto.id}" ${projeto.id == sessaoParaEditar.projetoId ? 'selected' : ''}>
                            <c:out value="${projeto.nome}"/> (ID: ${projeto.id})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="testadorId">Testador:</label>
                <select id="testadorId" name="testadorId" required>
                    <c:forEach var="testador" items="${todosTestadores}">
                        <option value="${testador.id}" ${testador.id == sessaoParaEditar.testadorId ? 'selected' : ''}>
                            <c:out value="${testador.nome}"/> (ID: ${testador.id} - <c:out value="${testador.tipoPerfil}"/>)
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="estrategiaId">Estratégia:</label>
                <select id="estrategiaId" name="estrategiaId" required>
                    <c:forEach var="estrategia" items="${todasEstrategias}">
                        <option value="${estrategia.id}" ${estrategia.id == sessaoParaEditar.estrategiaId ? 'selected' : ''}>
                            <c:out value="${estrategia.nome}"/> (ID: ${estrategia.id})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="tempoSessaoMinutos">Tempo da Sessão (minutos):</label>
                <input type="number" id="tempoSessaoMinutos" name="tempoSessaoMinutos" value="<c:out value='${sessaoParaEditar.tempoSessaoMinutos}'/>" min="1" required>
            </div>

            <div>
                <label for="descricaoSessao">Descrição:</label>
                <textarea id="descricaoSessao" name="descricaoSessao" required><c:out value='${sessaoParaEditar.descricao}'/></textarea>
            </div>

            <div>
                <label for="statusSessao">Status:</label>
                <select id="statusSessao" name="statusSessao" required>
                    <option value="CRIADO" ${sessaoParaEditar.status == 'CRIADO' ? 'selected' : ''}>Criado</option>
                    <option value="EM_EXECUCAO" ${sessaoParaEditar.status == 'EM_EXECUCAO' ? 'selected' : ''}>Em Execução</option>
                    <option value="FINALIZADO" ${sessaoParaEditar.status == 'FINALIZADO' ? 'selected' : ''}>Finalizado</option>
                </select>
            </div>

            <div>
                <label>Data de Criação:</label>
                    <%-- Exibindo a data de criação formatada --%>
                <div class="info-field"><fmt:formatDate value="${sessaoParaEditar.dataHoraCriacao}" pattern="dd/MM/yyyy HH:mm:ss"/></div>
            </div>

                <%-- Correção para dataHoraInicio --%>
            <c:set var="formattedInicio" value=""/>
            <c:if test="${not empty sessaoParaEditar.dataHoraInicio}">
                <fmt:formatDate value="${sessaoParaEditar.dataHoraInicio}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedInicio"/>
            </c:if>
            <div>
                <label for="dataHoraInicio">Data/Hora Início (opcional):</label>
                <input type="datetime-local" id="dataHoraInicio" name="dataHoraInicio" value="${formattedInicio}">
            </div>

                <%-- Correção para dataHoraFim --%>
            <c:set var="formattedFim" value=""/>
            <c:if test="${not empty sessaoParaEditar.dataHoraFim}">
                <fmt:formatDate value="${sessaoParaEditar.dataHoraFim}" pattern="yyyy-MM-dd'T'HH:mm" var="formattedFim"/>
            </c:if>
            <div>
                <label for="dataHoraFim">Data/Hora Fim (opcional):</label>
                <input type="datetime-local" id="dataHoraFim" name="dataHoraFim" value="${formattedFim}">
            </div>

            <button type="submit">Salvar Alterações na Sessão</button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/sessoes">Cancelar e Voltar</a>
        </div>
    </c:if> <%-- Fecha o c:if test="${not empty sessaoParaEditar}" --%>
</div>
</body>
</html>