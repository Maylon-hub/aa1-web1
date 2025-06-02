<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Editar Estratégia - Painel do Administrador</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; color: #333; }
        .container { width: 70%; margin: 20px auto; background-color: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { text-align: center; color: #0056b3; margin-bottom: 20px;}
        label { display: block; margin-top: 15px; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], textarea { width: calc(100% - 22px); padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        textarea { resize: vertical; min-height: 100px; }
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
    <h1>Editar Estratégia de Teste</h1>

    <c:if test="${not empty mensagemErroForm}"> <%-- Para erros de validação do formulário de edição --%>
        <p class="message error"><c:out value="${mensagemErroForm}"/></p>
    </c:if>

    <c:if test="${empty estrategia}">
        <p class="message error">Estratégia não encontrada ou ID inválido.</p>
        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarEstrategias">Voltar para Gerenciar Estratégias</a>
        </div>
    </c:if>

    <c:if test="${not empty estrategia}">
        <%-- O action apontará para o mesmo servlet, que tratará o POST --%>
        <form action="${pageContext.request.contextPath}/admin/editarEstrategia" method="post">
                <%-- Campo oculto para enviar o ID da estratégia que está sendo editada --%>
            <input type="hidden" name="id" value="<c:out value='${estrategia.id}'/>">

            <div>
                <label for="nome">Nome da Estratégia:</label>
                <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}'/>" required>
            </div>
            <div>
                <label for="descricao">Descrição:</label>
                <textarea id="descricao" name="descricao" required><c:out value='${estrategia.descricao}'/></textarea>
            </div>
            <div>
                <label for="exemplos">Exemplos:</label>
                    <%-- Para o campo 'exemplos' que pode conter o path da imagem, vamos separá-los aqui se necessário --%>
                    <%-- Esta é uma lógica simplificada. O ideal seria extrair o path da imagem no servlet --%>
                    <%-- e ter campos separados, ou uma interface de upload. --%>
                    <%-- Por enquanto, exibimos o conteúdo completo e o usuário edita. --%>
                <textarea id="exemplos" name="exemplos"><c:out value='${estrategia.exemplos}'/></textarea>
            </div>
            <div>
                <label for="dicas">Dicas:</label>
                <textarea id="dicas" name="dicas"><c:out value='${estrategia.dicas}'/></textarea>
            </div>
                <%-- Se quiser ter um campo separado para o imagemPath novamente na edição:
                <div>
                    <label for="imagemPath">Caminho/Nome da Imagem (opcional, se presente em exemplos):</label>
                    <input type="text" id="imagemPath" name="imagemPath" value="">
                </div>
                --%>
            <button type="submit">Salvar Alterações</button>
        </form>

        <div class="navigation-links">
            <a href="${pageContext.request.contextPath}/admin/gerenciarEstrategias">Cancelar e Voltar</a>
        </div>
    </c:if>
</div>
</body>
</html>