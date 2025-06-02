<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- AQUI: Defina o locale para Português do Brasil --%>
<fmt:setLocale value="pt" />
<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="message"/>

<%--
  Bloco de Proteção: Redireciona para o login se o usuário não for um administrador.
  A melhoria aqui é passar uma CHAVE de erro ('erroChave') em vez de um texto fixo.
  A página de login deverá ser capaz de ler esta chave.
--%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.usuarioLogado.tipoPerfil != 'ADMINISTRADOR'}">
    <c:redirect url="${pageContext.request.contextPath}/login.jsp">
        <c:param name="erroChave" value="auth.error.unauthorizedAdmin"/>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="strategy.register.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>
<div class="container">
    <h1><fmt:message key="strategy.register.header"/></h1>

    <c:if test="${not empty mensagemErro}">
        <p class="message error"><fmt:message key="${mensagemErro}"/></p>
    </c:if>

    <c:if test="${not empty mensagemSucessoTextoPronto}">
        <p class="message success"><c:out value="${mensagemSucessoTextoPronto}"/></p>
    </c:if>
    <c:if test="${not empty mensagemErroTextoPronto}">
        <p class="message error"><c:out value="${mensagemErroTextoPronto}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/cadastrarEstrategia" method="post">
        <div>
            <label for="nome"><fmt:message key="strategy.register.label.name"/></label>
            <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}'/>" required>
        </div>
        <div>
            <label for="descricao"><fmt:message key="strategy.register.label.description"/></label>
            <textarea id="descricao" name="descricao" required><c:out value='${param.descricao}'/></textarea>
        </div>
        <div>
            <label for="exemplos"><fmt:message key="strategy.register.label.examples"/></label>
            <textarea id="exemplos" name="exemplos"><c:out value='${param.exemplos}'/></textarea>
        </div>
        <div>
            <label for="dicas"><fmt:message key="strategy.register.label.tips"/></label>
            <textarea id="dicas" name="dicas"><c:out value='${param.dicas}'/></textarea>
        </div>
        <div>
            <label for="imagemPath"><fmt:message key="strategy.register.label.imagePath"/></label>
            <input type="text" id="imagemPath" name="imagemPath" value="<c:out value='${param.imagemPath}'/>">
        </div>
        <button type="submit"><fmt:message key="strategy.register.button.submit"/></button>
    </form>

    <div class="navigation-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp"><fmt:message key="strategy.register.link.backToDashboard"/></a>
        <a href="${pageContext.request.contextPath}/estrategias"><fmt:message key="strategy.register.link.viewList"/></a>
    </div>
</div>
</body>
</html>