package com.ecommerce.util;

import com.ecommerce.model.Cart;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public final class ServletUtil {
    private ServletUtil() {
    }

    public static void view(HttpServletRequest request, HttpServletResponse response, String viewName)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/" + viewName + ".jsp");
        dispatcher.forward(request, response);
    }

    public static void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    public static int intParam(HttpServletRequest request, String name, int defaultValue) {
        try {
            String value = request.getParameter(name);
            return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String stringParam(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    public static Cart cart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public static void flash(HttpServletRequest request, String type, String message) {
        request.getSession().setAttribute("flash" + type, message);
    }
}
