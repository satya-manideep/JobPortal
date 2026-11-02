package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import com.jobportal.dao.ApplicationDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/update-status")
public class UpdateStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Set<String> STATUSES = Set.of("APPLIED", "SHORTLISTED", "REJECTED");

    private final ApplicationDao applicationDao = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/my-jobs");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession(false).getAttribute("user");

        try {
            int applicationId = Integer.parseInt(request.getParameter("applicationId"));
            int jobId = Integer.parseInt(request.getParameter("jobId"));   // only used for the redirect
            String status = request.getParameter("status");

            if (!STATUSES.contains(status)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            boolean updated = applicationDao.updateStatus(applicationId, user.getId(), status);

            response.sendRedirect(request.getContextPath()
                    + "/applicants?jobId=" + jobId + "&updated=" + (updated ? "1" : "0"));

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}