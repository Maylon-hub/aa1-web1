<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

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
    <title>Registrar Novo Bug</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares aos outros formulários, ajuste conforme necessário */
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 700px; margin: auto; }
        label { display: block; margin-top: 10px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="url"], textarea, select {
            width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc;
            border-radius: 4px; box-sizing: border-box;
        }
        textarea { resize: vertical; min-height: 100px; }
        button[type="submit"] { padding: 10px 15px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 20px; }
        .session-info { background-color: #e9ecef; padding: 10px; margin-bottom:15px; border-radius:4px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Registrar Novo Bug</h1>

    <c:if test="${not empty sessaoId && not empty sessaoDescricao}">
        <div class="session-info">
            Registrando bug para a Sessão de Teste ID: <strong><c:out value="${sessaoId}"/></strong><br/>
            <em>(<c:out value="${sessaoDescricao}"/>)</em>
        </div>
    </c:if>


    <c:if test="${not empty mensagemSucessoBug}">
        <p class="message success"><c:out value="${mensagemSucessoBug}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroBug}">
        <p class="message error"><c:out value="${mensagemErroBug}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/testador/registrarBug" method="post">
        <%-- Campo oculto para enviar o ID da sessão --%>
        <input type="hidden" name="sessaoId" value="<c:out value='${sessaoId}'/>">
        <%-- Campo oculto para manter a descrição da sessão ao submeter e repopular se der erro --%>
        <input type="hidden" name="sessaoDescricao" value="<c:out value='${sessaoDescricao}'/>">


        <div>
            <label for="descricaoBug">Descrição do Bug:</label>
            <textarea id="descricaoBug" name="descricaoBug" required><c:out value='${valorDescricaoBug}'/></textarea>
        </div>

        <div>
            <label for="severidadeBug">Severidade:</label>
            <select id="severidadeBug" name="severidadeBug" required>
                <option value="" ${empty valorSeveridadeBug ? 'selected' : ''} disabled>Selecione a Severidade</option>
                <option value="BAIXA" ${valorSeveridadeBug == 'BAIXA' ? 'selected' : ''}>Baixa</option>
                <option value="MEDIA" ${valorSeveridadeBug == 'MEDIA' ? 'selected' : ''}>Média</option>
                <option value="ALTA" ${valorSeveridadeBug == 'ALTA' ? 'selected' : ''}>Alta</option>
                <option value="CRITICA" ${valorSeveridadeBug == 'CRITICA' ? 'selected' : ''}>Crítica</option>
            </select>
        </div>

        <div>
            <label for="screenshotUrlBug">URL da Screenshot (opcional):</label>
            <input type="url" id="screenshotUrlBug" name="screenshotUrlBug" value="<c:out value='${valorScreenshotUrlBug}'/>" placeholder="https://exemplo.com/imagem.png">
        </div>

        <button type="submit">Registrar Bug</button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/testador/minhasSessoes">Voltar para Minhas Sessões</a>
    </div>
</div>
</body>
</html>