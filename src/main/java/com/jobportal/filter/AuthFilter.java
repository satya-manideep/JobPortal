package com.jobportal.filter;

import java.io.IOException;
import java.util.Set;

import com.jobportal.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {
        "/post-job.jsp", "/my-jobs.jsp", "/my-jobs","/applicants", "/applicants.jsp",
        "/post-job", "/delete-job", "/update-status", "/applicant-resume",
        "/my-applications.jsp", "/apply", "/my-resume ", "/my-applications" 
})
public class AuthFilter implements Filter {

    private static final Set<String> RECRUITER_ONLY = Set.of(
            "/post-job.jsp", "/my-jobs.jsp","/my-jobs", "/applicants.jsp",
            "/post-job", "/delete-job", "/update-status", "/applicants", "/applicant-resume");

    private static final Set<String> SEEKER_ONLY = Set.of(
            "/my-applications.jsp", "/apply", "/my-resume","/my-applications");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Protected pages must not be kept in the browser cache,
        // otherwise the Back button shows them again after logout
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");

        // Not logged in: send to the login page
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?required=1");
            return;
        }

        // Logged in, but is this page meant for this role?
        String path = request.getServletPath();
        boolean allowed = (RECRUITER_ONLY.contains(path) && user.isRecruiter())
                       || (SEEKER_ONLY.contains(path) && user.isSeeker());

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/access-denied.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}