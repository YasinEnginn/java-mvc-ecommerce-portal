<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${product.name}" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="detail-layout">
    <div class="detail-media">
        <c:choose>
            <c:when test="${not empty product.imageUrl}">
                <img src="<c:out value='${product.imageUrl}' />" alt="<c:out value='${product.name}' />">
            </c:when>
            <c:otherwise>
                <span class="image-fallback large">Ürün</span>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="detail-info">
        <a class="muted" href="${pageContext.request.contextPath}/products?categoryId=${product.categoryId}">
            <c:out value="${product.categoryName}" />
        </a>
        <h1><c:out value="${product.name}" /></h1>
        <p><c:out value="${product.description}" /></p>
        <div class="price-line"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="TL " /></div>
        <c:choose>
            <c:when test="${product.inStock}">
                <p class="stock in">Stok miktarı: <c:out value="${product.stock}" /></p>
                <form class="buy-form" action="${pageContext.request.contextPath}/cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.id}">
                    <label>
                        Adet
                        <input type="number" name="quantity" min="1" max="${product.stock}" value="1" required>
                    </label>
                    <button class="button" type="submit">Sepete ekle</button>
                </form>
            </c:when>
            <c:otherwise>
                <p class="stock out">Stokta yok</p>
                <button class="button" disabled>Sepete ekle</button>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="common/footer.jspf" %>
