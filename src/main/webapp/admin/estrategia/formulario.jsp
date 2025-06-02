<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://jakarta.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${empty estrategia.id || estrategia.id == 0 ? 'Nova' : 'Editar'} Estratégia - Admin</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"> <%-- Exemplo de CSS --%>
  <style>
    /* Estilos básicos para formulário (coloque em um CSS externo depois) */
    body { font-family: sans-serif; margin: 20px; }
    .form-container { width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; background-color: #f9f9f9;}
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; }
    .form-group input[type="text"], .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
    .form-group textarea { min-height: 80px; resize: vertical; }
    .btn-salvar { background-color: #007bff; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
    .btn-cancelar { background-color: #6c757d; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; margin-left: 10px;}
  </style>
</head>
<body>
<%--<jsp:include page="/admin/adminHeader.jsp" /> &lt;%&ndash; Incluir um cabeçalho comum de admin &ndash;%&gt;--%>

<div class="form-container">
  <h1>${empty estrategia.id || estrategia.id == 0 ? 'Cadastrar Nova' : 'Editar'} Estratégia</h1>

  <form method="POST" action="${pageContext.request.contextPath}/admin/estrategias">
    <%-- A action do servlet é 'salvar' tanto para novo quanto para editar --%>
    <input type="hidden" name="action" value="${requestScope.action}"/>

    <%-- Se estiver editando, envia o ID da estratégia --%>
    <c:if test="${not empty estrategia.id && estrategia.id != 0}">
      <input type="hidden" name="id" value="${estrategia.id}"/>
    </c:if>

    <div class="form-group">
      <label for="nome">Nome:</label>
      <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}'/>" required maxlength="100">
    </div>
    <div class="form-group">
      <label for="descricao">Descrição:</label>
      <textarea id="descricao" name="descricao" rows="4" required><c:out value='${estrategia.descricao}'/></textarea>
    </div>
    <div class="form-group">
      <label for="exemplos">Exemplos:</label>
      <textarea id="exemplos" name="exemplos" rows="3"><c:out value='${estrategia.exemplos}'/></textarea>
    </div>
    <div class="form-group">
      <label for="dicas">Dicas:</label>
      <textarea id="dicas" name="dicas" rows="3"><c:out value='${estrategia.dicas}'/></textarea>
    </div>
    <div>
      <button type="submit" class="btn-salvar">Salvar Estratégia</button>
      <a href="${pageContext.request.contextPath}/admin/estrategias?action=listar" class="btn-cancelar">Cancelar</a>
    </div>
  </form>
</div>
<p style="text-align:center; margin-top:20px;"><a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Dashboard</a></p>
</body>
</html>