package com.ecommerce.controller.admin;

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

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.view(request, response, "admin/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = ServletUtil.stringParam(request, "email").toLowerCase();
        String password = ServletUtil.stringParam(request, "password");
        Optional<User> user = userDAO.authenticate(email, password);
        if (user.isEmpty() || !user.get().isAdmin()) {
            request.setAttribute("error", "Admin bilgileri hatalı veya yetkiniz yok.");
            request.setAttribute("email", email);
            ServletUtil.view(request, response, "admin/login");
            return;
        }
        request.getSession().setAttribute("adminUser", user.get());
        request.getSession().setAttribute("currentUser", user.get());
        ServletUtil.redirect(request, response, "/admin/dashboard");
    }
}
