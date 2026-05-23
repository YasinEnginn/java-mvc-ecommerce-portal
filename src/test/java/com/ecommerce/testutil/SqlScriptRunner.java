package com.ecommerce.testutil;

import com.ecommerce.config.Database;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqlScriptRunner {
    private SqlScriptRunner() {
    }

    public static void resetDatabase() {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : readStatements()) {
                statement.execute(sql);
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Test veritabani hazirlanamadi.", e);
        }
    }

    private static List<String> readStatements() throws IOException {
        Path schema = Path.of("database", "schema.sql");
        String content = Files.readString(schema, StandardCharsets.UTF_8);
        content = content.replaceAll("(?is)CREATE\\s+DATABASE\\s+IF\\s+NOT\\s+EXISTS\\s+ecommerce_portal\\s+CHARACTER\\s+SET\\s+utf8mb4\\s+COLLATE\\s+utf8mb4_unicode_ci\\s*;", "");
        content = content.replaceAll("(?im)^\\s*USE\\s+ecommerce_portal\\s*;\\s*$", "");

        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch == '\'' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuote = !inQuote;
            }
            if (ch == ';' && !inQuote) {
                addStatement(statements, current);
            } else {
                current.append(ch);
            }
        }
        addStatement(statements, current);
        return statements;
    }

    private static void addStatement(List<String> statements, StringBuilder current) {
        String sql = current.toString().trim();
        current.setLength(0);
        if (!sql.isBlank()) {
            statements.add(sql);
        }
    }
}
