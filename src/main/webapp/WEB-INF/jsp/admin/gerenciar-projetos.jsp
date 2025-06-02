<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Gerenciar Projetos - Painel do Administrador</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
  <style>
    body { font-family: Arial, sans-serif; }
    .container { padding: 20px; }
    .action-btn { margin-top: 5px; margin-right: 5px; padding: 5px 10px; text-decoration: none; border-radius: 3px; display: inline-block; }
    .btn-cadastrar { background-color: #28a745; color: white; margin-bottom: 15px; }
    .btn-editar { background-color: #ffc107; color: black; }
    .btn-excluir { background-color: #dc3545; color: white; }
    .btn-membros { background-color: #007bff; color: white; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    th a { text-decoration: none; color: inherit; }
    th a:hover { text-decoration: underline; }
    .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
    .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .empty-list { text-align: center; font-style: italic; margin-top: 20px; }
    .navigation-links { margin-top: 20px; }
    .actions-cell { min-width: 280px; }
  </style>
</head>
<body>
<div class="container">
  <h1>Gerenciar Projetos</h1>

  <c:if test="${not empty sessionScope.mensagemSucessoGerenciamentoProjetos}">
    <p class="message success"><c:out value="${sessionScope.mensagemSucessoGerenciamentoProjetos}"/></p>
    <c:remove var="mensagemSucessoGerenciamentoProjetos" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.mensagemErroGerenciamentoProjetos}">
    <p class="message error"><c:out value="${sessionScope.mensagemErroGerenciamentoProjetos}"/></p>
    <c:remove var="mensagemErroGerenciamentoProjetos" scope="session"/>
  </c:if>
  <c:if test="${not empty mensagemErro}">
    <p class="message error"><c:out value="${mensagemErro}"/></p>
  </c:if>

  <a href="${pageContext.request.contextPath}/admin/cadastrarProjeto" class="action-btn btn-cadastrar">Cadastrar Novo Projeto</a>

  <c:choose>
    <c:when test="${not empty listaProjetos}">
      <table>
        <thead>
        <tr>
          <th>
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos?sort=id&order=${(currentSortField == 'id' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
              ID ${currentSortField == 'id' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
            </a>
          </th>
          <th>
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos?sort=nome&order=${(currentSortField == 'nome' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
              Nome ${currentSortField == 'nome' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
            </a>
          </th>
          <th>Descrição</th>
          <th>
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos?sort=data_criacao&order=${(currentSortField == 'data_criacao' && currentSortOrder == 'asc') ? 'desc' : 'asc'}">
              Data de Criação ${currentSortField == 'data_criacao' ? (currentSortOrder == 'asc' ? '▲' : '▼') : ''}
            </a>
          </th>
          <th class="actions-cell">Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="projeto" items="${listaProjetos}">
          <tr>
            <td><c:out value="${projeto.id}"/></td>
            <td><c:out value="${projeto.nome}"/></td>
            <td><c:out value="${projeto.descricao}"/></td>
            <td>
              <fmt:formatDate value="${projeto.dataCriacao}" pattern="dd/MM/yyyy HH:mm:ss"/>
            </td>
            <td>
              <a href="${pageContext.request.contextPath}/admin/editarProjeto?id=${projeto.id}" class="action-btn btn-editar">Editar</a>
              <a href="${pageContext.request.contextPath}/admin/excluirProjeto?id=${projeto.id}" class="action-btn btn-excluir" onclick="return confirm('Tem certeza que deseja excluir este projeto? Isso pode afetar sessões de teste e membros associados.');">Excluir</a>
              <a href="${pageContext.request.contextPath}/admin/gerenciarMembrosProjeto?projetoId=${projeto.id}" class="action-btn btn-membros">Gerenciar Membros</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <c:if test="${empty mensagemErro}">
        <p class="empty-list">Nenhum projeto cadastrado no sistema.</p>
      </c:if>
    </c:otherwise>
  </c:choose>
  <br/>
  <div class="navigation-links">
    <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
  </div>
</div>
</body>
</html>