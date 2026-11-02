<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, com.jobportal.model.Job, com.jobportal.util.HtmlUtil" %>
<%
    List<Job> latest = (List<Job>) request.getAttribute("latestJobs");
    if (latest == null) {   // opened directly: let the servlet load the jobs first
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
    String ctx = request.getContextPath();
    request.setAttribute("pageTitle", "Home");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<section class="hero">
    <h1>Find your next job</h1>
    <p>Search openings from recruiters and apply with your resume.</p>
    <form class="search" action="<%= ctx %>/jobs" method="get">
        <input type="search" name="keyword" placeholder="Job title or keyword" maxlength="100">
        <input type="text" name="location" placeholder="Location" maxlength="100">
        <select name="type">
            <option value="">Any type</option>
            <option>Full-time</option>
            <option>Part-time</option>
            <option>Internship</option>
            <option>Contract</option>
        </select>
        <button type="submit" class="btn btn-primary">Search</button>
    </form>
</section>

<div class="container">
    <h2 class="page-title">Latest jobs</h2>
    <p class="page-sub">The newest openings from recruiters.</p>

    <% if (latest.isEmpty()) { %>
        <div class="card empty"><p>No jobs have been posted yet. Please check back soon.</p></div>
    <% } else { %>
    <div class="grid">
        <% for (Job job : latest) { %>
        <article class="card job">
            <h3><%= HtmlUtil.escape(job.getTitle()) %></h3>
            <p class="company"><%= HtmlUtil.escape(job.getCompany()) %></p>
            <div class="meta">
                <span class="badge"><%= HtmlUtil.escape(job.getLocation()) %></span>
                <span class="badge"><%= HtmlUtil.escape(job.getJobType()) %></span>
                <% if (job.getSalary() != null && !job.getSalary().isEmpty()) { %>
                    <span class="badge ok"><%= HtmlUtil.escape(job.getSalary()) %></span>
                <% } %>
            </div>
            <p class="desc"><%= HtmlUtil.escape(job.getSummary()) %></p>
            <a class="btn btn-outline" href="<%= ctx %>/job-details?id=<%= job.getId() %>">View Details</a>
        </article>
        <% } %>
    </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>