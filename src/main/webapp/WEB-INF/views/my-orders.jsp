<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Siparişlerim" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Siparişlerim</h1>
        <p>Oluşturduğunuz siparişleri ve güncel durumlarını buradan takip edebilirsiniz.</p>
    </div>
</section>

<c:choose>
    <c:when test="${empty orders}">
        <div class="empty-state">
            <h2>Henüz siparişiniz yok</h2>
            <a class="button" href="${pageContext.request.contextPath}/products">Ürünlere git</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Sipariş no</th>
                    <th>Tarih</th>
                    <th>Toplam</th>
                    <th>Durum</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>#<c:out value="${order.id}" /></td>
                        <td><fmt:formatDate value="${order.orderDate}" pattern="dd.MM.yyyy HH:mm" /></td>
                        <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="TL " /></td>
                        <td>
                            <c:choose>
                                <c:when test="${order.status == 'Beklemede'}"><span class="status pending">Beklemede</span></c:when>
                                <c:when test="${order.status == 'İptal Edildi'}"><span class="status cancelled">İptal Edildi</span></c:when>
                                <c:otherwise><span class="status active"><c:out value="${order.status}" /></span></c:otherwise>
                            </c:choose>
                        </td>
                        <td><a class="button small ghost" href="${pageContext.request.contextPath}/order-detail?id=${order.id}">Detay</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
