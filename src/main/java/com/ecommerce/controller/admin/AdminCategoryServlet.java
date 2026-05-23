package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.model.Category;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/categories")
public class AdminCategoryServlet extends HttpServlet {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletUtil.stringParam(request, "action");
        if ("new".equals(action)) {
            Category category = new Category();
            category.setActive(true);
            request.setAttribute("category", category);
            ServletUtil.view(request, response, "admin/category-form");
            return;
        }
        if ("edit".equals(action)) {
            int id = ServletUtil.intParam(request, "id", 0);
            Optional<Category> category = categoryDAO.findById(id);
            if (category.isEmpty()) {
                ServletUtil.flash(request, "Error", "Kategori bulunamadı.");
                ServletUtil.redirect(request, response, "/admin/categories");
                return;
            }
            request.setAttribute("category", category.get());
            ServletUtil.view(request, response, "admin/category-form");
            return;
        }
        request.setAttribute("categories", categoryDAO.findAll());
        ServletUtil.view(request, response, "admin/categories");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletUtil.stringParam(request, "action");
        int id = ServletUtil.intParam(request, "id", 0);
        if ("delete".equals(action)) {
            boolean deleted = categoryDAO.deleteOrDeactivate(id);
            ServletUtil.flash(request, "Success", deleted
                    ? "Kategori silindi."
                    : "Kategoriye bağlı ürün olduğu için pasif yapıldı.");
            ServletUtil.redirect(request, response, "/admin/categories");
            return;
        }
        if ("toggle".equals(action)) {
            categoryDAO.setActive(id, Boolean.parseBoolean(ServletUtil.stringParam(request, "active")));
            ServletUtil.flash(request, "Success", "Kategori durumu güncellendi.");
            ServletUtil.redirect(request, response, "/admin/categories");
            return;
        }

        Category category = readCategory(request);
        List<String> errors = validate(category);
        if (!errors.isEmpty()) {
            request.setAttribute("category", category);
            request.setAttribute("errors", errors);
            ServletUtil.view(request, response, "admin/category-form");
            return;
        }
        if (category.getId() > 0) {
            categoryDAO.update(category);
            ServletUtil.flash(request, "Success", "Kategori güncellendi.");
        } else {
            categoryDAO.create(category);
            ServletUtil.flash(request, "Success", "Kategori eklendi.");
        }
        ServletUtil.redirect(request, response, "/admin/categories");
    }

    private Category readCategory(HttpServletRequest request) {
        Category category = new Category();
        category.setId(ServletUtil.intParam(request, "id", 0));
        category.setName(ServletUtil.stringParam(request, "name"));
        category.setDescription(ServletUtil.stringParam(request, "description"));
        category.setActive(request.getParameter("active") != null);
        return category;
    }

    private List<String> validate(Category category) {
        List<String> errors = new ArrayList<>();
        if (category.getName().isBlank()) {
            errors.add("Kategori adı boş olamaz.");
        }
        return errors;
    }
}
