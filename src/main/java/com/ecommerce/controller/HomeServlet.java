package com.ecommerce.controller;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/products")
public class HomeServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int selectedCategoryId = ServletUtil.intParam(request, "categoryId", 0);
        String search = ServletUtil.stringParam(request, "q");
        Integer categoryId = selectedCategoryId > 0 ? selectedCategoryId : null;

        request.setAttribute("categories", categoryDAO.findActive());
        request.setAttribute("products", productDAO.findActive(categoryId, search));
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("search", search);
        ServletUtil.view(request, response, "index");
    }
}
