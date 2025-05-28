<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html><html><head><title>Erro</title></head><body>
<h1>Ocorreu um Erro</h1>
<p>Desculpe, não foi possível processar sua solicitação.</p>
<p>${mensagemErro}</p> <%-- Exibe a mensagem de erro do servlet --%>
<a href="${pageContext.request.contextPath}/index.jsp">Voltar para a Página Inicial</a>
</body></html>

<jsp:include page="/WEB-INF/jspf/footerPublico.jspf" />