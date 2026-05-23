package com.ecommerce.controller;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.User;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            ServletUtil.flash(request, "Error", "Sipariş oluşturmak için giriş yapmalısınız.");
            ServletUtil.redirect(request, response, "/login");
            return;
        }
        Cart cart = ServletUtil.cart(request);
        if (cart.isEmpty()) {
            ServletUtil.flash(request, "Error", "Sepetiniz boş.");
            ServletUtil.redirect(request, response, "/cart");
            return;
        }
        try {
            int orderId = orderDAO.createOrder(user.getId(), cart);
            cart.clear();
            ServletUtil.flash(request, "Success", "Siparişiniz başarıyla oluşturuldu. Sipariş no: " + orderId);
            ServletUtil.redirect(request, response, "/my-orders");
        } catch (IllegalArgumentException e) {
            ServletUtil.flash(request, "Error", e.getMessage());
            ServletUtil.redirect(request, response, "/cart");
        }
    }
}
