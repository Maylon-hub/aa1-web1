<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="pt" />
<%-- Define o bundle de mensagens que será usado na página --%>
<fmt:setBundle basename="message"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
  <meta charset="UTF-8">
  <title><fmt:message key="strategies.manage.pageTitle"/></title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
  <style>
    /* Estilos básicos para tabela e botões (idealmente em um CSS externo) */
    body { font-family: sans-serif; margin: 20px; }
    table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    .actions a, .btn-novo { margin-right: 5px; text-decoration: none; padding: 5px 10px; border-radius: 3px; }
    .btn-editar { background-color: #ffc107; color: black; }
    .btn-excluir { background-color: #dc3545; color: white; }
    .btn-novo { background-color: #28a745; color: white; display: inline-block; margin-bottom: 15px; }
    .mensagem { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
    .mensagem-sucesso { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .mensagem-erro { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
  </style>
</head>
<body>
<h1><fmt:message key="strategies.manage.header"/></h1>

<%-- O Servlet deve passar a CHAVE da mensagem de sucesso/erro --%>
<c:if test="${not empty sessionScope.mensagemSucesso}">
  <div class="mensagem mensagem-sucesso"><fmt:message key="${sessionScope.mensagemSucesso}"/></div>
  <c:remove var="mensagemSucesso" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.mensagemErro}">
  <div class="mensagem mensagem-erro"><fmt:message key="${sessionScope.mensagemErro}"/></div>
  <c:remove var="mensagemErro" scope="session"/>
</c:if>

<a href="${pageContext.request.contextPath}/admin/estrategia?action=novo" class="btn-novo">
  <fmt:message key="strategies.manage.button.new"/>
</a>

<table>
  <thead>
  <tr>
    <th><fmt:message key="strategies.manage.table.header.id"/></th>
    <th><fmt:message key="strategies.manage.table.header.name"/></th>
    <th><fmt:message key="strategies.manage.table.header.description"/></th>
    <th><fmt:message key="strategies.manage.table.header.actions"/></th>
  </tr>
  </thead>
  <tbody>
  <c:choose>
    <c:when test="${empty listaEstrategias}">
      <tr>
        <td colspan="4"><fmt:message key="strategies.manage.table.empty"/></td>
      </tr>
    </c:when>
    <c:otherwise>
      <%-- Armazena a mensagem de confirmação em uma variável para usar no JS --%>
      <fmt:message key="strategies.manage.table.confirmDelete" var="confirmDeleteMsg"/>

      <c:forEach var="estrategia" items="${listaEstrategias}">
        <tr>
          <td>${estrategia.id}</td>
          <td><c:out value="${estrategia.nome}"/></td>
          <td>
            <c:choose>
              <c:when test="${fn:length(estrategia.descricao) > 100}">
                <c:out value="${fn:substring(estrategia.descricao, 0, 100)}"/>...
              </c:when>
              <c:otherwise>
                <c:out value="${estrategia.descricao}"/>
              </c:otherwise>
            </c:choose>
          </td>
          <td class="actions">
            <a href="${pageContext.request.contextPath}/admin/estrategia?action=editar&id=${estrategia.id}" class="btn-editar">
              <fmt:message key="strategies.manage.table.button.edit"/>
            </a>
            <a href="${pageContext.request.contextPath}/admin/estrategia?action=excluir&id=${estrategia.id}" class="btn-excluir"
               onclick="return confirm('${fn:escapeXml(confirmDeleteMsg)}');">
              <fmt:message key="strategies.manage.table.button.delete"/>
            </a>
          </td>
        </tr>
      </c:forEach>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>
<p><a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><fmt:message key="strategies.manage.link.backToDashboard"/></a></p>
</body>
</html>