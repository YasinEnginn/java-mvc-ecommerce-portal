package com.ecommerce.dao;

import com.ecommerce.config.Database;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrderDAO {
    public static final List<String> STATUSES = List.of(
            "Beklemede",
            "Hazırlanıyor",
            "Kargoya Verildi",
            "Tamamlandı",
            "İptal Edildi"
    );

    public int createOrder(int userId, Cart cart) {
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Sepet boş.");
        }
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                List<OrderItem> items = prepareItems(connection, cart);
                BigDecimal total = items.stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int orderId = insertOrder(connection, userId, total);
                for (OrderItem item : items) {
                    insertOrderItem(connection, orderId, item);
                    decreaseStock(connection, item.getProductId(), item.getQuantity());
                }
                connection.commit();
                return orderId;
            } catch (RuntimeException | SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException("Sipariş oluşturulamadı.", e);
        }
    }

    public List<Order> findByUser(int userId) {
        String sql = """
                SELECT o.*, u.full_name AS customer_name
                FROM orders o
                JOIN users u ON u.id = o.user_id
                WHERE o.user_id = ?
                ORDER BY o.order_date DESC
                """;
        return queryOrders(sql, List.of(userId));
    }

    public List<Order> findAll() {
        String sql = """
                SELECT o.*, u.full_name AS customer_name
                FROM orders o
                JOIN users u ON u.id = o.user_id
                ORDER BY o.order_date DESC
                """;
        return queryOrders(sql, List.of());
    }

    public Optional<Order> findById(int id) {
        String sql = """
                SELECT o.*, u.full_name AS customer_name
                FROM orders o
                JOIN users u ON u.id = o.user_id
                WHERE o.id = ?
                """;
        List<Order> orders = queryOrders(sql, List.of(id));
        if (orders.isEmpty()) {
            return Optional.empty();
        }
        Order order = orders.get(0);
        order.setItems(findItems(order.getId()));
        return Optional.of(order);
    }

    public Optional<Order> findByIdForUser(int id, int userId) {
        Optional<Order> order = findById(id);
        if (order.isPresent() && order.get().getUserId() == userId) {
            return order;
        }
        return Optional.empty();
    }

    public void updateStatus(int orderId, String status) {
        if (!STATUSES.contains(status)) {
            throw new IllegalArgumentException("Geçersiz sipariş durumu.");
        }
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Sipariş durumu güncellenemedi.", e);
        }
    }

    public int countAll() {
        return count("SELECT COUNT(*) FROM orders", null);
    }

    public int countByStatus(String status) {
        return count("SELECT COUNT(*) FROM orders WHERE status = ?", status);
    }

    private List<OrderItem> prepareItems(Connection connection, Cart cart) throws SQLException {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT id, name, price, stock, is_active FROM products WHERE id = ? FOR UPDATE";
        for (CartItem cartItem : cart.getItems()) {
            Product cartProduct = cartItem.getProduct();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, cartProduct.getId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next() || !resultSet.getBoolean("is_active")) {
                        throw new IllegalArgumentException(cartProduct.getName() + " artık satışta değil.");
                    }
                    int stock = resultSet.getInt("stock");
                    if (stock < cartItem.getQuantity()) {
                        throw new IllegalArgumentException(resultSet.getString("name") + " için yeterli stok yok.");
                    }
                    BigDecimal unitPrice = resultSet.getBigDecimal("price");
                    OrderItem item = new OrderItem();
                    item.setProductId(resultSet.getInt("id"));
                    item.setProductName(resultSet.getString("name"));
                    item.setQuantity(cartItem.getQuantity());
                    item.setUnitPrice(unitPrice);
                    item.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                    orderItems.add(item);
                }
            }
        }
        return orderItems;
    }

    private int insertOrder(Connection connection, int userId, BigDecimal total) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setBigDecimal(2, total);
            statement.setString(3, "Beklemede");
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Sipariş numarası oluşturulamadı.");
    }

    private void insertOrderItem(Connection connection, int orderId, OrderItem item) throws SQLException {
        String sql = """
                INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            statement.setBigDecimal(4, item.getUnitPrice());
            statement.setBigDecimal(5, item.getSubtotal());
            statement.executeUpdate();
        }
    }

    private void decreaseStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Stok güncellenemedi.");
            }
        }
    }

    private List<Order> queryOrders(String sql, List<Object> params) {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Siparişler listelenemedi.", e);
        }
        return orders;
    }

    private List<OrderItem> findItems(int orderId) {
        String sql = """
                SELECT oi.*, p.name AS product_name
                FROM order_items oi
                JOIN products p ON p.id = oi.product_id
                WHERE oi.order_id = ?
                ORDER BY oi.id
                """;
        List<OrderItem> items = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(resultSet.getInt("id"));
                    item.setOrderId(resultSet.getInt("order_id"));
                    item.setProductId(resultSet.getInt("product_id"));
                    item.setProductName(resultSet.getString("product_name"));
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setUnitPrice(resultSet.getBigDecimal("unit_price"));
                    item.setSubtotal(resultSet.getBigDecimal("subtotal"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Sipariş detayları listelenemedi.", e);
        }
        return items;
    }

    private int count(String sql, String status) {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (status != null) {
                statement.setString(1, status);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Sipariş sayısı alınamadı.", e);
        }
    }

    private Order mapOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setUserId(resultSet.getInt("user_id"));
        order.setCustomerName(resultSet.getString("customer_name"));
        Timestamp orderDate = resultSet.getTimestamp("order_date");
        order.setOrderDate(orderDate == null ? null : new Date(orderDate.getTime()));
        order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
        order.setStatus(resultSet.getString("status"));
        return order;
    }
}
