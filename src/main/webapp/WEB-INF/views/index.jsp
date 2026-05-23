<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Ürünler" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Ürünler</h1>
        <p>Aktif ürünleri inceleyin, kategoriye göre filtreleyin ve sepete ekleyin.</p>
    </div>
    <a class="button ghost" href="${pageContext.request.contextPath}/products">Tüm ürünler</a>
</section>

<section class="category-strip">
    <a class="${selectedCategoryId == 0 ? 'active' : ''}" href="${pageContext.request.contextPath}/products">Tüm kategoriler</a>
    <c:forEach var="category" items="${categories}">
        <a class="${selectedCategoryId == category.id ? 'active' : ''}"
           href="${pageContext.request.contextPath}/products?categoryId=${category.id}">
            <c:out value="${category.name}" />
        </a>
    </c:forEach>
</section>

<c:choose>
    <c:when test="${empty products}">
        <div class="empty-state">
            <h2>Ürün bulunamadı</h2>
            <p>Filtreyi temizleyerek tekrar deneyebilirsiniz.</p>
        </div>
    </c:when>
    <c:otherwise>
        <section class="product-grid">
            <c:forEach var="product" items="${products}">
                <article class="product-card">
                    <a class="product-media" href="${pageContext.request.contextPath}/product?id=${product.id}">
                        <c:choose>
                            <c:when test="${not empty product.imageUrl}">
                                <img src="<c:out value='${product.imageUrl}' />" alt="<c:out value='${product.name}' />">
                            </c:when>
                            <c:otherwise>
                                <span class="image-fallback">Ürün</span>
                            </c:otherwise>
                        </c:choose>
                    </a>
                    <div class="product-body">
                        <span class="muted"><c:out value="${product.categoryName}" /></span>
                        <h2><c:out value="${product.name}" /></h2>
                        <p><c:out value="${product.shortDescription}" /></p>
                        <div class="product-meta">
                            <strong><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="TL " /></strong>
                            <c:choose>
                                <c:when test="${product.inStock}">
                                    <span class="stock in">Stokta: <c:out value="${product.stock}" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="stock out">Stokta yok</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="product-actions">
                        <a class="button ghost" href="${pageContext.request.contextPath}/product?id=${product.id}">Detay</a>
                        <form action="${pageContext.request.contextPath}/cart" method="post">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="${product.id}">
                            <input type="hidden" name="quantity" value="1">
                            <button type="submit" class="button" <c:if test="${!product.inStock}">disabled</c:if>>Sepete ekle</button>
                        </form>
                    </div>
                </article>
            </c:forEach>
        </section>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
