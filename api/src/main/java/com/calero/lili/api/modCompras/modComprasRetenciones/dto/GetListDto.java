package com.calero.lili.api.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.FormatoDocumento;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
@Builder
public class GetListDto {

    private String sucursal;
    private UUID idRetencion;

    private String serieRetencion;
    private String secuencialRetencion;

    private String fechaEmisionRetencion;

    private String tipoIdentificacion;
    private String terceroNombre;
    private String numeroIdentificacion;

    private UUID idTercero;

    private Boolean anulada;
    private Boolean impresa;

    private List<ResponseValoresDto> valores;

    private List<com.calero.lili.api.modCompras.modComprasImpuestos.dto.GetListDto> listCompraImpuesto;
    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String email;

}
