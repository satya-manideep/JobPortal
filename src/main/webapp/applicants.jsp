<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, com.jobportal.model.Job, com.jobportal.model.Application, com.jobportal.util.HtmlUtil" %>
<%
    Job job = (Job) request.getAttribute("job");
    String ctx = request.getContextPath();
    if (job == null) {   // opened directly: let the servlet load the data first
        String qs = request.getQueryString();
        response.sendRedirect(ctx + "/applicants" + (qs == null ? "" : "?" + qs));
        return;
    }
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    Integer appliedCount = (Integer) request.getAttribute("appliedCount");
    Integer shortlistedCount = (Integer) request.getAttribute("shortlistedCount");
    Integer rejectedCount = (Integer) request.getAttribute("rejectedCount");

    String[] statusValues = { "APPLIED", "SHORTLISTED", "REJECTED" };
    String[] statusLabels = { "Applied", "Shortlisted", "Rejected" };

    request.setAttribute("pageTitle", "Applicants");
    request.setAttribute("extraCss", "dashboard.css");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <a class="back" href="<%= ctx %>/my-jobs">&larr; Back to my jobs</a>

    <div class="page-head">
        <div>
            <h2 class="page-title">Applicants for <%= HtmlUtil.escape(job.getTitle()) %></h2>
            <p class="page-sub">
                <%= HtmlUtil.escape(job.getCompany()) %> &middot;
                <%= HtmlUtil.escape(job.getLocation()) %> &middot;
                <%= HtmlUtil.escape(job.getJobType()) %>
            </p>
        </div>
    </div>

    <% if ("1".equals(request.getParameter("updated"))) { %>
        <div class="alert success">Status updated.</div>
    <% } else if ("0".equals(request.getParameter("updated"))) { %>
        <div class="alert error">That application could not be updated.</div>
    <% } %>

    <div class="stats">
        <div class="card stat"><span class="num"><%= applications.size() %></span><span class="lbl">Total applicants</span></div>
        <div class="card stat"><span class="num"><%= appliedCount %></span><span class="lbl">Applied</span></div>
        <div class="card stat"><span class="num"><%= shortlistedCount %></span><span class="lbl">Shortlisted</span></div>
        <div class="card stat"><span class="num"><%= rejectedCount %></span><span class="lbl">Rejected</span></div>
    </div>

    <% if (applications.isEmpty()) { %>
        <div class="card empty">
            <p>No one has applied for this job yet.</p>
        </div>
    <% } else { %>
    <div class="card flush">
        <div class="table-wrap">
            <table class="tbl">
                <thead>
                    <tr>
                        <th>Candidate</th>
                        <th>Phone</th>
                        <th>Applied on</th>
                        <th>Cover note</th>
                        <th>Resume</th>
                        <th>Status</th>
                        <th>Change status</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Application a : applications) { %>
                    <tr>
                        <td class="person">
                            <b><%= HtmlUtil.escape(a.getSeekerName()) %></b>
                            <small><%= HtmlUtil.escape(a.getSeekerEmail()) %></small>
                        </td>
                        <td><%= HtmlUtil.escape(a.getSeekerPhone()) %></td>
                        <td><%= a.getAppliedOn() %></td>
                        <td class="note">
                            <%= (a.getCoverNote() == null || a.getCoverNote().isEmpty())
                                    ? "&mdash;" : HtmlUtil.escapeWithBreaks(a.getCoverNote()) %>
                        </td>
                        <td><a class="action" href="<%= ctx %>/applicant-resume?id=<%= a.getId() %>">Download</a></td>
                        <td><span class="<%= a.getBadgeClass() %>"><%= a.getStatusLabel() %></span></td>
                        <td>
                            <form class="status-form" action="<%= ctx %>/update-status" method="post">
                                <input type="hidden" name="applicationId" value="<%= a.getId() %>">
                                <input type="hidden" name="jobId" value="<%= job.getId() %>">
                                <select name="status">
                                    <% for (int i = 0; i < statusValues.length; i++) { %>
                                        <option value="<%= statusValues[i] %>"
                                            <%= statusValues[i].equals(a.getStatus()) ? "selected" : "" %>><%= statusLabels[i] %></option>
                                    <% } %>
                                </select>
                                <button type="submit" class="btn btn-outline btn-sm">Update</button>
                            </form>
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