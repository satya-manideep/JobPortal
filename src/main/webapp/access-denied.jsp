<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Access Denied"); %>
<% request.setAttribute("extraCss", "dashboard.css"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="card empty">
        <h2 class="page-title">Access denied</h2>
        <p class="page-sub">Your account type doesn't have access to this page.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/index.jsp">Go to home</a>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>