<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Register"); %>
<% request.setAttribute("extraCss", "auth.css"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="card form-card wide">
        <h2>Create your account</h2>
        <p class="page-sub">Join as a job seeker or a recruiter</p>

        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="alert error"><%= error %></div>
        <% } %>

        <form id="regForm" action="register" method="post">
            <div class="field">
                <label>I am a</label>
                <div class="choice">
                    <label><input type="radio" name="role" value="SEEKER" checked> Job Seeker</label>
                    <label><input type="radio" name="role" value="RECRUITER"> Recruiter</label>
                </div>
            </div>

            <div class="row2">
                <div class="field">
                    <label for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" required>
                </div>
                <div class="field">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" required>
                </div>
            </div>

            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="field" id="companyField" style="display:none;">
                <label for="company">Company Name</label>
                <input type="text" id="company" name="company">
            </div>

            <div class="row2">
                <div class="field">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <div class="field">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Create Account</button>
        </form>

        <p class="form-foot">Already have an account? <a href="login.jsp">Login</a></p>
    </div>
</div>

<script>
    var form = document.getElementById("regForm");
    var companyField = document.getElementById("companyField");
    var companyInput = document.getElementById("company");

    // Company name is only needed for recruiters
    function toggleCompany() {
        var isRecruiter = form.role.value === "RECRUITER";
        companyField.style.display = isRecruiter ? "block" : "none";
        companyInput.required = isRecruiter;
    }

    form.addEventListener("change", function (e) {
        if (e.target.name === "role") { toggleCompany(); }
    });

    form.addEventListener("submit", function (e) {
        if (form.password.value !== form.confirmPassword.value) {
            e.preventDefault();
            alert("Passwords do not match.");
        }
    });

    toggleCompany();
</script>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>