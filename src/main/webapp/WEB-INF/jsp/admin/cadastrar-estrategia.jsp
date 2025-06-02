<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="mensagens"/>

<%--
  Proteção: Redireciona para login se não for admin.
  MUDANÇA CRÍTICA: Passamos uma CHAVE de erro ('erroChave'), não o texto completo.
  A página de login deverá ser capaz de ler esta chave.
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
    <title><fmt:message key="strategy.register.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos que estavam no seu JSP de cadastro anteriormente, caso o CSS externo não cubra tudo */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 100px; }
        button[type="submit"] { padding: 12px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; transition: background-color 0.2s; }
        button[type="submit"]:hover { background-color: #218838; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
        .navigation-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container">
    <h1><fmt:message key="strategy.register.header"/></h1>

    <%--
      Para as mensagens de sucesso/erro, o Servlet agora nos envia a CHAVE da mensagem.
      Usamos a tag <fmt:message> para exibir o texto correspondente.
    --%>
    <c:if test="${not empty mensagemSucesso}">
        <p class="message success"><fmt:message key="${mensagemSucesso}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <p class="message error"><fmt:message key="${mensagemErro}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/cadastrarEstrategia" method="post">
        <div>
            <label for="nome"><fmt:message key="strategy.register.label.name"/></label>
            <input type="text" id="nome" name="nome" value="<c:out value='${valorNome}'/>" required>
        </div>
        <div>
            <label for="descricao"><fmt:message key="strategy.register.label.description"/></label>
            <textarea id="descricao" name="descricao" required><c:out value='${valorDescricao}'/></textarea>
        </div>
        <div>
            <label for="exemplos"><fmt:message key="strategy.register.label.examples"/></label>
            <textarea id="exemplos" name="exemplos"><c:out value='${valorExemplos}'/></textarea>
        </div>
        <div>
            <label for="dicas"><fmt:message key="strategy.register.label.tips"/></label>
            <textarea id="dicas" name="dicas"><c:out value='${valorDicas}'/></textarea>
        </div>
        <div>
            <label for="imagemPath"><fmt:message key="strategy.register.label.imagePath"/></label>
            <input type="text" id="imagemPath" name="imagemPath" value="<c:out value='${valorImagemPath}'/>">
        </div>
        <button type="submit"><fmt:message key="strategy.register.button.submit"/></button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><fmt:message key="strategy.register.link.backToDashboard"/></a>
        <a href="${pageContext.request.contextPath}/estrategias"><fmt:message key="strategy.register.link.viewList"/></a>
    </div>
</div>
</body>
</html>
