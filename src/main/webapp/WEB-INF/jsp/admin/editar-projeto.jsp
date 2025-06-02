<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Para formatar a data de criação, se for exibi-la --%>

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
    <title>Editar Projeto - Painel do Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao cadastrar-projeto.jsp, ajuste conforme necessidade */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 80px; }
        .info-field { background-color: #e9ecef; padding: 10px; margin-bottom: 15px; border: 1px solid #ced4da; border-radius: 4px; }
        button[type="submit"] { padding: 12px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button[type="submit"]:hover { background-color: #0056b3; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
    </style>
</head>
<body>
<div class="container">
    <h1>Editar Projeto</h1>

    <c:if test="${not empty mensagemErroFormProjeto}">
        <p class="message error"><c:out value="${mensagemErroFormProjeto}"/></p>
    </c:if>

    <c:if test="${empty projeto}">
        <p class="message error">Projeto não encontrado ou ID inválido para edição.</p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Voltar para Gerenciar Projetos</a>
        </div>
    </c:if>

    <c:if test="${not empty projeto}">
        <form action="${pageContext.request.contextPath}/admin/editarProjeto" method="post">
                <%-- Campo oculto para enviar o ID do projeto que está sendo editado --%>
            <input type="hidden" name="idProjeto" value="<c:out value='${projeto.id}'/>">

            <div>
                <label>ID do Projeto:</label>
                <div class="info-field"><c:out value='${projeto.id}'/></div>
            </div>
            <div>
                <label for="nomeProjeto">Nome do Projeto:</label>
                <input type="text" id="nomeProjeto" name="nomeProjeto" value="<c:out value='${projeto.nome}'/>" required>
            </div>
            <div>
                <label for="descricaoProjeto">Descrição do Projeto (opcional):</label>
                <textarea id="descricaoProjeto" name="descricaoProjeto"><c:out value='${projeto.descricao}'/></textarea>
            </div>
            <div>
                <label>Data de Criação:</label>
                <div class="info-field">
                    <fmt:formatDate value="${projeto.dataCriacao}" pattern="dd/MM/yyyy HH:mm:ss"/>
                </div>
            </div>

                <%-- A gestão de membros será implementada posteriormente. --%>

            <button type="submit">Salvar Alterações</button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Cancelar e Voltar</a>
        </div>
    </c:if>
</div>
</body>
</html>