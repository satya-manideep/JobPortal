<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, com.jobportal.model.Job, com.jobportal.util.HtmlUtil" %>
<%
    List<Job> jobs = (List<Job>) request.getAttribute("jobs");
    if (jobs == null) {   // opened directly: let the servlet load the data first
        response.sendRedirect(request.getContextPath() + "/my-jobs");
        return;
    }
    Integer totalApplicants = (Integer) request.getAttribute("totalApplicants");
    Integer shortlisted = (Integer) request.getAttribute("shortlisted");
    request.setAttribute("pageTitle", "My Jobs");
    request.setAttribute("extraCss", "dashboard.css");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="page-head">
        <div>
            <h2 class="page-title">My job postings</h2>
            <p class="page-sub">Manage your openings and review who has applied.</p>
        </div>
        <a class="btn btn-primary" href="post-job.jsp">Post a new job</a>
    </div>

    <% if ("1".equals(request.getParameter("posted"))) { %>
        <div class="alert success">Job posted successfully.</div>
    <% } %>
    <% if ("1".equals(request.getParameter("deleted"))) { %>
        <div class="alert success">Job deleted.</div>
    <% } else if ("0".equals(request.getParameter("deleted"))) { %>
        <div class="alert error">That job could not be deleted.</div>
    <% } %>

    <div class="stats">
        <div class="card stat"><span class="num"><%= jobs.size() %></span><span class="lbl">Jobs posted</span></div>
        <div class="card stat"><span class="num"><%= totalApplicants %></span><span class="lbl">Total applicants</span></div>
        <div class="card stat"><span class="num"><%= shortlisted %></span><span class="lbl">Shortlisted</span></div>
    </div>

    <% if (jobs.isEmpty()) { %>
        <div class="card empty">
            <p>You haven't posted any jobs yet.</p>
            <a class="btn btn-primary" href="post-job.jsp">Post your first job</a>
        </div>
    <% } else { %>
    <div class="card flush">
        <div class="table-wrap">
            <table class="tbl">
                <thead>
                    <tr>
                        <th>Job</th>
                        <th>Location</th>
                        <th>Type</th>
                        <th>Posted on</th>
                        <th>Applicants</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Job job : jobs) { %>
                    <tr>
                        <td class="title"><%= HtmlUtil.escape(job.getTitle()) %></td>
                        <td><%= HtmlUtil.escape(job.getLocation()) %></td>
                        <td><span class="badge"><%= HtmlUtil.escape(job.getJobType()) %></span></td>
                        <td><%= job.getPostedOn() %></td>
                        <td>
                            <span class="badge <%= job.getApplicantCount() == 0 ? "warn" : "ok" %>">
                                <%= job.getApplicantCount() %> <%= job.getApplicantCount() == 1 ? "applicant" : "applicants" %>
                            </span>
                        </td>
                        <td>
                            <div class="row-actions">
                                <a class="btn btn-outline btn-sm" href="applicants?jobId=<%= job.getId() %>">View applicants</a>
                                <form action="delete-job" method="post"
                                      onsubmit="return confirm('Delete this job and all its applications?');">
                                    <input type="hidden" name="jobId" value="<%= job.getId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>