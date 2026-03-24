package com.calero.lili.core.modComprasItemsGrupos.dto;

import com.calero.lili.core.enums.TipoItemGrupo;
import lombok.Data;

import java.util.UUID;


@Data
public class GeItemGrupoCreationRequestDto {

    private String grupo;
    private TipoItemGrupo tipoGrupo;
    private UUID idCuentaInventario;
    private UUID idCuentaIngreso;
    private UUID idCuentaCosto;
    private UUID idCuentaDescuento;
    private UUID idCuentaDevolucion;
    private UUID idCuentaGasto;

}
