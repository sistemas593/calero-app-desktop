package com.calero.lili.core.comprobantesWs.dto;

import com.calero.lili.core.enums.TipoPdfFactura;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FilterFacturaPdfDto {

    private TipoPdfFactura tipo;
}
