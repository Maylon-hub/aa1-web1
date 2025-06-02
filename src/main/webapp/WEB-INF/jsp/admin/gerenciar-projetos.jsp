<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="mensagens"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="project.manage.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos básicos para a tabela e botões - ajuste conforme seu CSS principal */
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; }
        .action-btn { margin-right: 5px; padding: 5px 10px; text-decoration: none; border-radius: 3px; }
        .btn-cadastrar { background-color: #28a745; color: white; display: inline-block; margin-bottom: 15px; }
        .btn-editar { background-color: #ffc107; color: black; }
        .btn-excluir { background-color: #dc3545; color: white; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
        .navigation-links { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <h1><fmt:message key="project.manage.header"/></h1>

    <%-- O Servlet deve passar a CHAVE da mensagem de feedback --%>
    <c:if test="${not empty sessionScope.mensagemSucessoGerenciamentoProjetos}">
        <p class="message success"><fmt:message key="${sessionScope.mensagemSucessoGerenciamentoProjetos}"/></p>
        <c:remove var="mensagemSucessoGerenciamentoProjetos" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.mensagemErroGerenciamentoProjetos}">
        <p class="message error"><fmt:message key="${sessionScope.mensagemErroGerenciamentoProjetos}"/></p>
        <c:remove var="mensagemErroGerenciamentoProjetos" scope="session"/>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <p class="message error"><fmt:message key="${mensagemErro}"/></p>
    </c:if>

    <a href="${pageContext.request.contextPath}/admin/cadastrarProjeto" class="action-btn btn-cadastrar">
        <fmt:message key="project.manage.button.new"/>
    </a>

    <c:choose>
        <c:when test="${not empty listaProjetos}">
            <table>
                <thead>
                <tr>
                    <th><fmt:message key="project.manage.table.header.id"/></th>
                    <th><fmt:message key="project.manage.table.header.name"/></th>
                    <th><fmt:message key="project.manage.table.header.description"/></th>
                    <th><fmt:message key="project.manage.table.header.creationDate"/></th>
                    <th><fmt:message key="project.manage.table.header.actions"/></th>
                </tr>
                </thead>
                <tbody>
                <%-- Armazena a mensagem de confirmação em uma variável para usar no JS --%>
                <fmt:message key="project.manage.table.confirmDelete" var="confirmDeleteMsg"/>

                <c:forEach var="projeto" items="${listaProjetos}">
                    <tr>
                        <td><c:out value="${projeto.id}"/></td>
                        <td><c:out value="${projeto.nome}"/></td>
                        <td><c:out value="${projeto.descricao}"/></td>
                        <td>
                            <%-- Usando dateStyle/timeStyle para uma melhor internacionalização da data --%>
                            <fmt:formatDate value="${projeto.dataCriacao}" dateStyle="medium" timeStyle="short"/>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/editarProjeto?id=${projeto.id}" class="action-btn btn-editar">
                                <fmt:message key="project.manage.table.button.edit"/>
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/excluirProjeto?id=${projeto.id}" class="action-btn btn-excluir"
                               onclick="return confirm('${fn:escapeXml(confirmDeleteMsg)}');">
                                <fmt:message key="project.manage.table.button.delete"/>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <c:if test="${empty mensagemErro}">
                <p class="empty-list"><fmt:message key="project.manage.table.empty"/></p>
            </c:if>
        </c:otherwise>
    </c:choose>
    <br/>
    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><fmt:message key="project.manage.link.backToDashboard"/></a>
    </div>
</div>
</body>
</html>
