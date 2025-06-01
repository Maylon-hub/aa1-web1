<%--
  Created by IntelliJ IDEA.
  User: felip
  Date: 01/06/2025
  Time: 01:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Collections, java.util.Comparator, java.text.SimpleDateFormat, java.util.Date" %>
<%@ page import="com.gametester.model.Projeto, com.gametester.model.Usuario" %>

<%
  // Recuperar dados do request e session
  List<Projeto> listaProjetos = (List<Projeto>) request.getAttribute("listaProjetos");
  String ordenarPorAtual = (String) request.getAttribute("ordenarPorAtual");
  String ordemAtual = (String) request.getAttribute("ordemAtual");

  String mensagemSucesso = (String) session.getAttribute("mensagemSucesso");
  if (mensagemSucesso != null) {
    session.removeAttribute("mensagemSucesso");
  }
  String mensagemErroRequest = (String) request.getAttribute("mensagemErro");
  String mensagemErroSession = (String) session.getAttribute("mensagemErro");
  if (mensagemErroSession != null) {
    session.removeAttribute("mensagemErroSession");
  }

  Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
  String contextPath = request.getContextPath();

  // Lógica para links de ordenação
  String linkOrdemNome = "asc";
  if ("nome".equals(ordenarPorAtual) && "asc".equals(ordemAtual)) {
    linkOrdemNome = "desc";
  }
  String linkOrdemData = "asc";
  if ("dataCriacao".equals(ordenarPorAtual) && "asc".equals(ordemAtual)) {
    linkOrdemData = "desc";
  }

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gerenciar Projetos - Game Tester System</title>
  <link rel="stylesheet" type="text/css" href="<%= contextPath %>/css/estiloPrincipal.css">
  <style>
    .main-content table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    .main-content th, .main-content td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    .main-content th { background-color: #f2f2f2; }
    .main-content th a { text-decoration: none; color: black; }
    .sort-asc::after { content: " ▲"; }
    .sort-desc::after { content: " ▼"; }
    .actions a { margin-right: 10px; }
  </style>
</head>
<body>
<div class="page-container">
  <aside class="sidebar">
    <h2>Game Tester Sys</h2>
    <nav>
      <ul>
        <% String paginaAtivaListar = "projetos"; // Para destacar o link ativo %>
        <li><a href="<%= contextPath %>/admin/dashboard.jsp" class="<%= "dashboard".equals(paginaAtivaListar) ? "active" : "" %>">Dashboard</a></li>
        <li><a href="<%= contextPath %>/admin/estrategias?action=listar" class="<%= "estrategias".equals(paginaAtivaListar) ? "active" : "" %>">Gerenciar Estratégias</a></li>
        <li><a href="<%= contextPath %>/admin/listarProjetos" class="<%= "projetos".equals(paginaAtivaListar) ? "active" : "" %>">Gerenciar Projetos</a></li>
        <li><a href="<%= contextPath %>/admin/gerenciarUsuarios" class="<%= "usuarios".equals(paginaAtivaListar) ? "active" : "" %>">Gerenciar Usuários</a></li>
        <li><a href="#" class="<%= "sessoesAdmin".equals(paginaAtivaListar) ? "active" : "" %>">Sessões de Teste (Admin)</a></li>
      </ul>
    </nav>
    <% if (usuarioLogado != null) { %>
    <div class="user-info">
      <p><strong>Usuário:</strong><br><%= usuarioLogado.getNome() %></p>
      <p><strong>Email:</strong><br><%= usuarioLogado.getEmail() %></p>
      <p><strong>Perfil:</strong><br><%= usuarioLogado.getTipoPerfil() %></p>
    </div>
    <% } %>
  </aside>

  <main class="main-content">
    <div class="header">
      <h1>Gerenciar Projetos (R4)</h1>
      <a href="<%= contextPath %>/login?action=logout" class="logout-btn">Logout</a>
    </div>

    <% if (mensagemSucesso != null) { %>
    <div class="mensagem sucesso"><%= mensagemSucesso %></div>
    <% } %>
    <% if (mensagemErroRequest != null) { %>
    <div class="mensagem erro"><%= mensagemErroRequest %></div>
    <% } %>
    <% if (mensagemErroSession != null) { %>
    <div class="mensagem erro"><%= mensagemErroSession %></div>
    <% } %>

    <p><a href="<%= contextPath %>/admin/cadastrar-projeto.jsp" class="btn">Cadastrar Novo Projeto (R3)</a></p>

    <% if (listaProjetos != null && !listaProjetos.isEmpty()) { %>
    <table>
      <thead>
      <tr>
        <th>
          <a href="?ordenarPor=nome&ordem=<%= linkOrdemNome %>">Nome do Projeto</a>
          <% if ("nome".equals(ordenarPorAtual)) { %><span class="sort-<%= ordemAtual %>"></span><% } %>
        </th>
        <th>Descrição</th>
        <th>
          <a href="?ordenarPor=dataCriacao&ordem=<%= linkOrdemData %>">Data de Criação</a>
          <% if ("dataCriacao".equals(ordenarPorAtual)) { %><span class="sort-<%= ordemAtual %>"></span><% } %>
        </th>
        <th>Ações</th>
      </tr>
      </thead>
      <tbody>
      <% for (Projeto projeto : listaProjetos) { %>
      <tr>
        <td><%= (projeto.getNome() == null ? "" : projeto.getNome()) %></td>
        <td><%= (projeto.getDescricao() == null ? "" : projeto.getDescricao()) %></td>
        <td>
          <%= (projeto.getDataCriacao() == null ? "" : sdf.format(projeto.getDataCriacao())) %>
        </td>
        <td class="actions">
          <a href="<%= contextPath %>/admin/detalhesProjeto?id=<%= projeto.getId() %>" class="btn-action detalhe">Detalhes</a>
          <%-- <a href="<%= contextPath %>/admin/editarProjeto?id=<%= projeto.getId() %>" class="btn-action editar">Editar</a> --%>
          <a href="<%= contextPath %>/admin/excluirProjeto?id=<%= projeto.getId() %>" class="btn-action excluir" onclick="return confirm('Tem certeza que deseja excluir o projeto \'<%= projeto.getNome() == null ? "" : projeto.getNome().replace("'", "\\'") %>\'? Esta ação não pode ser desfeita.');">Excluir</a>
        </td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } else { %>
    <p>Nenhum projeto cadastrado ainda.</p>
    <% } %>
  </main>
</div>

<footer class="footer">
  <% java.util.Calendar cal = java.util.Calendar.getInstance(); int year = cal.get(java.util.Calendar.YEAR); %>
  <p>&copy; <%= year %> Game Tester System. Todos os direitos reservados.</p>
</footer>

</body>
</html>
