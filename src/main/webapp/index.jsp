<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="en" />
<%-- 2. Definir o arquivo de mensagens a ser usado na página --%>
<fmt:setBundle basename="message"/>

<!DOCTYPE html>
<%-- Opcional: Definir o idioma da página dinamicamente --%>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="welcome.page.title"/></title>
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
    <%-- 4. Substituir os textos do corpo pela chave --%>
    <h1><fmt:message key="welcome.page.header"/></h1>
    <p>
        <fmt:message key="welcome.page.paragraph"/>
    </p>
    <a href="${pageContext.request.contextPath}/login.jsp" class="btn-login"><fmt:message key="welcome.page.button.access"/></a>
    <a href="${pageContext.request.contextPath}/estrategias-publicas" class="btn-secondary"><fmt:message key="welcome.page.button.publicStrategies"/></a>
</div>

<div class="footer">
    <%-- Bloco para obter o ano corrente de forma mais limpa --%>
    <jsp:useBean id="date" class="java.util.Date" />
    <c:set var="currentYear" value="${1900 + date.year}"/>

    <%-- 5. Usar fmt:message com fmt:param para o rodapé --%>
    <p>
        <fmt:message key="footer.copyright">
            <fmt:param value="${currentYear}"/>
        </fmt:message>
    </p>
</div>
</body>
</html>