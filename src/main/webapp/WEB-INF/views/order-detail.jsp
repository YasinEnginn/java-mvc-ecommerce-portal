<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Sipariş Detayı" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Sipariş #<c:out value="${order.id}" /></h1>
        <p><fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy HH:mm" /></p>
    </div>
    <c:choose>
        <c:when test="${order.status == 'Beklemede'}"><span class="status pending">Beklemede</span></c:when>
        <c:when test="${order.status == 'İptal Edildi'}"><span class="status cancelled">İptal Edildi</span></c:when>
        <c:otherwise><span class="status active"><c:out value="${order.status}" /></span></c:otherwise>
    </c:choose>
</section>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>Ürün</th>
            <th>Adet</th>
            <th>Birim fiyat</th>
            <th>Ara toplam</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${order.items}">
            <tr>
                <td><c:out value="${item.productName}" /></td>
                <td><c:out value="${item.quantity}" /></td>
                <td><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="TL " /></td>
                <td><fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="TL " /></td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
        <tr>
            <th colspan="3">Genel toplam</th>
            <th><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="TL " /></th>
        </tr>
        </tfoot>
    </table>
</div>

<%@ include file="common/footer.jspf" %>
