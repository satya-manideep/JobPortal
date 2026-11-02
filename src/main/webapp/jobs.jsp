<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, com.jobportal.model.Job, com.jobportal.util.HtmlUtil" %>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {   // opened directly: let the servlet load the jobs first
        String qs = request.getQueryString();
        response.sendRedirect(request.getContextPath() + "/jobs" + (qs == null ? "" : "?" + qs));
        return;
    }
    String keyword  = (String) request.getAttribute("keyword");
    String location = (String) request.getAttribute("location");
    String type     = (String) request.getAttribute("type");
    String ctx = request.getContextPath();
    String[] types = { "Full-time", "Part-time", "Internship", "Contract" };

    request.setAttribute("pageTitle", "Browse Jobs");
    request.setAttribute("extraCss", "jobs.css");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <h2 class="page-title">Browse jobs</h2>
    <p class="page-sub">Find an opening that fits you and apply with your resume.</p>

    <form class="card filters" action="<%= ctx %>/jobs" method="get">
        <input type="search" name="keyword" placeholder="Job title or keyword" maxlength="100"
               value="<%= HtmlUtil.escape(keyword) %>">
        <input type="text" name="location" placeholder="Location" maxlength="100"
               value="<%= HtmlUtil.escape(location) %>">
        <select name="type">
            <option value="">Any type</option>
            <% for (String t : types) { %>
                <option <%= t.equals(type) ? "selected" : "" %>><%= t %></option>
            <% } %>
        </select>
        <button type="submit" class="btn btn-primary">Search</button>
    </form>

    <p class="count"><%= jobs.size() %> <%= jobs.size() == 1 ? "job" : "jobs" %> found</p>

    <% if (jobs.isEmpty()) { %>
        <div class="card empty">
            <p>No jobs match your search. Try different words or clear the filters.</p>
            <a class="btn btn-outline" href="<%= ctx %>/jobs">Show all jobs</a>
        </div>
    <% } else { %>
    <div class="grid">
        <% for (Job job : jobs) { %>
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
            <p class="posted">Posted <%= job.getPostedOn() %></p>
        </article>
        <% } %>
    </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>