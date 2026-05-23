<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Kategoriler" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Kategoriler</h1>
        <p>Kategori ekleyin, güncelleyin veya pasife alın.</p>
    </div>
    <a class="button" href="${pageContext.request.contextPath}/admin/categories?action=new">Yeni kategori</a>
</section>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Kategori adı</th>
            <th>Açıklama</th>
            <th>Durum</th>
            <th>İşlem</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="category" items="${categories}">
            <tr>
                <td><c:out value="${category.id}" /></td>
                <td><strong><c:out value="${category.name}" /></strong></td>
                <td><c:out value="${category.description}" /></td>
                <td>
                    <c:choose>
                        <c:when test="${category.active}"><span class="status active">Aktif</span></c:when>
                        <c:otherwise><span class="status cancelled">Pasif</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a class="button small ghost" href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${category.id}">Düzenle</a>
                    <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="${category.id}">
                        <input type="hidden" name="active" value="${!category.active}">
                        <button class="button small ghost" type="submit">${category.active ? 'Pasif yap' : 'Aktif yap'}</button>
                    </form>
                    <form action="${pageContext.request.contextPath}/admin/categories" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${category.id}">
                        <button class="button small danger" type="submit">Sil</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="common/footer.jspf" %>
