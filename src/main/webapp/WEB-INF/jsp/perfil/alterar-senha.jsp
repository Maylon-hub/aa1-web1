<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Sessão expirada ou acesso não autorizado."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Alterar Senha</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        /* Estilos similares ao editar-perfil.jsp */
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 50%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); max-width: 500px; }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="password"] {
            width: calc(100% - 22px); padding: 10px; margin-bottom: 15px;
            border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;
        }
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
    <h1>Alterar Minha Senha</h1>

    <c:if test="${not empty mensagemSucessoSenha}">
        <p class="message success"><c:out value="${mensagemSucessoSenha}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroSenha}">
        <p class="message error"><c:out value="${mensagemErroSenha}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/perfil/alterarSenha" method="post">
        <div>
            <label for="senhaAtual">Senha Atual:</label>
            <input type="password" id="senhaAtual" name="senhaAtual" required>
        </div>
        <div>
            <label for="novaSenha">Nova Senha:</label>
            <input type="password" id="novaSenha" name="novaSenha" required>
        </div>
        <div>
            <label for="confirmaNovaSenha">Confirmar Nova Senha:</label>
            <input type="password" id="confirmaNovaSenha" name="confirmaNovaSenha" required>
        </div>

        <button type="submit">Alterar Senha</button>
    </form>

    <div class="navigation-links">
        <c:choose>
            <c:when test="${sessionScope.usuarioLogado.tipoPerfil == 'ADMINISTRADOR'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Painel do Administrador</a>
            </c:when>
            <c:when test="${sessionScope.usuarioLogado.tipoPerfil == 'TESTADOR'}">
                <a href="${pageContext.request.contextPath}/testador/dashboard.jsp">Voltar ao Painel do Testador</a>
            </c:when>
            <c:otherwise>
                <%-- Se por algum motivo o perfil não for identificado, volta para login ou index --%>
                <a href="${pageContext.request.contextPath}/login.jsp">Voltar</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>