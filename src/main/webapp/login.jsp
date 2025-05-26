<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- Para usar JSTL, se necessário --%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - Game Tester System</title>
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
    <h2>Login</h2>

    <%-- Exibe mensagem de erro, se houver --%>
    <c:if test="${not empty erroLogin}">
        <p class="error-message">${erroLogin}</p>
    </c:if>

    <%-- Exibe mensagem de logout, se houver --%>
    <c:if test="${param.logout == 'true'}">
        <p class="logout-message">Logout realizado com sucesso!</p>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/login">
        <div>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div>
            <label for="senha">Senha:</label>
            <input type="password" id="senha" name="senha" required>
        </div>
        <div>
            <button type="submit">Entrar</button>
        </div>
    </form>
</div>
</body>
</html>