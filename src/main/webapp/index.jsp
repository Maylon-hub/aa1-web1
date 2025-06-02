<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bem-vindo ao Game Tester System</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f7f6; /* Um cinza bem claro */
            color: #333;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            text-align: center;
        }

        .container {
            background-color: #ffffff;
            padding: 40px 50px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            max-width: 600px;
        }

        h1 {
            color: #007bff; /* Azul primário, similar ao que usamos nos botões */
            margin-bottom: 15px;
            font-size: 2.5em;
        }

        p {
            font-size: 1.1em;
            line-height: 1.6;
            margin-bottom: 30px;
            color: #555;
        }

        .btn-login {
            display: inline-block;
            padding: 15px 35px;
            font-size: 1.2em;
            font-weight: bold;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

        .btn-login:hover, .btn-login:focus {
            background-color: #0056b3; /* Azul mais escuro no hover */
            transform: translateY(-2px); /* Efeito sutil de elevação */
            outline: none;
        }

        .footer {
            margin-top: 40px;
            font-size: 0.9em;
            color: #777;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Game Tester System</h1>
    <p>
        Bem-vindo à sua plataforma dedicada para gerenciamento e execução de testes exploratórios em jogos digitais.
        Organize seus projetos, defina estratégias e acompanhe suas sessões de teste de forma eficiente.
    </p>
    <a href="${pageContext.request.contextPath}/login.jsp" class="btn-login">Acessar o Sistema</a>
    <a href="${pageContext.request.contextPath}/estrategias-publicas" class="btn-login">Ver Estratégias Públicas</a>
</div>

<div class="footer">
    <p>&copy; ${currentYear} Game Tester System. Todos os direitos reservados.</p>
    <%-- Para obter o ano corrente dinamicamente (opcional) --%>
    <jsp:useBean id="date" class="java.util.Date" />
    <c:set var="currentYear"><jsp:getProperty name="date" property="year" /></c:set>
    <c:set var="currentYear" value="${currentYear + 1900}"/>
</div>
</body>
</html>