package com.calero.lili.api.auth;

import com.calero.lili.api.auth.dto.UsuarioSecurity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioEntity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioRepository;
import com.calero.lili.core.errors.exceptions.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityUtils {

    private final AdUsuarioRepository repository;

    public UsuarioSecurity getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioSecurity principal =(UsuarioSecurity) authentication.getPrincipal();
        Long random = principal.getRandom();
        String usuario = principal.getUsername();

        Optional<AdUsuarioEntity> o = repository.getUserByUsername(usuario);

        if (!o.isPresent()) {
            throw new GeneralException(String.format("Username %s no existe en el sistema!", principal.getUsername()));
        }
        AdUsuarioEntity user = o.orElseThrow();

        if (random != user.getRandom()){
            throw new GeneralException(String.format("El token ha sido eliminado", principal.getUsername()));
        }

        return  principal;
    }
}
