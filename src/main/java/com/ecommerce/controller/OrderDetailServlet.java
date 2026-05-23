package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/order-detail")
public class OrderDetailServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            ServletUtil.flash(request, "Error", "Sipariş detayını görmek için giriş yapmalısınız.");
            ServletUtil.redirect(request, response, "/login");
            return;
        }
        int id = ServletUtil.intParam(request, "id", 0);
        Optional<Order> order = orderDAO.findByIdForUser(id, user.getId());
        if (order.isEmpty()) {
            ServletUtil.flash(request, "Error", "Sipariş bulunamadı.");
            ServletUtil.redirect(request, response, "/my-orders");
            return;
        }
        request.setAttribute("order", order.get());
        ServletUtil.view(request, response, "order-detail");
    }
}
