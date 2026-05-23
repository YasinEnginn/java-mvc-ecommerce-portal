package com.ecommerce.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart implements Serializable {
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    public void add(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }
        CartItem existing = items.get(product.getId());
        int currentQuantity = existing == null ? 0 : existing.getQuantity();
        int targetQuantity = Math.min(product.getStock(), currentQuantity + quantity);
        if (targetQuantity <= 0) {
            return;
        }
        if (existing == null) {
            items.put(product.getId(), new CartItem(product, targetQuantity));
        } else {
            existing.setProduct(product);
            existing.setQuantity(targetQuantity);
        }
    }

    public void update(Product product, int quantity) {
        if (product == null) {
            return;
        }
        if (quantity <= 0) {
            remove(product.getId());
            return;
        }
        int safeQuantity = Math.min(product.getStock(), quantity);
        if (safeQuantity <= 0) {
            remove(product.getId());
            return;
        }
        items.put(product.getId(), new CartItem(product, safeQuantity));
    }

    public void remove(int productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }
}
