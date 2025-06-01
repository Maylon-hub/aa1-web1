<%--
  Created by IntelliJ IDEA.
  User: felip
  Date: 31/05/2025
  Time: 23:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.ArrayList" %>
<%@ page import="com.gametester.model.Usuario" %>

<%
    String contextPath = request.getContextPath();
    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
    String mensagemErro = (String) request.getAttribute("mensagemErro");

    List<Usuario> listaUsuariosDisponiveis = (List<Usuario>) request.getAttribute("listaUsuariosDisponiveis");
    if (listaUsuariosDisponiveis == null) {
        listaUsuariosDisponiveis = new ArrayList<>();
    }

    // Para repopular seleção múltipla
    String[] paramMembrosIds = request.getParameterValues("membrosIds");
    java.util.Set<String> selectedMembrosIds = new java.util.HashSet<>();
    if (paramMembrosIds != null) {
        for (String id : paramMembrosIds) {
            selectedMembrosIds.add(id);
        }
    }
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastrar Novo Projeto - Game Tester System</title>
    <link rel="stylesheet" type="text/css" href="<%= contextPath %>/css/estiloPrincipal.css">
    <style>
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input[type="text"],
        .form-group textarea,
        .form-group select {
            width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
        }
        .form-group select[multiple] { height: 120px; }
        .form-group small { font-size: 0.9em; color: #666; }
    </style>
</head>
<body>
<div class="page-container">
    <aside class="sidebar">
        <h2>Game Tester Sys</h2>
        <nav>
            <ul>
                <% String paginaAtivaCadastrar = "projetos"; %>
                <li><a href="<%= contextPath %>/admin/dashboard.jsp" class="<%= "dashboard".equals(paginaAtivaCadastrar) ? "active" : "" %>">Dashboard</a></li>
                <li><a href="<%= contextPath %>/admin/estrategias?action=listar" class="<%= "estrategias".equals(paginaAtivaCadastrar) ? "active" : "" %>">Gerenciar Estratégias</a></li>
                <li><a href="<%= contextPath %>/admin/listar-projetos.jsp" class="<%= "projetos".equals(paginaAtivaCadastrar) ? "active" : "" %>">Gerenciar Projetos</a></li>
                <li><a href="<%= contextPath %>/admin/gerenciarUsuarios" class="<%= "usuarios".equals(paginaAtivaCadastrar) ? "active" : "" %>">Gerenciar Usuários</a></li>
                <li><a href="#" class="<%= "sessoesAdmin".equals(paginaAtivaCadastrar) ? "active" : "" %>">Sessões de Teste (Admin)</a></li>
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
            <h1>Cadastrar Novo Projeto (R3)</h1>
            <a href="<%= contextPath %>/login?action=logout" class="logout-btn">Logout</a>
        </div>

        <% if (mensagemErro != null) { %>
        <div class="mensagem erro"><%= mensagemErro %></div>
        <% } %>

        <form method="POST" action="<%= contextPath %>/admin/cadastrar-projeto.jsp" class="form-cadastro">
            <div class="form-group">
                <label for="nomeProjeto">Nome do Projeto:</label>
                <input type="text" id="nomeProjeto" name="nomeProjeto" value="<%= request.getParameter("nomeProjeto") != null ? request.getParameter("nomeProjeto") : "" %>" required>
            </div>

            <div class="form-group">
                <label for="descricao">Descrição:</label>
                <textarea id="descricao" name="descricao" rows="4"><%= request.getParameter("descricao") != null ? request.getParameter("descricao") : "" %></textarea>
            </div>

            <div class="form-group">
                <label for="membrosIds">Membros Permitidos:</label>
                <select id="membrosIds" name="membrosIds" multiple>
                    <% for (Usuario usuario : listaUsuariosDisponiveis) { %>
                    <option value="<%= usuario.getId() %>"
                            <%= selectedMembrosIds.contains(String.valueOf(usuario.getId())) ? "selected" : "" %>>
                        <%= usuario.getNome() %> (<%= usuario.getTipoPerfil() %>)
                    </option>
                    <% } %>
                </select>
                <small>Segure Ctrl (ou Cmd no Mac) para selecionar múltiplos membros.</small>
            </div>

            <button type="submit" class="btn">Cadastrar Projeto</button>
            <a href="<%= contextPath %>/admin/listar-projetos.jsp" class="btn btn-secondary">Cancelar</a>
        </form>
    </main>
</div>

<footer class="footer">
    <% java.util.Calendar cal = java.util.Calendar.getInstance(); int year = cal.get(java.util.Calendar.YEAR); %>
    <p>&copy; <%= year %> Game Tester System. Todos os direitos reservados.</p>
</footer>

</body>
</html>