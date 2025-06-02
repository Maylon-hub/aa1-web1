<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o bundle de mensagens que será usado na página --%>
<fmt:setBundle basename="mensagens"/>

<%-- Cria uma variável para facilitar a verificação de "novo" vs "editar" --%>
<c:set var="isNew" value="${empty estrategia.id || estrategia.id == 0}"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
  <meta charset="UTF-8">
  <title>
    <c:choose>
      <c:when test="${isNew}">
        <fmt:message key="strategy.form.title.new"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="strategy.form.title.edit"/>
      </c:otherwise>
    </c:choose>
  </title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
  <style>
    /* Estilos básicos para formulário (coloque em um CSS externo depois) */
    body { font-family: sans-serif; margin: 20px; }
    .form-container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;}
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; }
    .form-group input[type="text"], .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
    .form-group textarea { min-height: 80px; resize: vertical; }
    .btn-salvar { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
    .btn-cancelar { background-color: #6c757d; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; margin-left: 10px;}
  </style>
</head>
<body>

<div class="form-container">
  <h1>
    <c:choose>
      <c:when test="${isNew}">
        <fmt:message key="strategy.form.header.new"/>
      </c:when>
      <c:otherwise>
        <fmt:message key="strategy.form.header.edit"/>
      </c:otherwise>
    </c:choose>
  </h1>

  <form method="POST" action="${pageContext.request.contextPath}/admin/estrategias">
    <input type="hidden" name="action" value="${requestScope.action}"/>

    <%-- Se estiver editando, envia o ID da estratégia --%>
    <c:if test="${not isNew}">
      <input type="hidden" name="id" value="${estrategia.id}"/>
    </c:if>

    <div class="form-group">
      <label for="nome"><fmt:message key="strategy.form.label.name"/></label>
      <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}'/>" required maxlength="100">
    </div>
    <div class="form-group">
      <label for="descricao"><fmt:message key="strategy.form.label.description"/></label>
      <textarea id="descricao" name="descricao" rows="4" required><c:out value='${estrategia.descricao}'/></textarea>
    </div>
    <div class="form-group">
      <label for="exemplos"><fmt:message key="strategy.form.label.examples"/></label>
      <textarea id="exemplos" name="exemplos" rows="3"><c:out value='${estrategia.exemplos}'/></textarea>
    </div>
    <div class="form-group">
      <label for="dicas"><fmt:message key="strategy.form.label.tips"/></label>
      <textarea id="dicas" name="dicas" rows="3"><c:out value='${estrategia.dicas}'/></textarea>
    </div>
    <div>
      <button type="submit" class="btn-salvar"><fmt:message key="strategy.form.button.save"/></button>
      <a href="${pageContext.request.contextPath}/admin/estrategias?action=listar" class="btn-cancelar"><fmt:message key="strategy.form.button.cancel"/></a>
    </div>
  </form>
</div>
<p style="text-align:center; margin-top:20px;"><a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><fmt:message key="strategy.form.link.backToDashboard"/></a></p>
</body>
</html>
