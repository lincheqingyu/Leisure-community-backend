package com.izijin.cmp.security;

import com.izijin.cmp.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        logger.info("JwtAuthenticationFilter 开始处理请求: {}", path);

        if ("/login".equals(path) || "/register".equals(path)) {
            logger.info("跳过 JWT 认证，因为这是公开端点: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        logger.debug("收到的Authorization 头: {}", authorizationHeader);

        try {
            String jwt = null;
            String username = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                logger.debug("提取的 JWT: {}", jwt);
                try {
                    username = jwtUtil.extractUsername(jwt);
                    logger.debug("从JWT中提取的用户名: {}", username);
                } catch (Exception e) {
                    logger.error("从JWT提取用户名时发生错误", e);
                }
            } else {
                logger.debug("请求中没有有效的Authorization头");
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("用户 {} 认证成功", username);
                    } else {
                        logger.warn("JWT token 无效");
                    }
                } catch (UsernameNotFoundException e) {
                    logger.error("找不到用户: {}", username, e);
                } catch (Exception e) {
                    logger.error("验证token时发生错误", e);
                }
            }

            filterChain.doFilter(request, response);
            logger.info("JwtAuthenticationFilter 完成处理");
        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter 处理请求时发生错误", e);
            // 在这里，我们记录了异常，但仍然抛出它，以便可以被全局异常处理器捕获
            throw e;
        }
    }
}