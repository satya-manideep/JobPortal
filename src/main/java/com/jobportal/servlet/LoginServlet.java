package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;

import org.mindrot.bcrypt.BCrypt;

import com.jobportal.dao.UserDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email") == null
                ? "" : request.getParameter("email").trim().toLowerCase();
        String password = request.getParameter("password");

        String error = null;

        try {
            User user = userDao.findByEmail(email);

            if (user != null && password != null && BCrypt.checkpw(password, user.getPassword())) {

                // Drop any old session and start a fresh one for the logged-in user
                HttpSession old = request.getSession(false);
                if (old != null) {
                    old.invalidate();
                }
                HttpSession session = request.getSession(true);
                session.setMaxInactiveInterval(30 * 60);   // 30 minutes

                user.setPassword(null);                    // never keep the hash in the session
                session.setAttribute("user", user);

                String home = user.isRecruiter() ? "/my-jobs.jsp" : "/jobs";
                response.sendRedirect(request.getContextPath() + home);
                return;
            }

            // Same message for a wrong email and a wrong password
            error = "Invalid email or password.";

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            error = "Something went wrong on the server. Please try again.";
        }

        request.setAttribute("error", error);
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}