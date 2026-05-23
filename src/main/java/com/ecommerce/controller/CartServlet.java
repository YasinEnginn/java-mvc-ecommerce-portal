package com.ecommerce.controller;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cart", ServletUtil.cart(request));
        ServletUtil.view(request, response, "cart");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletUtil.stringParam(request, "action");
        Cart cart = ServletUtil.cart(request);
        if ("clear".equals(action)) {
            cart.clear();
            ServletUtil.flash(request, "Success", "Sepet temizlendi.");
            ServletUtil.redirect(request, response, "/cart");
            return;
        }

        int productId = ServletUtil.intParam(request, "productId", 0);
        Optional<Product> product = productDAO.findById(productId);
        if (product.isEmpty() || !product.get().isActive()) {
            ServletUtil.flash(request, "Error", "Ürün bulunamadı.");
            ServletUtil.redirect(request, response, "/cart");
            return;
        }

        if ("remove".equals(action)) {
            cart.remove(productId);
            ServletUtil.flash(request, "Success", "Ürün sepetten çıkarıldı.");
        } else if ("update".equals(action)) {
            int quantity = ServletUtil.intParam(request, "quantity", 1);
            cart.update(product.get(), quantity);
            ServletUtil.flash(request, "Success", "Sepet güncellendi.");
        } else {
            int quantity = ServletUtil.intParam(request, "quantity", 1);
            if (!product.get().isInStock()) {
                ServletUtil.flash(request, "Error", "Bu ürün stokta yok.");
            } else {
                cart.add(product.get(), quantity);
                ServletUtil.flash(request, "Success", "Ürün sepete eklendi.");
            }
        }
        ServletUtil.redirect(request, response, "/cart");
    }
}
