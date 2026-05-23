package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-orders")
public class MyOrdersServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            ServletUtil.flash(request, "Error", "Siparişlerinizi görmek için giriş yapmalısınız.");
            ServletUtil.redirect(request, response, "/login");
            return;
        }
        request.setAttribute("orders", orderDAO.findByUser(user.getId()));
        ServletUtil.view(request, response, "my-orders");
    }
}
