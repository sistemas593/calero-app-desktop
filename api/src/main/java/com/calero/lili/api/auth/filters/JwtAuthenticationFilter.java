package com.calero.lili.api.auth.filters;

import com.calero.lili.api.auth.dto.UsuarioSecurity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioEntity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioRepository;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.calero.lili.api.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.calero.lili.api.auth.TokenJwtConfig.PREFIX_TOKEN;
import static com.calero.lili.api.auth.TokenJwtConfig.SECRET_KEY;


// AQUI INGRESA CUANDO INTENTA AUTENTICAR
// AQUI EXTENDEMOS DE UsernamePasswordAuthenticationFilter Y EN ESA CLASE INDICA CUAL ES EL ENDPOINT PARA LOGEARSE
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private AdUsuarioRepository repository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AdUsuarioRepository repository) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
    }

    // request CONTTIENE LOS DATOS DE INICIO DE SESION
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        AdUsuarioEntity user = null;
        String username = null;
        String password = null;

        try {
            // CREAMOS EL OBJETO USER Y LO LLENAMOS CON LOS DATOS DEL REQUEST
            user = new ObjectMapper().readValue(request.getInputStream(), AdUsuarioEntity.class);

            // RECUPERAMOS EL USERNAME Y PASSWORD DEL OBJETO USER
            username = user.getUsername();
            password = user.getPassword();

//             logger.info("Username desde request InputStream (raw) " + username);
//             logger.info("Password desde request InputStream (raw) " + password);

        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // EN authResult ESTAN LOS DATOS DEL USUARIO QUE ASIGNE EN attemptAuthentication
        // CREO UN OBJETO USER PERO DE SERCURITY NO ES USER ENTITY,

        UsuarioSecurity usuario = ((UsuarioSecurity) authResult.getPrincipal());

        String username = usuario.getUsername();
        String ar = usuario.getArea();
        Long dt = usuario.getData();
        int nv = usuario.getNivel();

        Random random = new Random();

        Optional<AdUsuarioEntity> o = repository.getUserByUsername(username);
        AdUsuarioEntity user = o.orElseThrow();
        user.setRandom(random.nextInt(1000));
        repository.save(user);

        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("ar", ar);
        claims.put("dt", dt);
        claims.put("nv", nv);
        claims.put("rn", user.getRandom());

        // 3600000 EXPIRA EN 1 HORA MAS 0 10 HORAS

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .signWith(SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 36000000))
                .compact();
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!", username));
        body.put("username", username);

        // ASIGNO EL body en el response, convirtiendo el map a un json

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto!");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }

}
