<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalhes da Estratégia - <c:out value="${estrategiaDetalhes.nome}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #0056b3; margin-bottom: 5px; }
        h2 { color: #333; margin-top: 25px; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px;}
        p, .details-block { margin-bottom: 15px; line-height: 1.6; white-space: pre-wrap; /* Para preservar quebras de linha em exemplos/dicas */ }
        .details-block { background-color: #f8f9fa; padding: 10px; border-radius: 4px; }
        .navigation-links { margin-top: 30px; text-align: center; }
        .navigation-links a, .navigation-links button {
            margin: 0 10px; text-decoration: none; color: #007bff;
            padding: 8px 15px; border-radius: 4px; border: 1px solid #007bff;
            background-color: white; cursor: pointer;
        }
        .navigation-links a:hover, .navigation-links button:hover { background-color: #f0f0f0; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
<div class="container">
    <c:choose>
        <c:when test="${not empty estrategiaDetalhes}">
            <h1><c:out value="${estrategiaDetalhes.nome}"/></h1>
            <p><em>ID: <c:out value="${estrategiaDetalhes.id}"/></em></p>

            <h2>Descrição</h2>
            <div class="details-block">
                <p><c:out value="${estrategiaDetalhes.descricao}"/></p>
            </div>

            <h2>Exemplos</h2>
            <div class="details-block">
                    <%-- O campo exemplos pode conter a referência [Imagem: path] --%>
                    <%-- Para exibir a imagem de fato, seria necessário mais lógica aqui ou no CSS/JS --%>
                <p><c:out value="${estrategiaDetalhes.exemplos}"/></p>
            </div>

            <h2>Dicas</h2>
            <div class="details-block">
                <p><c:out value="${estrategiaDetalhes.dicas}"/></p>
            </div>
        </c:when>
        <c:otherwise>
            <p class="message error">
                <c:choose>
                    <c:when test="${not empty mensagemErro}">${mensagemErro}</c:when>
                    <c:otherwise>Estratégia não encontrada.</c:otherwise>
                </c:choose>
            </p>
        </c:otherwise>
    </c:choose>

    <div class="navigation-links">
        <button onclick="window.close();">Fechar Janela</button> <%-- Se aberto em target="_blank" --%>
        <a href="${pageContext.request.contextPath}/estrategias">Ver Todas as Estratégias</a>
    </div>
</div>
</body>
</html>