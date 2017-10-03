<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:template>
    <jsp:attribute name="head">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/trix/0.11.0/trix.css"
        integrity="sha256-dWB4+Wz+fVR7yYPbQslYl3heVgh3qKR5mhXfZLNLSoE=" crossorigin="anonymous"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/trix/0.11.0/trix.js"
        integrity="sha256-Ak+mUV35SNYrBPU3LzuPezJMi5RLv0IkhU0sY1dVlYo=" crossorigin="anonymous"></script>
    </jsp:attribute>
    <jsp:body>
        <h1 class="mt-5">Publish ${file.filename}</h1>
        <form method="POST">
            <div style="height: 400px">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <textarea id="editor"
                          style="display:none"
                          name="description">${file.description}</textarea>
                <trix-editor input="editor" style="height: 330px"></trix-editor>
            </div>
            <button type="submit" class="btn btn-primary">Publish</button>
            <a href="/myfiles" class="btn btn-secondary">Cancel</a>
        </form>
    </jsp:body>
</t:template>