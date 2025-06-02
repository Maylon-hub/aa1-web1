<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado || (sessionScope.usuarioLogado.tipoPerfil != 'TESTADOR' && sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR')}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso restrito a testadores e administradores."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cadastrar Nova Sessão de Teste</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos básicos - ajuste conforme seu CSS principal */
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 700px; margin: auto; }
        label { display: block; margin-top: 10px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="number"], textarea, select {
            width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc;
            border-radius: 4px; box-sizing: border-box;
        }
        textarea { resize: vertical; min-height: 80px; }
        button[type="submit"] { padding: 10px 15px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Cadastrar Nova Sessão de Teste</h1>

    <c:if test="${not empty mensagemSucessoSessao}">
        <p class="message success"><c:out value="${mensagemSucessoSessao}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroSessao}">
        <p class="message error"><c:out value="${mensagemErroSessao}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/testador/cadastrarSessao" method="post">
        <div>
            <label for="projetoId">Projeto:</label>
            <select id="projetoId" name="projetoId" required>
                <option value="" ${empty valorProjetoId ? 'selected' : ''} disabled>Selecione um Projeto</option>
                <c:forEach var="projeto" items="${projetos}">
                    <option value="${projeto.id}" ${projeto.id == valorProjetoId ? 'selected' : ''}>
                        <c:out value="${projeto.nome}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label for="estrategiaId">Estratégia de Teste:</label>
            <select id="estrategiaId" name="estrategiaId" required>
                <option value="" ${empty valorEstrategiaId ? 'selected' : ''} disabled>Selecione uma Estratégia</option>
                <c:forEach var="estrategia" items="${estrategias}">
                    <option value="${estrategia.id}" ${estrategia.id == valorEstrategiaId ? 'selected' : ''}>
                        <c:out value="${estrategia.nome}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label for="tempoSessao">Tempo da Sessão (minutos):</label>
            <input type="number" id="tempoSessao" name="tempoSessao" value="<c:out value='${valorTempoSessao}'/>" min="1" required>
        </div>

        <div>
            <label for="descricaoSessao">Descrição/Objetivo da Sessão:</label>
            <textarea id="descricaoSessao" name="descricaoSessao" required><c:out value='${valorDescricaoSessao}'/></textarea>
        </div>

        <button type="submit">Cadastrar Sessão</button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Painel do Testador</a>
    </div>
</div>
</body>
</html>