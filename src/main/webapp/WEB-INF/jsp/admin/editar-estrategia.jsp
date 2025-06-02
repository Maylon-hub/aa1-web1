<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="pt" />
<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="message"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="strategy.edit.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos básicos para formulário (coloque em um CSS externo depois) */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 100px; }
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
    <h1><fmt:message key="strategy.edit.header"/></h1>

    <%-- O Servlet deve passar a CHAVE do erro de validação --%>
    <c:if test="${not empty mensagemErroForm}">
        <p class="message error"><fmt:message key="${mensagemErroForm}"/></p>
    </c:if>

    <c:if test="${empty estrategia}">
        <p class="message error"><fmt:message key="strategy.edit.error.notFound"/></p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarEstrategias"><fmt:message key="strategy.edit.link.backToManage"/></a>
        </div>
    </c:if>

    <c:if test="${not empty estrategia}">
        <form action="${pageContext.request.contextPath}/admin/editarEstrategia" method="post">
            <input type="hidden" name="id" value="<c:out value='${estrategia.id}'/>">

            <div>
                <label for="nome"><fmt:message key="strategy.edit.label.name"/></label>
                <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}'/>" required>
            </div>
            <div>
                <label for="descricao"><fmt:message key="strategy.edit.label.description"/></label>
                <textarea id="descricao" name="descricao" required><c:out value='${estrategia.descricao}'/></textarea>
            </div>
            <div>
                <label for="exemplos"><fmt:message key="strategy.edit.label.examples"/></label>
                <textarea id="exemplos" name="exemplos"><c:out value='${estrategia.exemplos}'/></textarea>
            </div>
            <div>
                <label for="dicas"><fmt:message key="strategy.edit.label.tips"/></label>
                <textarea id="dicas" name="dicas"><c:out value='${estrategia.dicas}'/></textarea>
            </div>

            <button type="submit"><fmt:message key="strategy.edit.button.save"/></button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarEstrategias"><fmt:message key="strategy.edit.link.cancelAndBack"/></a>
        </div>
    </c:if>
</div>
</body>
</html>