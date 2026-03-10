package com.calero.lili.api.utils;

import com.calero.lili.api.auth.SecurityUtils;
import com.calero.lili.api.auth.dto.UsuarioSecurity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IdDataServiceImpl {


    private final SecurityUtils securityUtils;

    public Long getIdData() {
        UsuarioSecurity user = securityUtils.getUser();
        return user.getData();
    }
}
