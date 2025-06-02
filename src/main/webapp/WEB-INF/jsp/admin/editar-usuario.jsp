<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso não autorizado."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Editar Usuário - Painel do Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao cadastrar-usuario.jsp */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 60%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"], input[type="password"], select {
            width: calc(100% - 22px); padding: 10px; margin-bottom: 15px;
            border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
        }
        .info-field { background-color: #e9ecef; padding: 10px; margin-bottom: 15px; border: 1px solid #ced4da; border-radius: 4px; }
        button[type="submit"] { padding: 12px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
    </style>
</head>
<body>
<div class="container">
    <h1>Editar Usuário</h1>

    <c:if test="${not empty mensagemErroFormUsuario}">
        <p class="message error"><c:out value="${mensagemErroFormUsuario}"/></p>
    </c:if>

    <c:if test="${empty usuarioParaEditar}">
        <p class="message error">Usuário não encontrado ou ID inválido para edição.</p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios">Voltar para Gerenciar Usuários</a>
        </div>
    </c:if>

    <c:if test="${not empty usuarioParaEditar}">
        <form action="${pageContext.request.contextPath}/admin/editarUsuario" method="post">
            <input type="hidden" name="idUsuario" value="<c:out value='${usuarioParaEditar.id}'/>">

            <div>
                <label>ID do Usuário:</label>
                <div class="info-field"><c:out value='${usuarioParaEditar.id}'/></div>
            </div>
            <div>
                <label for="nomeUsuario">Nome Completo:</label>
                <input type="text" id="nomeUsuario" name="nomeUsuario" value="<c:out value='${usuarioParaEditar.nome}'/>" required>
            </div>
            <div>
                <label for="emailUsuario">E-mail:</label>
                <input type="email" id="emailUsuario" name="emailUsuario" value="<c:out value='${usuarioParaEditar.email}'/>" required>
            </div>
            <div>
                <label for="tipoPerfilUsuario">Tipo de Perfil:</label>
                <select id="tipoPerfilUsuario" name="tipoPerfilUsuario" required>
                    <option value="TESTADOR" ${usuarioParaEditar.tipoPerfil == 'TESTADOR' ? 'selected' : ''}>Testador</option>
                    <option value="ADMINISTRADOR" ${usuarioParaEditar.tipoPerfil == 'ADMINISTRADOR' ? 'selected' : ''}>Administrador</option>
                </select>
            </div>
            <hr>
            <p><em>Deixe os campos de senha em branco se não desejar alterá-la.</em></p>
            <div>
                <label for="novaSenhaUsuario">Nova Senha (opcional):</label>
                <input type="password" id="novaSenhaUsuario" name="novaSenhaUsuario">
            </div>
            <div>
                <label for="confirmaNovaSenhaUsuario">Confirmar Nova Senha (se alterando):</label>
                <input type="password" id="confirmaNovaSenhaUsuario" name="confirmaNovaSenhaUsuario">
            </div>

            <button type="submit">Salvar Alterações</button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarUsuarios">Cancelar e Voltar</a>
        </div>
    </c:if>
</div>
</body>
</html>