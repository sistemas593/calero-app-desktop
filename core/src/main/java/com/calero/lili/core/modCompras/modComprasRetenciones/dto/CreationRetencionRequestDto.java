package com.calero.lili.core.modCompras.modComprasRetenciones.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.FormatoDocumento;
import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreationRetencionRequestDto {

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;
    @NotEmpty(message = "No existe el serie")
    private String serieRetencion;

    private String secuencialRetencion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmisionRetencion;
    private UUID idTercero;
    private String numeroAutorizacionRetencion;
    private String email;
    private Boolean impresa;
    @NotNull(message = "Es requerido compras impuestos")
    @NotEmpty(message = "Es requerido compras impuestos")
    private List<CompraImpuestosDto> compraImpuestos;
    private List<InformacionAdicionalDto> informacionAdicional;
    private Integer ambiente;
    private Boolean retencionAsumida;

    @NotNull(message = "No existe periodo fiscal")
    private FormatoDocumento formatoDocumento;

    private String relacionado;
    @NotEmpty(message = "No existe periodo fiscal")
    private String periodoFiscal; // 01/2026  //

}
