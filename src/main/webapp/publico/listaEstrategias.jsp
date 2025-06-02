<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/functions" prefix="fn" %>--%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Estratégias de Teste - Game Tester System</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos adicionais específicos para esta página, se necessário */
        .container-publico {
            max-width: 900px;
            margin: 30px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }
        .estrategia-item {
            border-bottom: 1px solid #eee;
            padding: 15px 0;
        }
        .estrategia-item:last-child {
            border-bottom: none;
        }
        .estrategia-item h3 {
            margin-top: 0;
            color: #007bff;
        }
        .estrategia-item p {
            margin-bottom: 8px;
            line-height: 1.6;
        }
        .breadcrumb-nav { margin-bottom: 20px; font-size: 0.9em; }
        .breadcrumb-nav a { color: #007bff; text-decoration: none; }
        .breadcrumb-nav a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container-publico">
    <nav aria-label="breadcrumb" class="breadcrumb-nav">
        <ol style="list-style:none; padding:0; margin:0; display:flex;">
            <li style="margin-right:5px;"><a href="${pageContext.request.contextPath}/index.jsp">Início</a></li>
            <li class="active" aria-current="page" style="color:#6c757d;">&nbsp;/ Estratégias</li>
        </ol>
    </nav>

    <h1>Estratégias de Teste Exploratório</h1>
    <p>Conheça as diversas estratégias que podem ser aplicadas em seus testes de jogos.</p>

    <c:if test="${not empty mensagemErro}">
        <div class="mensagem mensagem-erro">${mensagemErro}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty listaEstrategias}">
            <p>Nenhuma estratégia cadastrada no momento.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="estrategia" items="${listaEstrategias}">
                <article class="estrategia-item">
                    <h3><c:out value="${estrategia.nome}"/></h3>
                    <p><strong>Descrição:</strong> <c:out value="${estrategia.descricao}"/></p>
                    <c:if test="${not empty estrategia.exemplos}">
                        <p><strong>Exemplos:</strong> <c:out value="${estrategia.exemplos}"/></p>
                    </c:if>
                    <c:if test="${not empty estrategia.dicas}">
                        <p><strong>Dicas:</strong> <c:out value="${estrategia.dicas}"/></p>
                    </c:if>
                </article>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">Acessar Área Restrita</a>
    </div>
</div>

<%-- Incluindo rodapé Publico --%>
<jsp:include page="/WEB-INF/jsp/footerPublico.jspf" />
<%-- rodape interno:
<footer class="footer" style="background-color: #343a40; color: #adb5bd; text-align:center; padding: 20px; margin-top: 30px;">
    <p>&copy; <jsp:useBean id="javaDate" class="java.util.Date" /><c:set var="currentYear"><jsp:getProperty name="javaDate" property="year" /></c:set><c:out value="${currentYear + 1900}"/> Game Tester System.</p>
</footer>

</body>
</html>