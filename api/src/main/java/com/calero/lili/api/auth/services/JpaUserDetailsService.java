package com.calero.lili.api.auth.services;

import com.calero.lili.api.auth.dto.UsuarioSecurity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioEntity;
import com.calero.lili.api.modAdminUsuarios.AdUsuarioRepository;
import com.calero.lili.api.modAdminUsuarios.adPermisos.AdPermisosEntity;
import com.calero.lili.core.errors.exceptions.GeneralException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private AdUsuarioRepository repository;

    @Override
    @Transactional(readOnly = false)

    public UserDetails loadUserByUsername(String username) throws GeneralException {
        Optional<AdUsuarioEntity> o = repository.getUserByUsername(username);

        if (!o.isPresent()) {
            throw new GeneralException(String.format("Username %s no existe en el sistema!", username));
        }
        AdUsuarioEntity user = o.orElseThrow();

        List<String> permisos = user.getGrupos().stream()
                .flatMap(grupo -> grupo.getPermisos().stream())
                .map(AdPermisosEntity::getPermiso)
                .distinct()
                .toList();

        // AQUI ES DONDE SE OBTIENE LAS PERMISO
        List<GrantedAuthority> authorities = permisos.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Quitar el id area
        return new UsuarioSecurity(
                user.getUsername(),
                user.getPassword(),
                user.getIdArea(),
                user.getIdData(),
                user.getNivel(),
                authorities,
                0L
        );
    }


}
