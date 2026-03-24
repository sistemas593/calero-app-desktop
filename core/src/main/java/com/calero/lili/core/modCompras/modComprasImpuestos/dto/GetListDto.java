package com.calero.lili.core.modCompras.modComprasImpuestos.dto;

import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
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

    private String sucursal;
    private UUID idImpuestos;
    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String fechaEmision;
    private UUID idTercero;
    private String terceroNombre;
    private String numeroIdentificacion;
    private int numeroItems;
    private String formatoDocumento;
    private Boolean anulada;
    private List<ResponseValoresDto> valores;
    private String fechaAutorizacion;
    private String fechaRegistro;
    private List<ImpuestoCodigoDto> impuestoCodigos;

    private String codigoDocumento;
    private String documento;

    private String codigoSustento;
    private String sustento;

   /*private TbDocumentosGetOneDto documento;
    private TbSustentosGetOneDto sustento;*/


    private String destino;

}
