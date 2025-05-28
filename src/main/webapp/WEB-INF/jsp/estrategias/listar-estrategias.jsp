<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lista de Estratégias de Teste</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 80%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #007bff; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
        .navigation-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container">
    <h1>Lista de Todas as Estratégias de Teste</h1>

    <c:if test="${not empty mensagemErro}">
        <p class="message error"><c:out value="${mensagemErro}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${not empty listaEstrategias}">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Descrição</th>
                    <th>Exemplos</th>
                    <th>Dicas</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="estrategia" items="${listaEstrategias}">
                    <tr>
                        <td><c:out value="${estrategia.id}"/></td>
                        <td><c:out value="${estrategia.nome}"/></td>
                        <td><c:out value="${estrategia.descricao}"/></td>
                        <td><c:out value="${estrategia.exemplos}"/></td>
                        <td><c:out value="${estrategia.dicas}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="empty-list">Nenhuma estratégia cadastrada ou encontrada.</p>
        </c:otherwise>
    </c:choose>

    <div class="navigation-links">
        <%-- Link para voltar ao dashboard do admin, se o usuário estiver logado como admin --%>
        <c:if test="${sessionScope.usuarioLogado.tipoPerfil == 'ADMINISTRADOR'}">
            <a href="${pageContext.request.contextPath}/WEB-INF/jsp/admin/dashboard.jsp">Voltar ao Dashboard do Admin</a>
        </c:if>
        <%-- Link para a página inicial ou outra página pública --%>
        <a href="${pageContext.request.contextPath}/index.jsp">Página Inicial</a>
    </div>
</div>
</body>
</html>