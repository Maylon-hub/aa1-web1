<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nova Sessão de Teste - Testador</title>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<%--<jsp:include page="/testador/testadorHeader.jsp" /> &lt;%&ndash; Cabeçalho comum para testador (opcional) &ndash;%&gt;--%>

<div class="form-container">
    <h1>Cadastrar Nova Sessão de Teste</h1>

    <form method="POST" action="${pageContext.request.contextPath}/testador/sessoes">
        <input type="hidden" name="action" value="salvar"/>

        <div class="form-group">
            <label for="projetoId">Projeto:</label>
            <select id="projetoId" name="projetoId" required>
                <option value="">Selecione um Projeto</option>
                <c:forEach var="projeto" items="${listaProjetos}">
                    <option value="${projeto.id}">${projeto.nome}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="estrategiaId">Estratégia:</label>
            <select id="estrategiaId" name="estrategiaId" required>
                <option value="">Selecione uma Estratégia</option>
                <c:forEach var="estrategia" items="${listaEstrategias}">
                    <option value="${estrategia.id}">${estrategia.nome}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="tempoSessaoMinutos">Tempo da Sessão (minutos):</label>
            <input type="number" id="tempoSessaoMinutos" name="tempoSessaoMinutos" value="<c:out value='${sessaoTeste.tempoSessaoMinutos > 0 ? sessaoTeste.tempoSessaoMinutos : 60 }'/>" required min="1">
        </div>

        <div class="form-group">
            <label for="descricao">Descrição da Sessão (Objetivos, Foco):</label>
            <textarea id="descricao" name="descricao" rows="4" required><c:out value='${sessaoTeste.descricao}'/></textarea>
        </div>

        <div>
            <button type="submit" class="btn-salvar">Salvar Sessão</button>
            <a href="${pageContext.request.contextPath}/testador/dashboard.jsp" class="btn-cancelar">Cancelar</a>
        </div>
    </form>
</div>
<p style="text-align:center; margin-top:20px;"><a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Dashboard</a></p>
</body>
</html>