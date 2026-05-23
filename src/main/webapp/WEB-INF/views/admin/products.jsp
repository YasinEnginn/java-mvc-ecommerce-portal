<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Ürünler" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Ürünler</h1>
        <p>Fiyat, stok, kategori ve aktiflik durumlarını yönetin.</p>
    </div>
    <a class="button" href="${pageContext.request.contextPath}/admin/products?action=new">Yeni ürün</a>
</section>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Ürün</th>
            <th>Kategori</th>
            <th>Fiyat</th>
            <th>Stok</th>
            <th>Durum</th>
            <th>İşlem</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="product" items="${products}">
            <tr>
                <td><c:out value="${product.id}" /></td>
                <td><strong><c:out value="${product.name}" /></strong></td>
                <td><c:out value="${product.categoryName}" /></td>
                <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="TL " /></td>
                <td><c:out value="${product.stock}" /></td>
                <td>
                    <c:choose>
                        <c:when test="${product.active}"><span class="status active">Aktif</span></c:when>
                        <c:otherwise><span class="status cancelled">Pasif</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a class="button small ghost" href="${pageContext.request.contextPath}/admin/products?action=edit&id=${product.id}">Düzenle</a>
                    <form action="${pageContext.request.contextPath}/admin/products" method="post">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="${product.id}">
                        <input type="hidden" name="active" value="${!product.active}">
                        <button class="button small ghost" type="submit">${product.active ? 'Pasif yap' : 'Aktif yap'}</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/products" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${product.id}">
                        <button class="button small danger" type="submit">Sil</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="common/footer.jspf" %>
