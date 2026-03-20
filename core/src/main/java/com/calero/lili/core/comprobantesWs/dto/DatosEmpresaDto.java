package com.calero.lili.core.comprobantesWs.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class DatosEmpresaDto {

    //private String sgn;
    private String pwd;
    private byte[] imageBytes;
    private InputStream inputStreamFileSgn;
}
