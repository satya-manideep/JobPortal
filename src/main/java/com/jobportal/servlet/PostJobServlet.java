package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import com.jobportal.dao.JobDao;
import com.jobportal.model.Job;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post-job")
public class PostJobServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Set<String> JOB_TYPES =
            Set.of("Full-time", "Part-time", "Internship", "Contract");

    private final JobDao jobDao = new JobDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/post-job.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // AuthFilter guarantees a logged-in recruiter
        User user = (User) request.getSession(false).getAttribute("user");

        String title        = clean(request.getParameter("title"));
        String company      = clean(request.getParameter("company"));
        String location     = clean(request.getParameter("location"));
        String jobType      = clean(request.getParameter("jobType"));
        String salary       = clean(request.getParameter("salary"));
        String description  = clean(request.getParameter("description"));
        String requirements = clean(request.getParameter("requirements"));

        String error = null;
        if (title.isEmpty() || company.isEmpty() || location.isEmpty()
                || description.isEmpty() || requirements.isEmpty()) {
            error = "Please fill in all the required fields.";
        } else if (!JOB_TYPES.contains(jobType)) {
            error = "Please choose a valid job type.";
        } else if (title.length() > 150 || company.length() > 100
                || location.length() > 100 || salary.length() > 50) {
            error = "One of the fields is too long.";
        }

        if (error == null) {
            try {
                Job job = new Job();
                job.setRecruiterId(user.getId());     // from the session, never from the form
                job.setTitle(title);
                job.setCompany(company);
                job.setLocation(location);
                job.setJobType(jobType);
                job.setSalary(salary.isEmpty() ? null : salary);
                job.setDescription(description);
                job.setRequirements(requirements);

                jobDao.insert(job);

                response.sendRedirect(request.getContextPath() + "/my-jobs?posted=1");
                return;

            } catch (SQLException e) {
                e.printStackTrace();
                error = "Something went wrong on the server. Please try again.";
            }
        }

        request.setAttribute("error", error);
        request.getRequestDispatcher("/post-job.jsp").forward(request, response);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}