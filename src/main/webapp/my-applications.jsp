<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
         import="java.util.List, com.jobportal.model.Application, com.jobportal.util.HtmlUtil" %>
<%
    List<Application> applications = (List<Application>) request.getAttribute("applications");
    if (applications == null) {   // opened directly: let the servlet load the data first
        response.sendRedirect(request.getContextPath() + "/my-applications");
        return;
    }
    Integer appliedCount = (Integer) request.getAttribute("appliedCount");
    Integer shortlistedCount = (Integer) request.getAttribute("shortlistedCount");
    Integer rejectedCount = (Integer) request.getAttribute("rejectedCount");
    String ctx = request.getContextPath();

    request.setAttribute("pageTitle", "My Applications");
    request.setAttribute("extraCss", "dashboard.css");
%>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="page-head">
        <div>
            <h2 class="page-title">My applications</h2>
            <p class="page-sub">Track the jobs you have applied for.</p>
        </div>
        <a class="btn btn-primary" href="<%= ctx %>/jobs">Browse more jobs</a>
    </div>

    <div class="stats">
        <div class="card stat"><span class="num"><%= applications.size() %></span><span class="lbl">Total applications</span></div>
        <div class="card stat"><span class="num"><%= appliedCount %></span><span class="lbl">Applied</span></div>
        <div class="card stat"><span class="num"><%= shortlistedCount %></span><span class="lbl">Shortlisted</span></div>
        <div class="card stat"><span class="num"><%= rejectedCount %></span><span class="lbl">Rejected</span></div>
    </div>

    <% if (applications.isEmpty()) { %>
        <div class="card empty">
            <p>You haven't applied for any jobs yet.</p>
            <a class="btn btn-primary" href="<%= ctx %>/jobs">Find a job</a>
        </div>
    <% } else { %>
    <div class="card flush">
        <div class="table-wrap">
            <table class="tbl">
                <thead>
                    <tr>
                        <th>Job</th>
                        <th>Company</th>
                        <th>Applied on</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Application a : applications) { %>
                    <tr>
                        <td class="title">
                            <a class="action" href="<%= ctx %>/job-details?id=<%= a.getJobId() %>"><%= HtmlUtil.escape(a.getJobTitle()) %></a>
                        </td>
                        <td><%= HtmlUtil.escape(a.getCompany()) %></td>
                        <td><%= a.getAppliedOn() %></td>
                        <td><span class="<%= a.getBadgeClass() %>"><%= a.getStatusLabel() %></span></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <% } %>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>