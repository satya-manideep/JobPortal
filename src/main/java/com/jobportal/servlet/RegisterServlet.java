package com.jobportal.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import org.mindrot.bcrypt.BCrypt;

import com.jobportal.dao.UserDao;
import com.jobportal.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();

    // Someone typed /register in the address bar
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String role     = clean(request.getParameter("role"));
        String fullName = clean(request.getParameter("fullName"));
        String phone    = clean(request.getParameter("phone"));
        String email    = clean(request.getParameter("email")).toLowerCase();
        String company  = clean(request.getParameter("company"));
        String password = request.getParameter("password");
        String confirm  = request.getParameter("confirmPassword");

        // The browser checks these too, but the server must never trust the browser
        String error = null;
        if (!"SEEKER".equals(role) && !"RECRUITER".equals(role)) {
            error = "Please choose whether you are a job seeker or a recruiter.";
        } else if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            error = "Please fill in all the required fields.";
        } else if ("RECRUITER".equals(role) && company.isEmpty()) {
            error = "Recruiters must enter a company name.";
        } else if (password == null || password.length() < 8) {
            error = "Password must be at least 8 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        if (error == null) {
            try {
                if (userDao.emailExists(email)) {
                    error = "An account with this email already exists.";
                } else {
                    User user = new User();
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                    user.setRole(role);
                    user.setPhone(phone);
                    user.setCompany("RECRUITER".equals(role) ? company : null);

                    userDao.register(user);

                    // Redirect so a page refresh can't submit the form twice
                    response.sendRedirect(request.getContextPath() + "/login.jsp?registered=1");
                    return;
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                error = "An account with this email already exists.";
            } catch (SQLException e) {
                e.printStackTrace();
                error = "Something went wrong on the server. Please try again.";
            }
        }

        request.setAttribute("error", error);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}