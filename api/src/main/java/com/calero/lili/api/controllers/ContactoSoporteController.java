package com.calero.lili.api.controllers;

import com.calero.lili.core.apiSitac.dtos.contacto.ContactoRequestDto;
import com.calero.lili.core.apiSitac.services.ContactoSoporteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("apist/v1.0/contacto")
//@CrossOrigin(originPatterns = "*")

@CrossOrigin(origins = {"https://www.software.com.ec", "http://www.software.com.ec"})
public class ContactoSoporteController {

    private final ContactoSoporteServiceImpl contactoSoporteService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void enviarContacto(@RequestBody ContactoRequestDto request) {
        contactoSoporteService.enviarContacto(request);
    }
}
