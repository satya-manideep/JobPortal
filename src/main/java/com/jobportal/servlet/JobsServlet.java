package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.jobportal.dao.JobDao;
import com.jobportal.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Set<String> TYPES =
            Set.of("Full-time", "Part-time", "Internship", "Contract");

    private final JobDao jobDao = new JobDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword  = clean(request.getParameter("keyword"), 100);
        String location = clean(request.getParameter("location"), 100);
        String type     = clean(request.getParameter("type"), 30);
        if (!TYPES.contains(type)) {
            type = "";
        }

        try {
            List<Job> jobs = jobDao.search(keyword, location, type);

            request.setAttribute("jobs", jobs);
            request.setAttribute("keyword", keyword);
            request.setAttribute("location", location);
            request.setAttribute("type", type);

            request.getRequestDispatcher("/jobs.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String clean(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}