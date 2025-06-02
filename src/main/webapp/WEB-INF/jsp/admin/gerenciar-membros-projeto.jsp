<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Proteção --%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erro" value="Acesso restrito."/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gerenciar Membros do Projeto - <c:out value="${projeto.nome}"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
    <style>
        body { font-family: Arial, sans-serif; }
        .container { padding: 20px; max-width: 800px; margin: auto; }
        .projeto-info { background-color: #f0f0f0; padding: 15px; margin-bottom: 20px; border-radius: 5px; }
        .membros-section, .adicionar-membro-section { margin-bottom: 30px; }
        h2, h3 { margin-top: 0; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #e9ecef; }
        .action-btn { padding: 3px 8px; font-size:0.9em; text-decoration: none; border-radius: 3px; color:white; }
        .btn-remover { background-color: #dc3545; }
        .form-inline select, .form-inline button { padding: 8px; margin-right: 10px; }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; text-align: center; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .empty-list { font-style: italic; }
        .navigation-links { margin-top: 20px; }
    </style>
</head>
<body>
<div class="container">
    <c:if test="${not empty projeto}">
        <h1>Gerenciar Membros do Projeto: <c:out value="${projeto.nome}"/></h1>
        <div class="projeto-info">
            <p><strong>ID do Projeto:</strong> <c:out value="${projeto.id}"/></p>
            <p><strong>Descrição:</strong> <c:out value="${projeto.descricao}"/></p>
        </div>

        <%-- Mensagens de feedback para operações de adicionar/remover membro --%>
        <c:if test="${not empty sessionScope.mensagemSucessoMembros}">
            <p class="message success"><c:out value="${sessionScope.mensagemSucessoMembros}"/></p>
            <c:remove var="mensagemSucessoMembros" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.mensagemErroMembros}">
            <p class="message error"><c:out value="${sessionScope.mensagemErroMembros}"/></p>
            <c:remove var="mensagemErroMembros" scope="session"/>
        </c:if>


        <section class="membros-section">
            <h3>Membros Atuais do Projeto</h3>
            <c:choose>
                <c:when test="${not empty membrosAtuais}">
                    <table>
                        <thead>
                        <tr>
                            <th>ID Usuário</th>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Perfil</th>
                            <th>Ação</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="membro" items="${membrosAtuais}">
                            <tr>
                                <td><c:out value="${membro.id}"/></td>
                                <td><c:out value="${membro.nome}"/></td>
                                <td><c:out value="${membro.email}"/></td>
                                <td><c:out value="${membro.tipoPerfil}"/></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/gerenciarMembrosProjeto" method="post" style="display:inline;">
                                        <input type="hidden" name="acao" value="removerMembro">
                                        <input type="hidden" name="projetoId" value="${projeto.id}">
                                        <input type="hidden" name="usuarioId" value="${membro.id}">
                                        <button type="submit" class="action-btn btn-remover" onclick="return confirm('Remover ${membro.nome} do projeto?');">Remover</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p class="empty-list">Este projeto ainda não possui membros.</p>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="adicionar-membro-section">
            <h3>Adicionar Novo Membro ao Projeto</h3>
            <c:choose>
                <c:when test="${not empty usuariosDisponiveis}">
                    <form action="${pageContext.request.contextPath}/admin/gerenciarMembrosProjeto" method="post" class="form-inline">
                        <input type="hidden" name="acao" value="adicionarMembro">
                        <input type="hidden" name="projetoId" value="${projeto.id}">
                        <label for="usuarioIdAdd">Selecionar Usuário (Testadores preferencialmente):</label>
                        <select id="usuarioIdAdd" name="usuarioIdAdd" required>
                            <option value="">Selecione um usuário...</option>
                            <c:forEach var="usuario" items="${usuariosDisponiveis}">
                                <option value="${usuario.id}">
                                    <c:out value="${usuario.nome}"/> (<c:out value="${usuario.email}"/>) - <c:out value="${usuario.tipoPerfil}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <button type="submit">Adicionar Membro</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <p class="empty-list">Não há usuários disponíveis para adicionar (ou todos já são membros).</p>
                </c:otherwise>
            </c:choose>
        </section>

    </c:if> <%-- Fim do c:if test="${not empty projeto}" --%>
    <c:if test="${empty projeto}">
        <p class="message error">Projeto não encontrado ou ID inválido.</p>
    </c:if>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/gerenciarProjetos">Voltar para Gerenciar Projetos</a>
    </div>
</div>
</body>
</html>