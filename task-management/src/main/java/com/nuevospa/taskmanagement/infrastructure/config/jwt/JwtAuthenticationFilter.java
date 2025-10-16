package com.nuevospa.taskmanagement.infrastructure.config.jwt;

import com.nuevospa.taskmanagement.infrastructure.config.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Verificar si el token existe y tiene el formato "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (eliminar "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Extraer el nombre de usuario del token
        username = jwtProvider.extractUsername(jwt);

        // 4. Si el nombre de usuario es válido y AÚN NO está autenticado en el contexto de seguridad
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. Cargar los detalles del usuario desde la DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validar el token contra el usuario cargado
            if (jwtProvider.validateToken(jwt, userDetails)) {

                // 7. Si es válido, crear un objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // 8. Añadir detalles de la solicitud (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. Establecer el usuario como autenticado en Spring Security (Contexto)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Pasar al siguiente filtro en la cadena (o al Controller si no hay más filtros)
        filterChain.doFilter(request,response);
    }
}
