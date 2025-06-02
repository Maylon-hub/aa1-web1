<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/functions" prefix="fn" %>--%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Gerenciar Estratégias - Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estiloPrincipal.css"> <%-- Exemplo de CSS --%>
  <style>
    /* Estilos básicos para tabela e botões (coloque em um CSS externo depois) */
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
<%--<jsp:include page="/admin/adminHeader.jsp" /> &lt;%&ndash; Incluir um cabeçalho comum de admin &ndash;%&gt;--%>
<h1>Gerir Estratégias</h1>

<c:if test="${not empty sessionScope.mensagemSucesso}">
  <div class="mensagem mensagem-sucesso">${sessionScope.mensagemSucesso}</div>
  <c:remove var="mensagemSucesso" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.mensagemErro}">
  <div class="mensagem mensagem-erro">${sessionScope.mensagemErro}</div>
  <c:remove var="mensagemErro" scope="session"/>
</c:if>

<a href="${pageContext.request.contextPath}/admin/estrategia?action=novo" class="btn-novo">Nova Estratégia</a>

<table>
  <thead>
  <tr>
    <th>ID</th>
    <th>Nome</th>
    <th>Descrição</th>
    <th>Ações</th>
  </tr>
  </thead>
  <tbody>
  <c:choose>
    <jsp:useBean id="listaEstrategias" type="com"/>
    <c:when test="${empty listaEstrategias}">
      <tr>
        <td colspan="4">Nenhuma estratégia cadastrada.</td>
      </tr>
    </c:when>
    <c:otherwise>
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
            <a href="${pageContext.request.contextPath}/admin/estrategia?action=editar&id=${estrategia.id}" class="btn-editar">Editar</a>
            <a href="${pageContext.request.contextPath}/admin/estrategia?action=excluir&id=${estrategia.id}" class="btn-excluir" onclick="return confirm('Tem certeza que deseja excluir esta estratégia?');">Excluir</a>
          </td>
        </tr>
      </c:forEach>
    </c:otherwise>
  </c:choose>
  </tbody>
</table>
<p><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Dashboard</a></p>
</body>
</html>