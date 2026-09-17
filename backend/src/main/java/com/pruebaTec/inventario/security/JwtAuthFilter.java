package com.pruebaTec.inventario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;




/**
 * Este filtro se ejecuta una vez por petición HTTP entrante.
 * Si la petición trae un JWT válido en el header Authorization, identifica
 * al usuario y su rol, para que las reglas de auntorización puedan aplicarse
 * correctamente.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**    
     * Aquí se intercepta la petición, valida el JWT si existe y registra la
     * autenticación en el contexto de seguridad.
     * 
     * @param request petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @param filterChain cadena de filtros a continuar
     */
    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain) throws ServletException, IOException {
        
        //Se espera formato Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        //Sin token se deja pasar la petición sin autenticar. 
        //SecurityConfig es quien decide si esa ruta lo requiere o no
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Se remueven los primeros 7 caracteres para dejar solo el token
        String token = authHeader.substring(7);

        if (jwtUtil.isTokenValid(token)) {
            //si el token es válido, se obitnene la identidad y rol del usuario
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            //Se construye el objeto de autenticación que entiende Spring Security.
            //Aquí no se valida la contraseña ya que la firma válida del JWT ya es
            //la prueba de identidad. 
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            
            //Aquí se registra la autenticación para el resto del procesamiento de la petición. 
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        //Se continua el flujo normal de la petición se autentique o no. 
        filterChain.doFilter(request, response);
    }
}