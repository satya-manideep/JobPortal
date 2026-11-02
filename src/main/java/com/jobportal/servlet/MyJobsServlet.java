package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.jobportal.dao.JobDao;
import com.jobportal.model.Job;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/my-jobs")
public class MyJobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobDao jobDao = new JobDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession(false).getAttribute("user");

        try {
            List<Job> jobs = jobDao.findByRecruiter(user.getId());

            int totalApplicants = 0;
            for (Job job : jobs) {
                totalApplicants += job.getApplicantCount();
            }

            request.setAttribute("jobs", jobs);
            request.setAttribute("totalApplicants", totalApplicants);
            request.setAttribute("shortlisted", jobDao.countShortlisted(user.getId()));

            request.getRequestDispatcher("/my-jobs.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}