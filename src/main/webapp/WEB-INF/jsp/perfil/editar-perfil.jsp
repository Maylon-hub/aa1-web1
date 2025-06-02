<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<c:if test="${empty sessionScope.usuarioLogado}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Sessão expirada ou acesso não autorizado."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Editar Meu Cadastro</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 60%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="email"] {
            width: calc(100% - 22px); padding: 10px; margin-bottom: 15px;
            border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
        }
        .info-field { background-color: #e9ecef; padding: 10px; margin-bottom: 15px; border: 1px solid #ced4da; border-radius: 4px; }
        button[type="submit"] { padding: 12px 20px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
    </style>
</head>
<body>
<div class="container">
    <h1>Editar Meu Cadastro</h1>

    <c:if test="${not empty mensagemSucessoPerfil}">
        <p class="message success"><c:out value="${mensagemSucessoPerfil}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroPerfil}">
        <p class="message error"><c:out value="${mensagemErroPerfil}"/></p>
    </c:if>

    <c:if test="${not empty usuarioParaEditar}">
        <form action="${pageContext.request.contextPath}/perfil/editar" method="post">

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
                <label>Tipo de Perfil:</label>
                <div class="info-field"><c:out value='${usuarioParaEditar.tipoPerfil}'/></div> <%-- Perfil não é editável pelo usuário --%>
            </div>

            <button type="submit">Salvar Alterações</button>
        </form>
    </c:if>
    <c:if test="${empty usuarioParaEditar && empty mensagemErroPerfil}">
        <p class="message error">Não foi possível carregar os dados do seu perfil.</p>
    </c:if>

    <div class="navigation-links">
        <c:choose>
            <c:when test="${sessionScope.usuarioLogado.tipoPerfil == 'ADMINISTRADOR'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
            </c:when>
            <c:when test="${sessionScope.usuarioLogado.tipoPerfil == 'TESTADOR'}">
                <a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Painel do Testador</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/index.jsp">Página Inicial</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>