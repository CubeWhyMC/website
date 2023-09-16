package org.cubewhy.api.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cubewhy.api.entity.Account;
import org.cubewhy.api.entity.AuthorizeVO;
import org.cubewhy.api.entity.RestBean;
import org.cubewhy.api.filter.JwtAuthorizeFilter;
import org.cubewhy.api.service.impl.AccountServiceImpl;
import org.cubewhy.api.utils.JwtUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

@Configuration
public class SecurityConfiguration {
    @Resource
    AccountServiceImpl accountService;
    @Resource
    JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter filter;

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                // user requests
                .authorizeHttpRequests(
                        conf -> conf
                                .requestMatchers("/admin/login").anonymous()
                                .requestMatchers("/launcher/**").permitAll()
                                .requestMatchers("/capes/**").permitAll()
                                .requestMatchers("/game/**").permitAll()
                                .requestMatchers("/market/**").permitAll()
                                .requestMatchers("/api/**").permitAll()
                                .requestMatchers("/api/customer").rememberMe()
                                .requestMatchers("/api/admin/**").hasRole("admin")
                                .requestMatchers("/admin/**").hasRole("admin")
                                .requestMatchers("/admin/").hasRole("admin")
                                .requestMatchers("/static/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        conf -> conf
                                .loginProcessingUrl("/api/auth/login")
                                .successHandler(this::onAuthenticationSuccessful)
                                .failureHandler(this::onAuthenticationFailure)
                )
                .logout(
                        conf -> conf
                                .logoutUrl("/api/auth/logout")
                                .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(exception).toJson());
    }

    private void onUnauthorized(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized(exception).toJson());
    }

    private void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String auth = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(auth)) {
            // make token invalidate
            writer.write(RestBean.success().toJson());
        } else {
            writer.write(RestBean.failure(400, "Failed to logout").toJson());
        }
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.unauthorized(exception).toJson());
    }

    private void onAuthenticationSuccessful(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        User user = (User) authentication.getPrincipal();
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        String token = jwtUtils.createJwt(user, 1, account.getUsername());
        AuthorizeVO authorizeVO = account.asViewObject(AuthorizeVO.class, authorizeVO1 -> {
            authorizeVO1.setExpire(jwtUtils.getExpireDate());
            authorizeVO1.setToken(token);
        });
        response.getWriter().write(RestBean.success(authorizeVO).toJson());
    }
}
