package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.dtos.EnvioCorreoModeloDto;
import com.calero.lili.core.apiSitac.dtos.contacto.ContactoRequestDto;
import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactoSoporteServiceImpl {

    private final AdMailsConfigRepository adConfigRepository;
    private final EmailSender emailSender;
    private final GenerarBody generarBody;

    public void enviarContacto(ContactoRequestDto request) {
        AdMailConfigEntity config = adConfigRepository.findByIdConfig(Long.valueOf(1));
        EnvioCorreoModeloDto model = generarBody.generarModelCorreoNuevoContacto(request, config);
        emailSender.send(model);
    }
}
