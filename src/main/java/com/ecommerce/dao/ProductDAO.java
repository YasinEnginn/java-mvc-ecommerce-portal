package com.ecommerce.dao;

import com.ecommerce.config.Database;
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

public class ProductDAO {
    public List<Product> findActive(Integer categoryId, String search) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*, c.name AS category_name
                FROM products p
                JOIN categories c ON c.id = p.category_id
                WHERE p.is_active = TRUE AND c.is_active = TRUE
                """);
        List<Object> params = new ArrayList<>();
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?)");
            String pattern = "%" + search.toLowerCase() + "%";
            params.add(pattern);
            params.add(pattern);
        }
        sql.append(" ORDER BY p.created_at DESC");
        return query(sql.toString(), params);
    }

    public List<Product> findAll() {
        String sql = """
                SELECT p.*, c.name AS category_name
                FROM products p
                JOIN categories c ON c.id = p.category_id
                ORDER BY p.created_at DESC
                """;
        return query(sql, List.of());
    }

    public Optional<Product> findById(int id) {
        String sql = """
                SELECT p.*, c.name AS category_name
                FROM products p
                JOIN categories c ON c.id = p.category_id
                WHERE p.id = ?
                """;
        List<Product> products = query(sql, List.of(id));
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("Ürün sayısı alınamadı.", e);
        }
    }

    public int create(Product product) {
        String sql = """
                INSERT INTO products (category_id, name, description, price, stock, image_url, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillStatement(statement, product);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ürün kaydedilemedi.", e);
        }
        return 0;
    }

    public void update(Product product) {
        String sql = """
                UPDATE products
                SET category_id = ?, name = ?, description = ?, price = ?, stock = ?, image_url = ?, is_active = ?
                WHERE id = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, product);
            statement.setInt(8, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ürün güncellenemedi.", e);
        }
    }

    public boolean deleteOrDeactivate(int id) {
        if (hasOrderItems(id)) {
            setActive(id, false);
            return false;
        }
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DaoException("Ürün silinemedi.", e);
        }
    }

    public void setActive(int id, boolean active) {
        String sql = "UPDATE products SET is_active = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ürün durumu güncellenemedi.", e);
        }
    }

    private boolean hasOrderItems(int id) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE product_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Ürün sipariş kontrolü yapılamadı.", e);
        }
    }

    private List<Product> query(String sql, List<Object> params) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ürünler listelenemedi.", e);
        }
        return products;
    }

    private void fillStatement(PreparedStatement statement, Product product) throws SQLException {
        statement.setInt(1, product.getCategoryId());
        statement.setString(2, product.getName());
        statement.setString(3, product.getDescription());
        statement.setBigDecimal(4, product.getPrice());
        statement.setInt(5, product.getStock());
        statement.setString(6, product.getImageUrl());
        statement.setBoolean(7, product.isActive());
    }

    private Product map(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCategoryId(resultSet.getInt("category_id"));
        product.setCategoryName(resultSet.getString("category_name"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStock(resultSet.getInt("stock"));
        product.setImageUrl(resultSet.getString("image_url"));
        product.setActive(resultSet.getBoolean("is_active"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        product.setCreatedAt(createdAt == null ? null : new Date(createdAt.getTime()));
        return product;
    }
}
