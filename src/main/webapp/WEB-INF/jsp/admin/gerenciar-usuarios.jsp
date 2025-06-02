<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Gerenciar Usuários - Painel do Administrador</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css"> <%-- Seu CSS principal --%>
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
  <h1>Gerenciar Usuários</h1>

  <%-- Para mensagens de feedback de ações como cadastro, edição, exclusão de usuários --%>
  <c:if test="${not empty sessionScope.mensagemSucessoGerenciamentoUsuarios}">
    <p class="message success"><c:out value="${sessionScope.mensagemSucessoGerenciamentoUsuarios}"/></p>
    <c:remove var="mensagemSucessoGerenciamentoUsuarios" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.mensagemErroGerenciamentoUsuarios}">
    <p class="message error"><c:out value="${sessionScope.mensagemErroGerenciamentoUsuarios}"/></p>
    <c:remove var="mensagemErroGerenciamentoUsuarios" scope="session"/>
  </c:if>

  <%-- Mensagem de erro ao carregar a lista (vinda do request) --%>
  <c:if test="${not empty mensagemErroUsuarios}">
    <p class="message error"><c:out value="${mensagemErroUsuarios}"/></p>
  </c:if>

  <a href="${pageContext.request.contextPath}/admin/cadastrarUsuario" class="action-btn btn-cadastrar">Cadastrar Novo Usuário</a>

  <c:choose>
    <c:when test="${not empty listaUsuarios}">
      <table>
        <thead>
        <tr>
          <th>ID</th>
          <th>Nome</th>
          <th>Email</th>
          <th>Perfil</th>
          <th>Ações</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="usuario" items="${listaUsuarios}">
          <tr>
            <td><c:out value="${usuario.id}"/></td>
            <td><c:out value="${usuario.nome}"/></td>
            <td><c:out value="${usuario.email}"/></td>
            <td><c:out value="${usuario.tipoPerfil}"/></td>
            <td>
              <a href="${pageContext.request.contextPath}/admin/editarUsuario?id=${usuario.id}" class="action-btn btn-editar">Editar</a>
                <%-- Evitar que o admin logado se auto-exclua ou pelo menos dar um aviso forte --%>
              <c:if test="${sessionScope.usuarioLogado.id != usuario.id}">
                <a href="${pageContext.request.contextPath}/admin/excluirUsuario?id=${usuario.id}" class="action-btn btn-excluir" onclick="return confirm('Tem certeza que deseja excluir este usuário: ${usuario.nome}?');">Excluir</a>
              </c:if>
              <c:if test="${sessionScope.usuarioLogado.id == usuario.id}">
                <span class="action-btn" style="color: grey; cursor: not-allowed;" title="Não é possível auto-exclusão">Excluir</span>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <c:if test="${empty mensagemErroUsuarios}"> <%-- Só mostra "nenhum usuário" se não houve erro ao carregar a lista --%>
        <p class="empty-list">Nenhum usuário cadastrado no sistema.</p>
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