package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import com.jobportal.model.User;
import com.jobportal.dao.ApplicationDao;
import com.jobportal.dao.JobDao;
import com.jobportal.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/job-details")
public class JobDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final JobDao jobDao = new JobDao();
    private final ApplicationDao applicationDao = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Job job = null;
            try {
                job = jobDao.findById(Integer.parseInt(request.getParameter("id")));
            } catch (NumberFormatException e) {
                // a missing or non-numeric id is treated as "not found"
            }
            

            if (job == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("notFound", true);
            } else {
                request.setAttribute("job", job);
                
                HttpSession session = request.getSession(false);
                User user = (session == null) ? null : (User) session.getAttribute("user");
                if (user != null && user.isSeeker()
                        && applicationDao.hasApplied(job.getId(), user.getId())) {
                    request.setAttribute("alreadyApplied", Boolean.TRUE);
                }
            }
            request.getRequestDispatcher("/job-details.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}