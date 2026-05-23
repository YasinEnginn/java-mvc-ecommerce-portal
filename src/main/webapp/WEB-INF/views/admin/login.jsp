<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Giriş</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<main class="page auth-page">
    <section class="form-panel narrow">
        <h1>Admin girişi</h1>
        <c:if test="${not empty error}">
            <div class="alert error"><c:out value="${error}" /></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/admin/login" method="post" class="stack-form">
            <label>
                E-posta
                <input type="email" name="email" value="<c:out value='${email}' />" required>
            </label>
            <label>
                Şifre
                <input type="password" name="password" required>
            </label>
            <button type="submit" class="button">Panele gir</button>
        </form>
    </section>
</main>
</body>
</html>
