package com.example.userauth.filter;

import net.spy.memcached.MemcachedClient;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final MemcachedClient memcachedClient;

    public AuthFilter(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // ✅ CRITICAL: allow CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        return path.startsWith("/api/auth") || path.equals("/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"success\":false,\"message\":\"Missing Authorization header\"}");
            return;
        }

        String token = auth.substring(7);
        Object val = memcachedClient.get("auth:token:" + token);

        if (val == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"success\":false,\"message\":\"Invalid or expired token\"}");
            return;
        }

        // ✅ pass logged-in user to controllers
        // format = "userId|email"
        request.setAttribute("authUser", val.toString());

        filterChain.doFilter(request, response);
    }
}
