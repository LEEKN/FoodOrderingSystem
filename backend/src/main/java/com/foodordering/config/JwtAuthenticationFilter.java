package com.foodordering.config;

import com.foodordering.model.utils.JwtUtil;
import com.foodordering.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 Header 拿 Authorization 欄位
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. 檢查格式是否正確 (要以 Bearer 開頭)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 去掉 "Bearer "
            try {
                // 從 Token 解析出名字 (如果過期或變造，這裡會報錯，就會被擋下)
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("Token 解析失敗: " + e.getMessage());
            }
        }

        // 3. 如果有名字，且目前沒人登入 -> 進行驗證
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 驗證 Token 是否有效 (這步需要去 JwtUtil 加一個方法，等下會補)
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // 4. 驗證通過！發給他通行證，放入 SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. 繼續往下一關走
        filterChain.doFilter(request, response);
    }
}