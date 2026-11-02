<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setAttribute("pageTitle", "Post a Job"); %>
<% request.setAttribute("extraCss", "dashboard.css"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="page-head">
        <div>
            <h2 class="page-title">Post a job</h2>
            <p class="page-sub">Describe the role and candidates can apply right away.</p>
        </div>
    </div>

    <div class="card post-card">
        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="alert error"><%= error %></div>
        <% } %>

        <form action="post-job" method="post">
            <div class="row2">
                <div class="field">
                    <label for="title">Job Title</label>
                    <input type="text" id="title" name="title" maxlength="150" placeholder="e.g. Java Developer" required>
                </div>
                <div class="field">
                    <label for="company">Company</label>
                       <input type="text" id="company" name="company" maxlength="100"
          value="<%= com.jobportal.util.HtmlUtil.escape(navUser.getCompany()) %>" required>
                </div>
            </div>

            <div class="row2">
                <div class="field">
                    <label for="location">Location</label>
                    <input type="text" id="location" name="location" maxlength="100" placeholder="e.g. Hyderabad or Remote" required>
                </div>
                <div class="field">
                    <label for="jobType">Job Type</label>
                    <select id="jobType" name="jobType" required>
                        <option>Full-time</option>
                        <option>Part-time</option>
                        <option>Internship</option>
                        <option>Contract</option>
                    </select>
                </div>
            </div>

            <div class="field">
                <label for="salary">Salary (optional)</label>
                <input type="text" id="salary" name="salary" maxlength="100" placeholder="e.g. 3.5 - 5 LPA or 15,000 / month">
            </div>

            <div class="field">
                <label for="description">About the role</label>
                <textarea id="description" name="description" rows="5"
                          placeholder="What will the person work on?" required></textarea>
            </div>

            <div class="field">
                <label for="requirements">Requirements</label>
                <textarea id="requirements" name="requirements" rows="5"
                          placeholder="One requirement per line" required></textarea>
                <p class="hint">Write one requirement per line. Each line becomes a bullet on the job page.</p>
            </div>

            <div class="actions">
                <button type="submit" class="btn btn-primary">Post Job</button>
                <a class="btn btn-outline" href="my-jobs.jsp">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>