package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetListDto {

    private UUID idRetencion;
    private String sucursal;
    private String numeroAutorizacionRetencion;
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
    private List<com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetListDto> listCompraImpuesto;
    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String email;
    private EstadoDocumento estadoDocumento;
    private Integer emailEstado;

}
