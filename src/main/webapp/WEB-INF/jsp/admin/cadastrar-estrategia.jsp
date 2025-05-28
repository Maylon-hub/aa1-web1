
<<<<<<< HEAD
=======
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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
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
>>>>>>> c3b3927c0bb42c80512177e0ac4dec200659e784
