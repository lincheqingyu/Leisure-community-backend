package com.izijin.cmp.config;
import com.izijin.cmp.model.User;
import com.izijin.cmp.security.JwtAuthenticationFilter;
import com.izijin.cmp.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.izijin.cmp.security.SecurityUser;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource, 
                          @Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        logger.info("SecurityConfig 初始化");
    }

    @Autowired
    private UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.info("创建 AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("创建 UserDetailsService");
        return username -> {
            logger.info("尝试加载用户: {}", username);
            User user = userService.findByUsername(username);
            if (user == null) {
                logger.warn("用户不存在: {}", username);
                throw new UsernameNotFoundException("用户不存在");
            }
            logger.info("成功加载用户: {}", username);
            return new SecurityUser(
                user.getUsername(),
                user.getPassword(),
                user.getUserType().toString()
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("配置 SecurityFilterChain");
        http
            .csrf(csrf -> {
                csrf.disable();
                logger.info("CSRF 保护已禁用");
            })
            .cors(cors -> {
                cors.configurationSource(corsConfigurationSource);
                logger.info("CORS 配置已应用");
            })
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/error", "/register","/admin/migrate-passwords").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                logger.info("会话管理策略设置为无状态");
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> {
                form.disable();
                logger.info("表单登录已禁用");
            })
            .httpBasic(basic -> {
                basic.disable();
                logger.info("HTTP Basic 认证已禁用");
            });

        logger.info("SecurityFilterChain 配置完成");
        return http.build();
    }
}