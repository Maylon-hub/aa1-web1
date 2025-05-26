<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- Para usar JSTL --%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Lista de Estratégias de Teste</title>
  <%--
      Você pode adicionar um link para um arquivo CSS externo aqui para estilizar a página, por exemplo:
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estilos.css">
  --%>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f4; color: #333; }
    .container { width: 85%; margin: auto; background-color: #fff; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    h1 { text-align: center; color: #333; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background-color: #007bff; color: white; }
    tr:nth-child(even) { background-color: #f9f9f9; }
    tr:hover { background-color: #f1f1f1; }
    .no-strategies { text-align: center; color: #777; margin-top: 20px; }
    .action-links a { margin-right: 10px; text-decoration: none; color: #007bff; }
    .action-links a:hover { text-decoration: underline; }
    img.strategy-image { max-width: 100px; max-height: 100px; border-radius: 4px; }
  </style>
</head>
<body>
<div class="container">
  <h1>Estratégias de Teste Exploratório</h1>
  <p>Abaixo estão listadas as estratégias cadastradas no sistema. Visitantes podem visualizar e navegar nas estratégias cadastradas.</p>

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
            <%-- Descomente se for implementar a exibição de imagens --%>
            <%-- <th>Imagem</th> --%>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="estrategia" items="${listaEstrategias}">
          <tr>
            <td><c:out value="${estrategia.id}" /></td>
            <td><c:out value="${estrategia.nome}" /></td>
            <td><c:out value="${estrategia.descricao}" /></td>
            <td><c:out value="${estrategia.exemplos}" /></td>
            <td><c:out value="${estrategia.dicas}" /></td>
              <%--
              <td>
                  <c:if test="${not empty estrategia.imagemPath}">
                      <%-- Supondo que as imagens estejam em uma pasta 'imagens-estrategias' na raiz do webapp --%>
              <%-- Você precisará de um mecanismo para servir essas imagens ou de um caminho absoluto/relativo correto --%>
              <%-- <img class="strategy-image" src="${pageContext.request.contextPath}/path/to/your/images/${estrategia.imagemPath}" alt="Imagem da Estratégia ${estrategia.nome}" /> --%>
              <%-- </c:if>
              <c:if test="${empty estrategia.imagemPath}">
                  <span>Sem imagem</span>
              </c:if>
          </td>
          --%>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p class="no-strategies">Nenhuma estratégia cadastrada no momento.</p>
    </c:otherwise>
  </c:choose>

  <div style="margin-top: 20px; text-align: center;">
    <%-- Exemplo de link para uma página inicial (você precisará criar esta página/servlet) --%>
    <%-- <a href="${pageContext.request.contextPath}/index.jsp">Voltar à Página Inicial</a> --%>
  </div>
</div>
</body>
</html>