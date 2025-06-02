<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="en" />
<%-- 2. Definir o Resource Bundle que será usado na página --%>
<fmt:setBundle basename="message" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%-- 3. Usar a tag fmt:message para o título --%>
    <title><fmt:message key="login.title"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; min-height: 80vh; background-color: #f4f4f4; margin: 0; }
        .login-container { background-color: #fff; padding: 20px 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); width: 300px; }
        h2 { text-align: center; color: #333; }
        label { display: block; margin-bottom: 5px; color: #555; }
        input[type="email"], input[type="password"] { width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        button[type="submit"] { width: 100%; padding: 10px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button[type="submit"]:hover { background-color: #0056b3; }
        .error-message { color: red; text-align: center; margin-bottom: 15px; }
        .logout-message { color: green; text-align: center; margin-bottom: 15px; }
    </style>
</head>
<body>
<div class="login-container">
    <h2><fmt:message key="login.header"/></h2>

    <c:if test="${not empty erroLogin}">
        <p class="error-message"><fmt:message key="${erroLogin}"/></p>
    </c:if>

    <c:if test="${param.logout == 'true'}">
        <p class="logout-message"><fmt:message key="login.message.logout.success"/></p>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/login">
        <div>
            <label for="email"><fmt:message key="login.label.email"/></label>
            <input type="email" id="email" name="email" required>
        </div>
        <div>
            <label for="senha"><fmt:message key="login.label.password"/></label>
            <input type="password" id="senha" name="senha" required>
        </div>
        <div>
            <button type="submit"><fmt:message key="login.button.submit"/></button>
        </div>
    </form>
</div>
</body>
</html>