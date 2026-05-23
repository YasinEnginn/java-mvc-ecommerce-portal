<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Kullanıcılar" scope="request" />
<%@ include file="common/header.jspf" %>

<section class="toolbar">
    <div>
        <h1>Kullanıcılar</h1>
        <p>Kayıtlı kullanıcıları ve rollerini görüntüleyin.</p>
    </div>
</section>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Ad soyad</th>
            <th>E-posta</th>
            <th>Telefon</th>
            <th>Rol</th>
            <th>Kayıt tarihi</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td><c:out value="${user.id}" /></td>
                <td><strong><c:out value="${user.fullName}" /></strong></td>
                <td><c:out value="${user.email}" /></td>
                <td><c:out value="${user.phone}" /></td>
                <td><span class="status active"><c:out value="${user.role}" /></span></td>
                <td><fmt:formatDate value="${user.createdAt}" pattern="dd.MM.yyyy HH:mm" /></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

<%@ include file="common/footer.jspf" %>
