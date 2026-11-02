<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Login"); %>
<% request.setAttribute("extraCss", "auth.css"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="card form-card">
        <h2>Welcome back</h2>
        <p class="page-sub">Login to your account</p>

        <% if ("1".equals(request.getParameter("registered"))) { %>
            <div class="alert success">Account created. Please log in.</div>
        <% } %>
        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="alert error"><%= error %></div>
        <% } %>

        <form action="login" method="post">
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Login</button>
        </form>

        <p class="form-foot">New here? <a href="register.jsp">Create an account</a></p>
        <% if ("1".equals(request.getParameter("required"))) { %>
    <div class="alert error">Please log in to continue.</div>
<% } %>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>