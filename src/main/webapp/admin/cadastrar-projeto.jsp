<%--
  Created by IntelliJ IDEA.
  User: felip
  Date: 31/05/2025
  Time: 23:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<head>
    <title>Cadastrar Novo Projeto</title>
    <%-- Inclua seus CSS aqui --%>
</head>
<body>
<h2>Cadastrar Novo Projeto</h2>

<c:if test="${not empty mensagemErro}">
    <p style="color:red;">${fn:escapeXml(mensagemErro)}</p>
</c:if>
<c:if test="${not empty requestScope.mensagemSucesso}"> <%-- Se o sucesso for via request --%>
    <p style="color:green;">${fn:escapeXml(requestScope.mensagemSucesso)}</p>
</c:if>
<c:if test="${not empty sessionScope.mensagemSucesso}"> <%-- Se o sucesso for via session (após redirect) --%>
    <p style="color:green;">${fn:escapeXml(sessionScope.mensagemSucesso)}</p>
    <c:remove var="mensagemSucesso" scope="session" /> <%-- Limpa a mensagem da sessão --%>
</c:if>

<form method="POST" action="${pageContext.request.contextPath}/admin/cadastrarProjeto">
    <div>
        <label for="nomeProjeto">Nome do Projeto:</label><br>
        <input type="text" id="nomeProjeto" name="nomeProjeto" value="${fn:escapeXml(param.nomeProjeto)}" required>
    </div>
    <br>
    <div>
        <label for="descricao">Descrição:</label><br>
        <textarea id="descricao" name="descricao" rows="4" cols="50">${fn:escapeXml(param.descricao)}</textarea>
    </div>
    <br>
    <div>
        <label for="membrosIds">Membros Permitidos:</label><br>
        <select id="membrosIds" name="membrosIds" multiple size="5">
            <c:forEach var="usuario" items="${listaUsuariosDisponiveis}">
                <option value="${usuario.id}"
                        <c:forEach var="selectedId" items="${paramValues.membrosIds}">
                            <c:if test="${usuario.id == selectedId}">selected</c:if>
                        </c:forEach>
                >${fn:escapeXml(usuario.nome)} (${fn:escapeXml(usuario.tipoPerfil)})</option>
            </c:forEach>
        </select>
        <small>Segure Ctrl (ou Cmd) para selecionar múltiplos membros.</small>
    </div>
    <br>
    <input type="submit" value="Cadastrar Projeto">
</form>
<br>
<a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Dashboard</a>
</body>
</html>
