<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- <fmt:setBundle basename="mensagens" /> --%>

<footer class="footer">
    <jsp:useBean id="javaUtilDateForYear" class="java.util.Date" scope="page" />
    <c:set var="currentYearDynamic" value="${1900 + javaUtilDateForYear.year}" />

    <%--
      Substituímos a linha de texto fixo pela tag <fmt:message>.
      A tag <fmt:param> insere o ano dinâmico no placeholder {0} da nossa mensagem.
    --%>
    <p>
        <fmt:message key="footer.copyright">
            <fmt:param value="${currentYearDynamic}" />
        </fmt:message>
    </p>

    <%--
    <p>
        <a href="..."><fmt:message key="footer.link.aboutUs"/></a> |
        <a href="..."><fmt:message key="footer.link.contact"/></a> |
        <a href="..."><fmt:message key="footer.link.terms"/></a>
    </p>
    --%>
</footer>