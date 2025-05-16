package com.caop.taskmanager_inform.security;

import com.caop.taskmanager_inform.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String email;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        email = jwtUtil.getEmailFromToken(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userRepo.findByEmail(email).orElse(null);
            if (user != null && jwtUtil.validateToken(token)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getRoles()
                );
                user.getRoles().forEach(r -> System.out.println("ROL: " + r.getName()));


                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);

                request.setAttribute("userId", user.getId());
            }
        }

        filterChain.doFilter(request, response);

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("❌ Autenticación no establecida, posible problema con el token o roles.");
        }
    }
}
