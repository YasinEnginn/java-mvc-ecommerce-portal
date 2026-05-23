package com.ecommerce.dao;

import com.ecommerce.config.Database;
import com.ecommerce.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {
    public List<Category> findAll() {
        return find("SELECT * FROM categories ORDER BY name");
    }

    public List<Category> findActive() {
        return find("SELECT * FROM categories WHERE is_active = TRUE ORDER BY name");
    }

    public Optional<Category> findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Kategori bulunamadı.", e);
        }
        return Optional.empty();
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("Kategori sayısı alınamadı.", e);
        }
    }

    public int create(Category category) {
        String sql = "INSERT INTO categories (name, description, is_active) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Kategori kaydedilemedi.", e);
        }
        return 0;
    }

    public void update(Category category) {
        String sql = "UPDATE categories SET name = ?, description = ?, is_active = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setBoolean(3, category.isActive());
            statement.setInt(4, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Kategori güncellenemedi.", e);
        }
    }

    public boolean deleteOrDeactivate(int id) {
        if (hasProducts(id)) {
            setActive(id, false);
            return false;
        }
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DaoException("Kategori silinemedi.", e);
        }
    }

    public void setActive(int id, boolean active) {
        String sql = "UPDATE categories SET is_active = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Kategori durumu güncellenemedi.", e);
        }
    }

    public boolean hasProducts(int id) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Kategori ürün kontrolü yapılamadı.", e);
        }
    }

    private List<Category> find(String sql) {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException("Kategoriler listelenemedi.", e);
        }
        return categories;
    }

    private Category map(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getInt("id"));
        category.setName(resultSet.getString("name"));
        category.setDescription(resultSet.getString("description"));
        category.setActive(resultSet.getBoolean("is_active"));
        return category;
    }
}
