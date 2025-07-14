package com.Rewards.Swapify;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/adminDashboard.html")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean isLoggedIn = (session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn")));

        if (isLoggedIn) {
            chain.doFilter(request, response); // continue to dashboard
        } else {
            res.sendRedirect("/adminlogin.html");
        }
    }
}

