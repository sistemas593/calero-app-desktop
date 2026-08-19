package com.calero.lili.api.modAdminUsuarios;

import com.calero.lili.core.apiSitac.dtos.EnvioCorreoModeloDto;
import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.apiSitac.services.EmailSender;
import com.calero.lili.core.apiSitac.services.GenerarBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdUsuarioEnvioCorreoService {

    private final AdMailsConfigRepository adConfigRepository;
    private final GenerarBody generarBody;
    private final EmailSender emailSender;

    public void enviarCorreo(AdUsuarioEntity usuario, String password) {

        AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(1L);

        EnvioCorreoModeloDto dtoCorreo = generarBody.generarModelCorreoNuevoUsuario(adConfigMailEntity,
                usuario.getUsername(), password, usuario.getEmail());

        emailSender.send(dtoCorreo);

    }

}
