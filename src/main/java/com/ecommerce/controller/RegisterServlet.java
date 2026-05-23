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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.view(request, response, "register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = new User();
        user.setFullName(ServletUtil.stringParam(request, "fullName"));
        user.setEmail(ServletUtil.stringParam(request, "email").toLowerCase());
        user.setPassword(ServletUtil.stringParam(request, "password"));
        user.setPhone(ServletUtil.stringParam(request, "phone"));
        user.setAddress(ServletUtil.stringParam(request, "address"));
        user.setRole("CUSTOMER");

        List<String> errors = validate(user);
        if (errors.isEmpty() && userDAO.emailExists(user.getEmail())) {
            errors.add("Bu e-posta adresi zaten kayıtlı.");
        }
        if (!errors.isEmpty()) {
            user.setPassword("");
            request.setAttribute("formUser", user);
            request.setAttribute("errors", errors);
            ServletUtil.view(request, response, "register");
            return;
        }

        userDAO.create(user);
        ServletUtil.flash(request, "Success", "Kayıt başarılı. E-posta ve şifrenizle giriş yapabilirsiniz.");
        ServletUtil.redirect(request, response, "/login");
    }

    private List<String> validate(User user) {
        List<String> errors = new ArrayList<>();
        if (user.getFullName().isBlank()) {
            errors.add("Ad soyad boş olamaz.");
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            errors.add("Geçerli bir e-posta giriniz.");
        }
        if (user.getPassword().length() < 6) {
            errors.add("Şifre en az 6 karakter olmalıdır.");
        }
        if (user.getPhone().isBlank()) {
            errors.add("Telefon boş olamaz.");
        }
        if (user.getAddress().isBlank()) {
            errors.add("Adres boş olamaz.");
        }
        return errors;
    }
}
