package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos.dto;

import com.calero.lili.core.enums.TipoTerceroPerSoc;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.dto.detalles.ValoresDto;
import com.calero.lili.core.tablas.tbPaises.TbPaisGetOneDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetReembolsoDto {

    private UUID idLiquidacionReembolsos;
    private String tipoIdentificacionReemb;
    private String numeroIdentificacionReemb;
    private String codPaisPagoReemb;
    private TipoTerceroPerSoc tipoProveedorReemb;
    private String codigoDocumentoReemb;
    private String serieReemb;
    private String secuencialReemb;
    private String fechaEmisionReemb;
    private String numeroAutorizacionReemb;
    private List<ValoresDto> reembolsosValores;
    private TbPaisGetOneDto pais;
    private UUID idLiquidacion;
}
