package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.jobportal.dao.ApplicationDao;
import com.jobportal.dao.JobDao;
import com.jobportal.model.Application;
import com.jobportal.model.Job;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applicants")
public class ApplicantsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobDao jobDao = new JobDao();
    private final ApplicationDao applicationDao = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // AuthFilter guarantees a logged-in recruiter
        User user = (User) request.getSession(false).getAttribute("user");

        try {
            int jobId = Integer.parseInt(request.getParameter("jobId"));

            Job job = jobDao.findOwnById(jobId, user.getId());
            if (job == null) {
                // Missing, or someone else's job: same answer for both
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<Application> applications = applicationDao.findByJob(jobId, user.getId());

            int applied = 0, shortlisted = 0, rejected = 0;
            for (Application a : applications) {
                if ("SHORTLISTED".equals(a.getStatus())) {
                    shortlisted++;
                } else if ("REJECTED".equals(a.getStatus())) {
                    rejected++;
                } else {
                    applied++;
                }
            }

            request.setAttribute("job", job);
            request.setAttribute("applications", applications);
            request.setAttribute("appliedCount", applied);
            request.setAttribute("shortlistedCount", shortlisted);
            request.setAttribute("rejectedCount", rejected);

            request.getRequestDispatcher("/applicants.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}