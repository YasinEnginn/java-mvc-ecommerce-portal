package com.ecommerce.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class FlashFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession(false);
        if (session != null) {
            moveFlash(session, request, "Success");
            moveFlash(session, request, "Error");
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    private void moveFlash(HttpSession session, HttpServletRequest request, String type) {
        String key = "flash" + type;
        Object message = session.getAttribute(key);
        if (message != null) {
            request.setAttribute(key, message);
            session.removeAttribute(key);
        }
    }
}
