<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Ürün Formu" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>${product.id > 0 ? 'Ürün düzenle' : 'Yeni ürün'}</h1>
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
    <form action="${pageContext.request.contextPath}/admin/products" method="post" class="stack-form two-col">
        <input type="hidden" name="id" value="${product.id}">
        <label>
            Ürün adı
            <input type="text" name="name" value="<c:out value='${product.name}' />" required>
        </label>
        <label>
            Kategori
            <select name="categoryId" required>
                <option value="">Seçiniz</option>
                <c:forEach var="category" items="${categories}">
                    <option value="${category.id}" <c:if test="${category.id == product.categoryId}">selected</c:if>>
                        <c:out value="${category.name}" />
                    </option>
                </c:forEach>
            </select>
        </label>
        <label>
            Fiyat
            <input type="number" step="0.01" min="0.01" name="price" value="${product.price}" required>
        </label>
        <label>
            Stok miktarı
            <input type="number" min="0" name="stock" value="${product.stock}" required>
        </label>
        <label class="span-2">
            Ürün görsel yolu
            <input type="url" name="imageUrl" value="<c:out value='${product.imageUrl}' />" placeholder="https://...">
        </label>
        <label class="span-2">
            Açıklama
            <textarea name="description" rows="5"><c:out value="${product.description}" /></textarea>
        </label>
        <label class="check-row">
            <input type="checkbox" name="active" <c:if test="${product.active}">checked</c:if>>
            Aktif
        </label>
        <div class="form-actions span-2">
            <button class="button" type="submit">Kaydet</button>
            <a class="button ghost" href="${pageContext.request.contextPath}/admin/products">Vazgeç</a>
        </div>
    </form>
</section>

<%@ include file="common/footer.jspf" %>
