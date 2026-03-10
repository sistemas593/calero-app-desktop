package com.calero.lili.api.auth.filters;

import com.calero.lili.api.auth.TokenJwtConfig;
import com.calero.lili.api.auth.dto.UsuarioSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// AQUI INGRESA UNA VEZ QUE ESTA LOGUEADO Y ACCEDE A UN ENDPOINT

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        // EN EL REQUEST ESTA EL TOKEN DE AUTENTICACION

        String header = request.getHeader(TokenJwtConfig.HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(TokenJwtConfig.PREFIX_TOKEN)) {
            chain.doFilter(request, response);

            return;
        }

        // RETIRAMOS EL PREFIJO DEL TOKEN
        String token = header.replace(TokenJwtConfig.PREFIX_TOKEN, "");

        try {
            // AQUI SE VA A VALIDAR EL TOKEN ENVIAMOS LA LLAVE
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(TokenJwtConfig.SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object authoritiesClaims = claims.get("authorities");
            String username = claims.getSubject();
            Object username2 = claims.get("username");

            String ar = (String) claims.get("ar");
            Long dt = Long.valueOf((Integer) claims.get("dt")) ;
//            Long dt = 1L;

            Long random = Long.valueOf((Integer) claims.get("rn")) ;
//            Long random = 1L;

           /* System.out.println(ar);
            System.out.println(dt);*/

            // AQUI MUESTRA EL USUARIO QUE ESTA EN EL TOKEN
           /* System.out.println(username);
            System.out.println(username2);
            System.out.println(authoritiesClaims);*/


            List<String> roles = (List<String>) claims.get("authorities");

            Collection<? extends GrantedAuthority> authorities =
                    roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            // AQUI GENERA UN OBJETO AUTHENTICATION CON LOS DATOS DEL TOKEN, SE PASA EL USUARIO, LA CONTRASE;A Y LOS PERMISOS

            UsuarioSecurity usuarioSecurity = new UsuarioSecurity(username, null, ar, dt, 1 , authorities, random);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuarioSecurity, null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (Exception e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT no es valido!");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType("application/json");
        }
    }

}
