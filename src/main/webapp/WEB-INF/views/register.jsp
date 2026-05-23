<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Kayıt" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="form-panel">
    <h1>Kayıt ol</h1>
    <c:if test="${not empty errors}">
        <div class="alert error">
            <c:forEach var="message" items="${errors}">
                <div><c:out value="${message}" /></div>
            </c:forEach>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/register" method="post" class="stack-form two-col">
        <label>
            Ad soyad
            <input type="text" name="fullName" value="<c:out value='${formUser.fullName}' />" required>
        </label>
        <label>
            E-posta
            <input type="email" name="email" value="<c:out value='${formUser.email}' />" required>
        </label>
        <label>
            Şifre
            <input type="password" name="password" minlength="6" required>
        </label>
        <label>
            Telefon
            <input type="tel" name="phone" value="<c:out value='${formUser.phone}' />" required>
        </label>
        <label class="span-2">
            Adres
            <textarea name="address" rows="4" required><c:out value="${formUser.address}" /></textarea>
        </label>
        <button class="button" type="submit">Kayıt ol</button>
    </form>
</section>

<%@ include file="common/footer.jspf" %>
