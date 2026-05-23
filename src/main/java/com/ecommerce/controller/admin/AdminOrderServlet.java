package com.ecommerce.controller.admin;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = {"/admin/orders", "/admin/order-detail"})
public class AdminOrderServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getServletPath().endsWith("order-detail")) {
            int id = ServletUtil.intParam(request, "id", 0);
            Optional<Order> order = orderDAO.findById(id);
            if (order.isEmpty()) {
                ServletUtil.flash(request, "Error", "Sipariş bulunamadı.");
                ServletUtil.redirect(request, response, "/admin/orders");
                return;
            }
            request.setAttribute("order", order.get());
            request.setAttribute("statuses", OrderDAO.STATUSES);
            ServletUtil.view(request, response, "admin/order-detail");
            return;
        }
        request.setAttribute("orders", orderDAO.findAll());
        ServletUtil.view(request, response, "admin/orders");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ServletUtil.intParam(request, "id", 0);
        String status = ServletUtil.stringParam(request, "status");
        try {
            orderDAO.updateStatus(id, status);
            ServletUtil.flash(request, "Success", "Sipariş durumu güncellendi.");
        } catch (IllegalArgumentException e) {
            ServletUtil.flash(request, "Error", e.getMessage());
        }
        ServletUtil.redirect(request, response, "/admin/order-detail?id=" + id);
    }
}
