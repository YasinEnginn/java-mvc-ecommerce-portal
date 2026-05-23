<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Panel" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Yönetim paneli</h1>
        <p>Mağaza durumunu tek ekrandan izleyin.</p>
    </div>
</section>

<section class="stats-grid">
    <article class="stat-card">
        <span>Toplam ürün</span>
        <strong><c:out value="${stats.productCount}" /></strong>
    </article>
    <article class="stat-card">
        <span>Toplam kategori</span>
        <strong><c:out value="${stats.categoryCount}" /></strong>
    </article>
    <article class="stat-card">
        <span>Toplam kullanıcı</span>
        <strong><c:out value="${stats.userCount}" /></strong>
    </article>
    <article class="stat-card">
        <span>Toplam sipariş</span>
        <strong><c:out value="${stats.orderCount}" /></strong>
    </article>
    <article class="stat-card accent">
        <span>Bekleyen sipariş</span>
        <strong><c:out value="${stats.pendingOrderCount}" /></strong>
    </article>
</section>

<%@ include file="common/footer.jspf" %>
