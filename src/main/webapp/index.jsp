<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Lógica para obter o ano corrente no início para que a variável ${currentYear} esteja disponível --%>
<jsp:useBean id="date" class="java.util.Date" />
<c:set var="tempYear"><jsp:getProperty name="date" property="year" /></c:set>
<c:set var="currentYear" value="${tempYear + 1900}"/>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bem-vindo ao Game Tester System</title>
    <%-- Link para seu CSS principal, se existir --%>
    <%-- <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css"> --%>
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
            box-sizing: border-box; /* Garante que padding e border não aumentem o tamanho total */
        }

        .container {
            background-color: #ffffff;
            padding: 40px 50px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 90%; /* Responsividade para telas menores */
        }

        h1 {
            color: #0056b3; /* Ajustado para um azul um pouco mais escuro para consistência */
            margin-bottom: 20px; /* Aumentado um pouco */
            font-size: 2.2em;  /* Ajustado levemente */
        }

        p.intro-text { /* Adicionada uma classe para o parágrafo de introdução */
            font-size: 1.1em;
            line-height: 1.6;
            margin-top: 0; /* Remove margem superior se vier direto após o h1 */
            margin-bottom: 35px; /* Aumentado espaço antes dos botões */
            color: #555;
        }

        .action-buttons a { /* Container para os botões para controlar espaçamento */
            margin: 10px;
        }

        .btn-base { /* Classe base para botões */
            display: inline-block;
            padding: 14px 30px; /* Levemente ajustado */
            font-size: 1.1em;  /* Ajustado */
            font-weight: bold;
            color: #fff;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            transition: background-color 0.3s ease, transform 0.2s ease;
            cursor: pointer; /* Adicionado cursor pointer */
        }
        .btn-base:hover, .btn-base:focus {
            transform: translateY(-2px);
            outline: none;
        }

        .btn-login { /* Herda de btn-base e define cores específicas */
            background-color: #007bff; /* Azul primário */
        }
        .btn-login:hover, .btn-login:focus {
            background-color: #0056b3; /* Azul mais escuro no hover */
        }

        .btn-secondary { /* Nova classe para o botão de estratégias públicas */
            background-color: #6c757d; /* Cinza Bootstrap para secundário */
        }
        .btn-secondary:hover, .btn-secondary:focus {
            background-color: #545b62; /* Cinza mais escuro no hover */
        }

        .footer {
            margin-top: 50px; /* Aumentado espaço superior */
            padding-bottom: 20px; /* Espaço inferior */
            font-size: 0.9em;
            color: #777;
            width: 100%; /* Para ocupar a largura e centralizar o texto se necessário */
        }
        .footer p {
            margin: 0; /* Remove margens padrão do parágrafo no footer */
            padding: 0;
            font-size: 1em; /* Para herdar o font-size do .footer */
            line-height: normal; /* Reset para o parágrafo do footer */
            color: inherit; /* Herda a cor do .footer */
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Game Tester System</h1>
    <p class="intro-text">
        Bem-vindo à sua plataforma dedicada para gerenciamento e execução de testes exploratórios em jogos digitais.
        Organize seus projetos, defina estratégias e acompanhe suas sessões de teste de forma eficiente.
    </p>
    <div class="action-buttons">
        <a href="${pageContext.request.contextPath}/login.jsp" class="btn-base btn-login">Acessar o Sistema</a>
        <a href="${pageContext.request.contextPath}/estrategias" class="btn-base btn-secondary">Ver Estratégias</a>
        <%-- Note que o link para estratégias públicas foi alterado de /estrategias-publicas para /estrategias
             para coincidir com o ListarEstrategiasServlet que criamos. Ajuste se o seu servlet estiver
             mapeado para /estrategias-publicas. --%>
    </div>
</div>

<div class="footer">
    <p>&copy; ${currentYear} Game Tester System. Todos os direitos reservados.</p>
</div>

</body>
</html>