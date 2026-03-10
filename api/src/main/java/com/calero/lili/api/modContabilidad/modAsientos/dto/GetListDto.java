package com.calero.lili.api.modContabilidad.modAsientos.dto;

import com.calero.lili.core.enums.TipoAsiento;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GetListDto {

    private UUID idAsiento;
    private String sucursal;
    private String idPeriodo;
    private TipoAsiento tipoAsiento;
    private String numeroAsiento;
    private String fechaAsiento;
    private String concepto;
    private Boolean mayorizado;
    private Boolean anulada;
    private List<ResponseValoresDto> valores;

}
