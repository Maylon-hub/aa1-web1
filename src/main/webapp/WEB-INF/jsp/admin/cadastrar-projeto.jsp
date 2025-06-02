<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Proteção: Redireciona para login se não for admin logado --%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso não autorizado. Faça login como administrador."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cadastrar Novo Projeto - Painel do Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao cadastrar-estrategia.jsp, ajuste conforme necessidade */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 80px; }
        button[type="submit"] { padding: 12px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button[type="submit"]:hover { background-color: #218838; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
    </style>
</head>
<body>
<div class="container">
    <h1>Cadastrar Novo Projeto</h1>

    <c:if test="${not empty mensagemSucessoProjeto}">
        <p class="message success"><c:out value="${mensagemSucessoProjeto}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroProjeto}">
        <p class="message error"><c:out value="${mensagemErroProjeto}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/cadastrarProjeto" method="post">
        <div>
            <label for="nomeProjeto">Nome do Projeto:</label>
            <input type="text" id="nomeProjeto" name="nomeProjeto" value="<c:out value='${valorNomeProjeto}'/>" required>
        </div>
        <div>
            <label for="descricaoProjeto">Descrição do Projeto (opcional):</label>
            <textarea id="descricaoProjeto" name="descricaoProjeto"><c:out value='${valorDescricaoProjeto}'/></textarea>
        </div>

        <%-- A data de criação é gerada pelo sistema e não é um campo de entrada. --%>
        <%-- A gestão de membros será implementada posteriormente. --%>

        <button type="submit">Cadastrar Projeto</button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Voltar para Gerenciar Projetos</a>
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
    </div>
</div>
</body>
</html>