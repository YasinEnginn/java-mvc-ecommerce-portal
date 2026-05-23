package com.ecommerce.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Category;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.testutil.SqlScriptRunner;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DaoIntegrationTest {
    private final UserDAO userDAO = new UserDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @BeforeEach
    void setUp() {
        SqlScriptRunner.resetDatabase();
    }

    @Test
    void authenticatesSeedUsersWithHashedPasswords() {
        assertTrue(userDAO.authenticate("admin@portal.test", "admin123").orElseThrow().isAdmin());
        assertEquals("Ayşe Demir", userDAO.authenticate("ayse@example.com", "demo123").orElseThrow().getFullName());
        assertTrue(userDAO.authenticate("ayse@example.com", "wrong-password").isEmpty());
    }

    @Test
    void createsCustomerAndRejectsDuplicateEmailAtLookupLevel() {
        User user = new User();
        user.setFullName("Test Kullanıcı");
        user.setEmail("test@example.com");
        user.setPassword("secret123");
        user.setPhone("05554443322");
        user.setAddress("Test adresi");
        user.setRole("CUSTOMER");

        int id = userDAO.create(user);

        assertTrue(id > 0);
        assertTrue(userDAO.emailExists("test@example.com"));
        assertTrue(userDAO.authenticate("test@example.com", "secret123").isPresent());
    }

    @Test
    void listsProductsByCategoryAndSearchOnlyWhenActive() {
        List<Product> allProducts = productDAO.findActive(null, "");
        List<Product> phoneProducts = productDAO.findActive(1, "");
        List<Product> javaProducts = productDAO.findActive(null, "java");

        assertEquals(30, allProducts.size());
        assertEquals(3, phoneProducts.size());
        assertEquals(2, javaProducts.size());
        assertTrue(javaProducts.stream().anyMatch(product -> product.getName().equals("Java MVC Rehberi")));
    }

    @Test
    void cartCapsQuantityAtAvailableStock() {
        Product product = productDAO.findById(3).orElseThrow();
        Cart cart = new Cart();

        cart.add(product, product.getStock() + 10);

        assertEquals(product.getStock(), cart.getItems().iterator().next().getQuantity());
    }

    @Test
    void createsOrderAtomicallyAndDecreasesStock() {
        Product product = productDAO.findById(3).orElseThrow();
        int initialStock = product.getStock();
        Cart cart = new Cart();
        cart.add(product, 2);

        int orderId = orderDAO.createOrder(2, cart);
        Product updated = productDAO.findById(3).orElseThrow();
        Order order = orderDAO.findByIdForUser(orderId, 2).orElseThrow();

        assertTrue(orderId > 0);
        assertEquals(initialStock - 2, updated.getStock());
        assertEquals(new BigDecimal("65999.00"), order.getTotalAmount());
        assertEquals("Beklemede", order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(new BigDecimal("65999.00"), order.getItems().get(0).getSubtotal());
    }

    @Test
    void updatesOrderStatusOnlyForAllowedValues() {
        Product product = productDAO.findById(5).orElseThrow();
        Cart cart = new Cart();
        cart.add(product, 1);
        int orderId = orderDAO.createOrder(2, cart);

        orderDAO.updateStatus(orderId, "Kargoya Verildi");

        assertEquals("Kargoya Verildi", orderDAO.findById(orderId).orElseThrow().getStatus());
    }

    @Test
    void deactivatesReferencedCategoryInsteadOfDeletingIt() {
        boolean deleted = categoryDAO.deleteOrDeactivate(1);
        Category category = categoryDAO.findById(1).orElseThrow();

        assertFalse(deleted);
        assertFalse(category.isActive());
    }

    @Test
    void deactivatesOrderedProductInsteadOfDeletingIt() {
        Product product = productDAO.findById(5).orElseThrow();
        Cart cart = new Cart();
        cart.add(product, 1);
        orderDAO.createOrder(2, cart);

        boolean deleted = productDAO.deleteOrDeactivate(5);
        Product updated = productDAO.findById(5).orElseThrow();

        assertFalse(deleted);
        assertFalse(updated.isActive());
        assertNotNull(updated.getName());
        assertNotEquals(0, updated.getId());
    }
}
