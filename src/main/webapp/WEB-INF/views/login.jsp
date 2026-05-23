<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Giriş" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="form-panel narrow">
    <h1>Giriş yap</h1>
    <c:if test="${not empty error}">
        <div class="alert error"><c:out value="${error}" /></div>
    </c:if>
    <form action="${pageContext.request.contextPath}/login" method="post" class="stack-form">
        <label>
            E-posta
            <input type="email" name="email" value="<c:out value='${email}' />" required>
        </label>
        <label>
            Şifre
            <input type="password" name="password" required>
        </label>
        <button class="button" type="submit">Giriş yap</button>
    </form>
    <p class="muted">Hesabınız yoksa <a href="${pageContext.request.contextPath}/register">kayıt olun</a>.</p>
</section>

<%@ include file="common/footer.jspf" %>
