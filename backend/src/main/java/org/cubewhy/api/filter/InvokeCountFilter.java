package org.cubewhy.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;

import static org.cubewhy.api.StartupRunner.counter;


@Component
public class InvokeCountFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String path = URI.create(request.getRequestURI()).getPath();
        if (!path.equals("/api/web/invoke")) {
            counter.addCount(path);
        }
        filterChain.doFilter(request, response); // do filter
    }
}
