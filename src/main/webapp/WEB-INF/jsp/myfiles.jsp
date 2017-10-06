<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:template>
    <h1 class="mt-5">My files</h1>
    <p class="lead">
    <form action="/upload" method="post" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        Upload: <input type="file" name="file" onchange="this.form.submit()"/>
    </form>
    </p>
    <form method="get">
        Filter: <input name="name" autofocus style="width:90%"
                       value="${param.name}">
    </form>
    <table class="table table-striped">
        <thead class="thead-inverse">
        <tr>
            <th>Filename</th>
            <th>Upload date</th>
            <th>Published</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${files}" var="file">
            <tr>
                <td><a href="/download/${file.id}">${file.filename}</a></td>
                <td>${file.timestamp}</td>
                <td>${file.published}</td>
                <td>
                    <c:choose>
                        <c:when test="${!file.published}">
                            <a class="btn btn-primary" href="/publish/${file.id}">Publish</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-primary" href="/publish/${file.id}">Edit</a>
                        </c:otherwise>
                    </c:choose>
                    <a class="btn btn-danger"
                       onclick="return confirm('Are you sure?')"
                       href="/delete/${file.id}">Delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:choose>
        <c:when test="${empty files}">
            <p class="lead">You have not uploaded any files, yet.</p>
        </c:when>
        <c:otherwise>
        </c:otherwise>
    </c:choose>
</t:template>