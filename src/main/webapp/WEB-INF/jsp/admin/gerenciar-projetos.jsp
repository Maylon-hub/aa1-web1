<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Para formatação de datas --%>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Gerenciar Projetos - Painel do Administrador</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css"> <%-- Seu CSS principal --%>
  <style>
    /* Estilos básicos para a tabela e botões - ajuste conforme seu CSS principal */
    body { font-family: Arial, sans-serif; }
    .container { padding: 20px; }
    .action-btn { margin-right: 5px; padding: 5px 10px; text-decoration: none; border-radius: 3px; }
    .btn-cadastrar { background-color: #28a745; color: white; display: inline-block; margin-bottom: 15px; }
    .btn-editar { background-color: #ffc107; color: black; }
    .btn-excluir { background-color: #dc3545; color: white; }
    /* Adicione mais estilos conforme necessário para .btn-gerenciar-membros, etc. */
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
  <h1>Gerenciar Projetos</h1>

  <%-- Para mensagens de feedback de ações como cadastro, edição, exclusão --%>
  <c:if test="${not empty sessionScope.mensagemSucessoGerenciamentoProjetos}">
    <p class="message success"><c:out value="${sessionScope.mensagemSucessoGerenciamentoProjetos}"/></p>
    <c:remove var="mensagemSucessoGerenciamentoProjetos" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.mensagemErroGerenciamentoProjetos}">
    <p class="message error"><c:out value="${sessionScope.mensagemErroGerenciamentoProjetos}"/></p>
    <c:remove var="mensagemErroGerenciamentoProjetos" scope="session"/>
  </c:if>

  <%-- Mensagem de erro ao carregar a lista (vinda do request) --%>
  <c:if test="${not empty mensagemErro}">
    <p class="message error"><c:out value="${mensagemErro}"/></p>
  </c:if>

  <a href="${pageContext.request.contextPath}/admin/cadastrarProjeto" class="action-btn btn-cadastrar">Cadastrar Novo Projeto</a>
  <c:choose>
    <c:when test="${not empty listaProjetos}">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>Descrição</th>
          <th>Data de Criação</th>
          <th>Ações</th>
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
              <a href="${pageContext.request.contextPath}/admin/excluirProjeto?id=${projeto.id}" class="action-btn btn-excluir" onclick="return confirm('Tem certeza que deseja excluir este projeto? Isso pode afetar sessões de teste associadas.');">Excluir</a>
                <%-- Link para Gerenciar Membros (implementação futura) --%>
                <%-- <a href="${pageContext.request.contextPath}/admin/gerenciarMembrosProjeto?projetoId=${projeto.id}" class="action-btn">Gerenciar Membros</a> --%>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <c:if test="${empty mensagemErro}"> <%-- Só mostra "nenhum projeto" se não houve erro ao carregar a lista --%>
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