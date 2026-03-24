package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto;

import com.calero.lili.core.enums.TipoTerceroPerSoc;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReembolsoRequestDto {

    @NotEmpty(message = "El tipo de identificación esta vacío")
    private String tipoIdentificacionReemb;

    @NotEmpty(message = "El número de identificación esta vacío")
    private String numeroIdentificacionReemb;

    private String codPaisPagoReemb;

    @NotNull(message = "El tipo de proveedor no se encuentra")
    private TipoTerceroPerSoc tipoProveedorReemb;

    private String codigoDocumentoReemb;
    @NotEmpty(message = "La serie esta vacía")

    private String serieReemb;

    @NotEmpty(message = "El secuencial esta vacío")
    private String secuencialReemb;

    @NotEmpty(message = "La fecha de emision esta vacía")
    private String fechaEmisionReemb;

    private String numeroAutorizacionReemb;
    private List<ValoresDto> reembolsosValores;
    private UUID idLiquidacionReembolsos;

}
