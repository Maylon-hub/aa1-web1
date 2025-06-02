<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>


<fmt:setBundle basename="com.gametester.i18n.mensagens" var="langBundle" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="login.title" bundle="${langBundle}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Seus estilos CSS permanecem aqui... */
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 80vh; background-color: #f4f4f4; margin: 0; padding:20px; box-sizing:border-box;}
        .login-container { background-color: #fff; padding: 30px 40px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.15); width:100%; max-width: 360px; text-align: center; }
        h2 { text-align: center; color: #333; margin-top:0; margin-bottom:25px;}
        label { display: block; margin-bottom: 8px; color: #555; text-align: left; font-weight:bold;}
        input[type="email"], input[type="password"] { width: 100%; padding: 12px; margin-bottom: 20px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        button[type="submit"] { width: 100%; padding: 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; transition: background-color 0.2s ease;}
        button[type="submit"]:hover { background-color: #0056b3; }
        .error-message { color: #D8000C; background-color: #FFD2D2; border: 1px solid #D8000C; padding: 10px; border-radius: 4px; text-align: center; margin-bottom: 20px;}
        .logout-message { color: #4F8A10; background-color: #DFF2BF; border: 1px solid #4F8A10; padding: 10px; border-radius: 4px; text-align: center; margin-bottom: 20px;}
        .navigation-links { margin-top: 25px; }
        .back-link { display: inline-block; font-size: 0.95em; color: #007bff; text-decoration: none; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="login-container">
    <h2><fmt:message key="login.header" bundle="${langBundle}"/></h2>

    <%-- Exibe mensagem de erro, se houver (vindo do servlet) --%>
    <%-- Para internacionalizar esta mensagem, o servlet precisaria passar a CHAVE da mensagem, não o texto --%>
    <c:if test="${not empty erroLogin}">
        <%-- Se erroLogin for uma chave (ex: "login.error.loginInvalido"): --%>
        <%-- <p class="error-message"><fmt:message key="${erroLogin}" bundle="${langBundle}"/></p> --%>
        <%-- Por enquanto, se erroLogin for o texto literal: --%>
        <p class="error-message"><c:out value="${erroLogin}"/></p>
    </c:if>

    <c:if test="${param.logout == 'true'}">
        <p class="logout-message"><fmt:message key="login.success.logout" bundle="${langBundle}"/></p>
    </c:if>

    <%-- Mensagem de erro específica do filtro de autorização --%>
    <c:if test="${param.error == 'naoAutenticado'}">
        <p class="error-message"><fmt:message key="login.error.naoAutenticado" bundle="${langBundle}"/></p>
    </c:if>
    <c:if test="${param.error == 'acessoNegadoAdmin'}">
        <p class="error-message"><fmt:message key="login.error.acessoNegado" bundle="${langBundle}"/> (Admin)</p>
    </c:if>
    <c:if test="${param.error == 'acessoNegadoTestador'}">
        <p class="error-message"><fmt:message key="login.error.acessoNegado" bundle="${langBundle}"/> (Testador)</p>
    </c:if>


    <form method="POST" action="${pageContext.request.contextPath}/login">
        <div>
            <label for="email"><fmt:message key="login.label.email" bundle="${langBundle}"/></label>
            <input type="email" id="email" name="email" required>
        </div>
        <div>
            <label for="senha"><fmt:message key="login.label.senha" bundle="${langBundle}"/></label>
            <input type="password" id="senha" name="senha" required>
        </div>
        <div>
            <button type="submit"><fmt:message key="login.button.entrar" bundle="${langBundle}"/></button>
        </div>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/index.jsp" class="back-link"><fmt:message key="login.link.voltar" bundle="${langBundle}"/></a>
    </div>

</div>
</body>
</html>