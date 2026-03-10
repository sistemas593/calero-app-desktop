package com.calero.lili.api.modCompras.modComprasRetenciones.dto;

import com.calero.lili.api.modCompras.modCompras.dto.CompraImpuestosDto;
import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.enums.TipoIdentificacion;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreationRetencionRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;
    @NotEmpty(message = "No existe el serie")
    private String serieRetencion;
    @NotEmpty(message = "No existe el secuencial")
    private String secuencialRetencion;
    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmisionRetencion;
    private UUID idTercero;
    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String numeroAutorizacionRetencion;
    private String email;
    private Boolean impresa;
    private List<CompraImpuestosDto> compraImpuestos;
    private List<InformacionAdicionalDto> informacionAdicional;
    private Integer ambiente;
    private String codigoDocumento;

    @NotNull(message = "No existe periodo fiscal")
    private FormatoDocumento formatoDocumento;

    private String relacionado;
    @NotEmpty(message = "No existe periodo fiscal")
    private String periodoFiscal;

}
