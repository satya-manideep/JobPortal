package com.jobportal.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import com.jobportal.dao.ApplicationDao;
import com.jobportal.dao.JobDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/apply")
@MultipartConfig(maxFileSize = 20L * 1024 * 1024, maxRequestSize = 21L * 1024 * 1024)
public class ApplyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final long MAX_RESUME_BYTES = 5L * 1024 * 1024;   // 5 MB

    private final JobDao jobDao = new JobDao();
    private final ApplicationDao applicationDao = new ApplicationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/jobs");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // AuthFilter guarantees a logged-in seeker
        User user = (User) request.getSession(false).getAttribute("user");
        int jobId = -1;

        try {
            jobId = Integer.parseInt(request.getParameter("jobId"));
            String coverNote = request.getParameter("coverNote") == null
                    ? "" : request.getParameter("coverNote").trim();
            Part resume = request.getPart("resume");

            if (jobDao.findById(jobId) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String code;
            if (resume == null || resume.getSize() == 0) {
                code = "nofile";
            } else if (resume.getSize() > MAX_RESUME_BYTES) {
                code = "toolarge";
            } else if (coverNote.length() > 500) {
                code = "note";
            } else if (!isPdf(resume)) {
                code = "notpdf";
            } else if (applicationDao.hasApplied(jobId, user.getId())) {
                code = "duplicate";
            } else {
                try (InputStream in = resume.getInputStream()) {
                    applicationDao.insert(jobId, user.getId(), in, resume.getSize(),
                            cleanFileName(resume.getSubmittedFileName()), "application/pdf", coverNote);
                    code = "ok";
                } catch (SQLIntegrityConstraintViolationException e) {
                    code = "duplicate";   // the database's UNIQUE rule caught a double click
                }
            }

            response.sendRedirect(request.getContextPath() + "/job-details?id=" + jobId + "&apply=" + code);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);   // over the hard upload limit
        } catch (SQLException e) {
            e.printStackTrace();
            if (jobId > 0) {
                response.sendRedirect(request.getContextPath() + "/job-details?id=" + jobId + "&apply=failed");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    // A real PDF starts with the five characters %PDF-
    private boolean isPdf(Part part) throws IOException {
        try (InputStream in = part.getInputStream()) {
            byte[] head = in.readNBytes(5);
            return head.length == 5 && head[0] == '%' && head[1] == 'P'
                    && head[2] == 'D' && head[3] == 'F' && head[4] == '-';
        }
    }

    // Keeps only the file name (no folders or control characters)
    private String cleanFileName(String submitted) {
        if (submitted == null) {
            return "resume.pdf";
        }
        String name = submitted.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1).replaceAll("\\p{Cntrl}", "").trim();
        if (name.isEmpty()) {
            return "resume.pdf";
        }
        return name.length() > 255 ? name.substring(name.length() - 255) : name;
    }
}