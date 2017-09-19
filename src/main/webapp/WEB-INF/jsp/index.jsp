<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:template>
    <p class="lead">
    <c:choose>
        <c:when test="${empty pageContext.request.userPrincipal}">
            <p><a href="/myfiles">Log in</a> to upload files.</p>
        </c:when>
        <c:otherwise>
            <h1 class="mt-5">Welcome ${pageContext.request.userPrincipal.name}</h1>
            <a href="/myfiles">Upload files, browse then and publish</a>
        </c:otherwise>
    </c:choose>
    </p>
    <c:if test="${!files.isEmpty()}">
        <h1 class="mt-5">Published files</h1>
        <table class="table table-striped">
            <c:forEach items="${files}" var="file">
                <tr>
                    <td><a href="/download/${file.id}">${file.filename}</a></td>
                    <td>${file.owner}</td>
                    <td>${file.timestamp}</td>
                </tr>
                <tr>
                    <td colspan="3">${file.description}</td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</t:template>