package com.ecommerce.controller.admin;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Product;
import com.ecommerce.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletUtil.stringParam(request, "action");
        if ("new".equals(action)) {
            Product product = new Product();
            product.setActive(true);
            product.setPrice(BigDecimal.ZERO);
            request.setAttribute("product", product);
            request.setAttribute("categories", categoryDAO.findAll());
            ServletUtil.view(request, response, "admin/product-form");
            return;
        }
        if ("edit".equals(action)) {
            int id = ServletUtil.intParam(request, "id", 0);
            Optional<Product> product = productDAO.findById(id);
            if (product.isEmpty()) {
                ServletUtil.flash(request, "Error", "Ürün bulunamadı.");
                ServletUtil.redirect(request, response, "/admin/products");
                return;
            }
            request.setAttribute("product", product.get());
            request.setAttribute("categories", categoryDAO.findAll());
            ServletUtil.view(request, response, "admin/product-form");
            return;
        }
        request.setAttribute("products", productDAO.findAll());
        ServletUtil.view(request, response, "admin/products");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ServletUtil.stringParam(request, "action");
        int id = ServletUtil.intParam(request, "id", 0);
        if ("delete".equals(action)) {
            boolean deleted = productDAO.deleteOrDeactivate(id);
            ServletUtil.flash(request, "Success", deleted
                    ? "Ürün silindi."
                    : "Siparişte kullanıldığı için ürün pasif yapıldı.");
            ServletUtil.redirect(request, response, "/admin/products");
            return;
        }
        if ("toggle".equals(action)) {
            productDAO.setActive(id, Boolean.parseBoolean(ServletUtil.stringParam(request, "active")));
            ServletUtil.flash(request, "Success", "Ürün durumu güncellendi.");
            ServletUtil.redirect(request, response, "/admin/products");
            return;
        }

        Product product = readProduct(request);
        List<String> errors = validate(product);
        if (!errors.isEmpty()) {
            request.setAttribute("product", product);
            request.setAttribute("categories", categoryDAO.findAll());
            request.setAttribute("errors", errors);
            ServletUtil.view(request, response, "admin/product-form");
            return;
        }
        if (product.getId() > 0) {
            productDAO.update(product);
            ServletUtil.flash(request, "Success", "Ürün güncellendi.");
        } else {
            productDAO.create(product);
            ServletUtil.flash(request, "Success", "Ürün eklendi.");
        }
        ServletUtil.redirect(request, response, "/admin/products");
    }

    private Product readProduct(HttpServletRequest request) {
        Product product = new Product();
        product.setId(ServletUtil.intParam(request, "id", 0));
        product.setCategoryId(ServletUtil.intParam(request, "categoryId", 0));
        product.setName(ServletUtil.stringParam(request, "name"));
        product.setDescription(ServletUtil.stringParam(request, "description"));
        product.setPrice(parsePrice(ServletUtil.stringParam(request, "price")));
        product.setStock(ServletUtil.intParam(request, "stock", -1));
        product.setImageUrl(ServletUtil.stringParam(request, "imageUrl"));
        product.setActive(request.getParameter("active") != null);
        return product;
    }

    private BigDecimal parsePrice(String value) {
        try {
            return new BigDecimal(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private List<String> validate(Product product) {
        List<String> errors = new ArrayList<>();
        if (product.getName().isBlank()) {
            errors.add("Ürün adı boş olamaz.");
        }
        if (product.getCategoryId() <= 0) {
            errors.add("Kategori seçilmelidir.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Fiyat 0'dan büyük olmalıdır.");
        }
        if (product.getStock() < 0) {
            errors.add("Stok miktarı negatif olamaz.");
        }
        return errors;
    }
}
