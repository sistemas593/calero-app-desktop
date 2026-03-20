package com.calero.lili.core.apiSitac.dtos.contacto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactoRequestDto {
    private String name;
    private String email;
    private String number;
    private String message;
}
