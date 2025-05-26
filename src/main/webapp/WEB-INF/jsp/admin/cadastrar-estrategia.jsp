<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- Usando a URI correta para JSTL 3.0+ --%>

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
    <title>Cadastrar Nova Estratégia</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 100px; }
        button[type="submit"] { padding: 12px 20px; background-color: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; transition: background-color 0.2s; }
        button[type="submit"]:hover { background-color: #218838; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .navigation-links { margin-top: 25px; text-align: center; }
        .navigation-links a { margin: 0 10px; text-decoration: none; color: #007bff; }
        .navigation-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container">
    <h1>Cadastrar Nova Estratégia de Teste</h1>

    <c:if test="${not empty mensagemSucesso}">
        <p class="message success"><c:out value="${mensagemSucesso}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <p class="message error"><c:out value="${mensagemErro}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/cadastrarEstrategia" method="post">
        <div>
            <label for="nome">Nome da Estratégia:</label>
            <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>" required>
        </div>
        <div>
            <label for="descricao">Descrição:</label>
            <textarea id="descricao" name="descricao" required><c:out value='${param.descricao}'/></textarea>
        </div>
        <div>
            <label for="exemplos">Exemplos (opcional):</label>
            <textarea id="exemplos" name="exemplos"><c:out value='${param.exemplos}'/></textarea>
        </div>
        <div>
            <label for="dicas">Dicas (opcional):</label>
            <textarea id="dicas" name="dicas"><c:out value='${param.dicas}'/></textarea>
        </div>
        <div>
            <label for="imagemPath">Caminho/Nome do Arquivo da Imagem (opcional):</label>
            <input type="text" id="imagemPath" name="imagemPath" value="<c:out value='${param.imagemPath}'/>">
            <%-- Para upload de imagem real, o input seria type="file" e o servlet precisaria de tratamento para multipart/form-data. --%>
            <%-- Por agora, o admin digita o nome do arquivo que ele colocaria em uma pasta específica. --%>
        </div>
        <button type="submit">Cadastrar Estratégia</button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp">Voltar ao Dashboard</a>
        <a href="${pageContext.request.contextPath}/estrategias">Ver Lista de Estratégias</a>
    </div>
</div>
</body>
</html>