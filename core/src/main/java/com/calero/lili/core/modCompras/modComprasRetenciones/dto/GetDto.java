package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.enums.FormatoDocumento;
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
public class GetDto {

    // todo devolver lista de codigos impuesto.

    private UUID idRetencion;
    private String sucursal;
    private String numeroAutorizacionRetencion;
    private String serieRetencion;
    private String secuencialRetencion;
    private String fechaEmisionRetencion;

    private UUID idTercero;

    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String terceroNombre;

    private BigDecimal total;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private String fechaAnulacion;
    private String fechaAutorizacion;
    private String claveAcceso;

    private List<CompraImpuestoResponseDto> compraImpuestos;

    private Integer ambiente;
    private FormatoDocumento formatoDocumento;
    private EstadoDocumento estadoDocumento;
    private Integer emailEstado;
    private String periodoFiscal;
    private Boolean existeComprobante;
    private Boolean retencionAsumida;

    private List<InformacionAdicionalDto> informacionAdicional;




}
