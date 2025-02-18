package org.tix.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tix.backend.service.JwtService;
import org.tix.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;


import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    System.out.println("🔍 Кука: " + cookie.getName() + " = " + cookie.getValue()); // Логируем все куки
                    if ("access_token".equals(cookie.getName())) { // Ищем куку с токеном
                        System.out.println("✅ Найден access_token: " + cookie.getValue());
                        return cookie.getValue();
                    }
                }
            }
            System.out.println(" access_token не найден в куках!");
            return null;
        }
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. Достаём JWT из куков
        var jwt = extractTokenFromCookies(request);

// 2. Если нет в куке, пробуем заголовок Authorization
        if (StringUtils.isEmpty(jwt)) {
            var authHeader = request.getHeader("Authorization");
            if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }

// 3. Если токен не найден, пропускаем запрос дальше
        if (StringUtils.isEmpty(jwt)) {
            System.out.println("Токен не найден ни в куке, ни в заголовке!");
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Извлекаем имя пользователя из токена
        var username = jwtService.extractUserName(jwt);

        // 5. Проверяем, есть ли уже аутентификация в SecurityContext
        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(username);

            // 6. Проверяем валидность токена
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }

        // 7. Продолжаем выполнение цепочки фильтров
        filterChain.doFilter(request, response);
    }
}



