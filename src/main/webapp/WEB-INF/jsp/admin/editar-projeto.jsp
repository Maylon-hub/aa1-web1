<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="mensagens"/>

<%--
  Proteção: Redireciona para login se não for admin.
  MUDANÇA CRÍTICA: Passamos uma CHAVE de erro ('erroChave'), não o texto completo.
--%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erroChave" value="auth.error.unauthorizedAdmin"/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="project.edit.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao cadastrar-projeto.jsp, ajuste conforme necessidade */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 80px; }
        .info-field { background-color: #e9ecef; padding: 10px; margin-bottom: 15px; border: 1px solid #ced4da; border-radius: 4px; }
        button[type="submit"] { padding: 12px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button[type="submit"]:hover { background-color: #0056b3; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
    </style>
</head>
<body>
<div class="container">
    <h1><fmt:message key="project.edit.header"/></h1>

    <%-- O Servlet deve passar a CHAVE do erro de validação --%>
    <c:if test="${not empty mensagemErroFormProjeto}">
        <p class="message error"><fmt:message key="${mensagemErroFormProjeto}"/></p>
    </c:if>

    <c:if test="${empty projeto}">
        <p class="message error"><fmt:message key="project.edit.error.notFound"/></p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos"><fmt:message key="project.edit.link.backToManage"/></a>
        </div>
    </c:if>

    <c:if test="${not empty projeto}">
        <form action="${pageContext.request.contextPath}/admin/editarProjeto" method="post">
            <input type="hidden" name="idProjeto" value="<c:out value='${projeto.id}'/>">

            <div>
                <label><fmt:message key="project.edit.label.id"/></label>
                <div class="info-field"><c:out value='${projeto.id}'/></div>
            </div>
            <div>
                <label for="nomeProjeto"><fmt:message key="project.edit.label.name"/></label>
                <input type="text" id="nomeProjeto" name="nomeProjeto" value="<c:out value='${projeto.nome}'/>" required>
            </div>
            <div>
                <label for="descricaoProjeto"><fmt:message key="project.edit.label.description"/></label>
                <textarea id="descricaoProjeto" name="descricaoProjeto"><c:out value='${projeto.descricao}'/></textarea>
            </div>
            <div>
                <label><fmt:message key="project.edit.label.creationDate"/></label>
                <div class="info-field">
                    <%-- A tag <fmt:formatDate> já lida com a localização se usarmos dateStyle/timeStyle --%>
                    <fmt:formatDate value="${projeto.dataCriacao}" dateStyle="medium" timeStyle="medium"/>
                </div>
            </div>

            <button type="submit"><fmt:message key="project.edit.button.save"/></button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos"><fmt:message key="project.edit.link.cancelAndBack"/></a>
        </div>
    </c:if>
</div>
</body>
</html>
