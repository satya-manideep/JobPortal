package com.jobportal.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import com.jobportal.dao.ApplicationDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applicant-resume")
public class ApplicantResumeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ApplicationDao applicationDao = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession(false).getAttribute("user");

        try {
            int applicationId = Integer.parseInt(request.getParameter("id"));
            String name = applicationDao.findResumeNameForRecruiter(applicationId, user.getId());

            if (name == null) {
                // Not found, or not for one of this recruiter's jobs: same answer for both
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String safeName = name.replaceAll("[^A-Za-z0-9._ -]", "_");
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");

            response.setContentType("application/pdf");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + safeName + "\"; filename*=UTF-8''" + encodedName);

            applicationDao.copyResumeForRecruiter(applicationId, user.getId(), response.getOutputStream());

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}