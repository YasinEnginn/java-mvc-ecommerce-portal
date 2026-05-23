package com.ecommerce.controller;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.view(request, response, "login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = ServletUtil.stringParam(request, "email").toLowerCase();
        String password = ServletUtil.stringParam(request, "password");
        Optional<User> user = userDAO.authenticate(email, password);
        if (user.isEmpty()) {
            request.setAttribute("error", "E-posta veya şifre hatalı.");
            request.setAttribute("email", email);
            ServletUtil.view(request, response, "login");
            return;
        }
        request.getSession().setAttribute("currentUser", user.get());
        ServletUtil.flash(request, "Success", "Hoş geldiniz, " + user.get().getFullName() + ".");
        ServletUtil.redirect(request, response, "/products");
    }
}
