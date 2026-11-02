package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.jobportal.dao.JobDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/delete-job")
public class DeleteJobServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobDao jobDao = new JobDao();

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
            int jobId = Integer.parseInt(request.getParameter("jobId"));
            boolean deleted = jobDao.deleteOwnJob(jobId, user.getId());
            response.sendRedirect(request.getContextPath() + "/my-jobs?deleted=" + (deleted ? "1" : "0"));

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}