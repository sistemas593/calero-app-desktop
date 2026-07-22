package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modCompras.modComprasImpuestos.dto.GetReporteListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal total;
    private List<ResponseValoresDto> valores;
    private List<GetReporteListDto> listCompraImpuesto;
    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private String email;
    private EstadoDocumento estadoDocumento;
    private Integer emailEstado;
    private String periodoFiscal;
    private Boolean existeComprobante;

}
