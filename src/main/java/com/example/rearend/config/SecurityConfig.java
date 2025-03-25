package com.example.rearend.config;


import com.example.rearend.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 配置密码加密器
    @Bean
    public  PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置 SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 禁用 CSRF（如果使用 JWT，通常可以禁用 CSRF）
                .csrf(csrf -> csrf.disable())

                // 配置会话管理为无状态（适用于 JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置请求权限
                .authorizeHttpRequests(authorize -> authorize
                        // 允许公开访问的路径
                        .requestMatchers("/api/auth/*","/table/*","/api/*").permitAll() // 登录和注册接口
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 文档

                        // 需要特定角色的路径
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 管理员接口
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // 用户接口

                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )

                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 配置 JWT 过滤器
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}