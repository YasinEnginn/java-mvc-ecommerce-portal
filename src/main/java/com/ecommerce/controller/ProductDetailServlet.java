package com.ecommerce.controller;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = ServletUtil.intParam(request, "id", 0);
        Optional<Product> product = productDAO.findById(id);
        if (product.isEmpty() || !product.get().isActive()) {
            ServletUtil.flash(request, "Error", "Ürün bulunamadı veya aktif değil.");
            ServletUtil.redirect(request, response, "/");
            return;
        }
        request.setAttribute("product", product.get());
        ServletUtil.view(request, response, "product-detail");
    }
}
