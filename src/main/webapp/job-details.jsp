<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.net.URLEncoder, java.nio.charset.StandardCharsets, com.jobportal.model.Job, com.jobportal.util.HtmlUtil" %>
<%
    Job job = (Job) request.getAttribute("job");
    boolean notFound = request.getAttribute("notFound") != null;
    if (job == null && !notFound) {   // opened directly: let the servlet load the job first
        String id = request.getParameter("id");
        response.sendRedirect(request.getContextPath() + "/job-details"
                + (id == null ? "" : "?id=" + URLEncoder.encode(id, StandardCharsets.UTF_8)));
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "Job Details");
    request.setAttribute("extraCss", "jobs.css");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <a class="back" href="<%= ctx %>/jobs">&larr; Back to all jobs</a>
     
    <% if (notFound) { %>
        <div class="card empty">
            <h2 class="page-title">Job not found</h2>
            <p>This job is no longer available.</p>
            <a class="btn btn-primary" href="<%= ctx %>/jobs">Browse jobs</a>
        </div>
    <% } else { %>
    <div class="detail">
        <section class="card info">
            <h1><%= HtmlUtil.escape(job.getTitle()) %></h1>
            <p class="company"><%= HtmlUtil.escape(job.getCompany()) %></p>
            <div class="meta">
                <span class="badge"><%= HtmlUtil.escape(job.getLocation()) %></span>
                <span class="badge"><%= HtmlUtil.escape(job.getJobType()) %></span>
                <% if (job.getSalary() != null && !job.getSalary().isEmpty()) { %>
                    <span class="badge ok"><%= HtmlUtil.escape(job.getSalary()) %></span>
                <% } %>
            </div>

            <h3>About the role</h3>
            <p><%= HtmlUtil.escapeWithBreaks(job.getDescription()) %></p>

            <h3>Requirements</h3>
            <ul>
                <% for (String line : job.getRequirementList()) { %>
                    <li><%= HtmlUtil.escape(line) %></li>
                <% } %>
            </ul>

            <p class="posted">Posted <%= job.getPostedOn() %></p>
        </section>

               <aside class="card apply">
            <% if (navUser == null) { %>
                <h3>Apply for this job</h3>
                <p class="page-sub">Log in or create a free account to apply.</p>
                <a class="btn btn-primary btn-block" href="<%= ctx %>/login.jsp">Login to apply</a>
                <p class="hint">New here? <a href="<%= ctx %>/register.jsp">Create an account</a></p>

            <% } else if (navUser.isRecruiter()) { %>
                <h3>Recruiter account</h3>
                <p class="page-sub">Recruiter accounts can't apply for jobs. Log in as a job seeker to apply.</p>

            <% } else if (Boolean.TRUE.equals(request.getAttribute("alreadyApplied"))) { %>
                <h3>Application sent</h3>
                <p class="page-sub">You have already applied for this job.</p>
                <a class="btn btn-outline btn-block" href="<%= ctx %>/my-applications.jsp">View my applications</a>

            <% } else { %>
                <h3>Apply for this job</h3>
                <p class="page-sub">Upload your resume to apply.</p>

                <form action="<%= ctx %>/apply" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="jobId" value="<%= job.getId() %>">

                    <div class="field">
                        <label for="resume">Resume (PDF, up to 5 MB)</label>
                        <input type="file" id="resume" name="resume" accept=".pdf,application/pdf" required>
                    </div>

                    <div class="field">
                        <label for="coverNote">Cover note (optional)</label>
                        <textarea id="coverNote" name="coverNote" rows="4" maxlength="500"
                                  placeholder="Tell the recruiter why you are a good fit"></textarea>
                        <p class="hint">Up to 500 characters</p>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">Apply Now</button>
                </form>
            <% } %>
                </aside>
    </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>