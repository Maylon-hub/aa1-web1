<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Define o bundle de mensagens que será usado nesta página --%>
<fmt:setBundle basename="mensagens"/>

<!DOCTYPE html>
<html lang="${not empty sessionScope.userLocale ? sessionScope.userLocale.language : 'pt-BR'}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="session.new.pageTitle"/></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/estiloPrincipal.css">
</head>
<body>

<div class="form-container">
    <h1><fmt:message key="session.new.header"/></h1>

    <form method="POST" action="${pageContext.request.contextPath}/testador/sessoes">
        <input type="hidden" name="action" value="salvar"/>

        <div class="form-group">
            <label for="projetoId"><fmt:message key="session.new.label.project"/></label>
            <select id="projetoId" name="projetoId" required>
                <option value=""><fmt:message key="session.new.select.default.project"/></option>
                <c:forEach var="projeto" items="${listaProjetos}">
                    <option value="${projeto.id}">${projeto.nome}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="estrategiaId"><fmt:message key="session.new.label.strategy"/></label>
            <select id="estrategiaId" name="estrategiaId" required>
                <option value=""><fmt:message key="session.new.select.default.strategy"/></option>
                <c:forEach var="estrategia" items="${listaEstrategias}">
                    <option value="${estrategia.id}">${estrategia.nome}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="tempoSessaoMinutos"><fmt:message key="session.new.label.duration"/></label>
            <input type="number" id="tempoSessaoMinutos" name="tempoSessaoMinutos" value="<c:out value='${sessaoTeste.tempoSessaoMinutos > 0 ? sessaoTeste.tempoSessaoMinutos : 60 }'/>" required min="1">
        </div>

        <div class="form-group">
            <label for="descricao"><fmt:message key="session.new.label.description"/></label>
            <textarea id="descricao" name="descricao" rows="4" required><c:out value='${sessaoTeste.descricao}'/></textarea>
        </div>

        <div>
            <button type="submit" class="btn-salvar"><fmt:message key="session.new.button.save"/></button>
            <a href="${pageContext.request.contextPath}/testador/dashboard.jsp" class="btn-cancelar"><fmt:message key="session.new.button.cancel"/></a>
        </div>
    </form>
</div>
<p style="text-align:center; margin-top:20px;"><a href="${pageContext.request.contextPath}/testador/dashboard.jsp"><fmt:message key="session.new.link.backToDashboard"/></a></p>
</body>
</html>
