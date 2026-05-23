<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Kategori Formu" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>${category.id > 0 ? 'Kategori düzenle' : 'Yeni kategori'}</h1>
    </div>
</section>

<section class="form-panel">
    <c:if test="${not empty errors}">
        <div class="alert error">
            <c:forEach var="message" items="${errors}">
                <div><c:out value="${message}" /></div>
            </c:forEach>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="stack-form">
        <input type="hidden" name="id" value="${category.id}">
        <label>
            Kategori adı
            <input type="text" name="name" value="<c:out value='${category.name}' />" required>
        </label>
        <label>
            Açıklama
            <textarea name="description" rows="4"><c:out value="${category.description}" /></textarea>
        </label>
        <label class="check-row">
            <input type="checkbox" name="active" <c:if test="${category.active}">checked</c:if>>
            Aktif
        </label>
        <div class="form-actions">
            <button class="button" type="submit">Kaydet</button>
            <a class="button ghost" href="${pageContext.request.contextPath}/admin/categories">Vazgeç</a>
        </div>
    </form>
</section>

<%@ include file="common/footer.jspf" %>
