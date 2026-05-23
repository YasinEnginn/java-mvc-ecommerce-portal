<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Sepet" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Sepet</h1>
        <p>Adetleri stok miktarını aşmayacak şekilde güncelleyebilirsiniz.</p>
    </div>
</section>

<c:choose>
    <c:when test="${empty cart.items}">
        <div class="empty-state">
            <h2>Sepetiniz boş</h2>
            <a class="button" href="${pageContext.request.contextPath}/products">Alışverişe başla</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Ürün</th>
                    <th>Birim fiyat</th>
                    <th>Adet</th>
                    <th>Ara toplam</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${cart.items}">
                    <tr>
                        <td>
                            <strong><c:out value="${item.product.name}" /></strong>
                            <span class="muted block">Stok: <c:out value="${item.product.stock}" /></span>
                        </td>
                        <td><fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="TL " /></td>
                        <td>
                            <form class="quantity-form" action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="productId" value="${item.product.id}">
                                <input type="number" name="quantity" min="0" max="${item.product.stock}" value="${item.quantity}" required>
                                <button type="submit" class="button small">Güncelle</button>
                            </form>
                        </td>
                        <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="TL " /></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="${item.product.id}">
                                <button type="submit" class="button danger small">Çıkar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <section class="cart-summary">
            <form action="${pageContext.request.contextPath}/cart" method="post">
                <input type="hidden" name="action" value="clear">
                <button class="button ghost" type="submit">Sepeti temizle</button>
            </form>
            <div>
                <span>Genel toplam</span>
                <strong><fmt:formatNumber value="${cart.totalAmount}" type="currency" currencySymbol="TL " /></strong>
            </div>
            <form action="${pageContext.request.contextPath}/checkout" method="post">
                <button class="button" type="submit">Sipariş oluştur</button>
            </form>
        </section>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
