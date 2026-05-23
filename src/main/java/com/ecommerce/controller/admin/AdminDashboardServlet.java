package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.DashboardStats;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserDAO userDAO = new UserDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DashboardStats stats = new DashboardStats();
        stats.setProductCount(productDAO.countAll());
        stats.setCategoryCount(categoryDAO.countAll());
        stats.setUserCount(userDAO.countAll());
        stats.setOrderCount(orderDAO.countAll());
        stats.setPendingOrderCount(orderDAO.countByStatus("Beklemede"));
        request.setAttribute("stats", stats);
        ServletUtil.view(request, response, "admin/dashboard");
    }
}
