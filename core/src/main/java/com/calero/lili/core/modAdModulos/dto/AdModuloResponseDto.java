package com.calero.lili.core.modAdModulos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdModuloResponseDto {

    private Long idModuloData;
    private Long idModAdminEmpresas;
    private Long idModAdminEmpresasSeries;
    private Long idModAdminEmpresasSeriesDocumentos;
    private Long idModAdminEmpresasSucursales;
    private Long idModAdminListaNegra;
    private Long idModAdminPorcentajes;
    private Long idModCargarExcelServices;
    private Long idModCargarExcelDP;
    private Long idModClientesConfiguraciones;
    private Long idModClientesTickets;
    private Long idModCompras;
    private Long idModComprasItems;
    private Long idModComprasItemsBodegas;
    private Long idModComprasItemsCategorias;
    private Long idModComprasItemsGrupos;
    private Long idModComprasItemsImpuesto;
    private Long idModComprasItemsMarcas;
    private Long idModComprasItemsMedidas;
    private Long idModComprasOrden;
    private Long idModComprasProveedoresGrupos;
    private Long idModContabilidad;
    private Long idModImpuestosAnexos;
    private Long idModImpuestosProcesos;
    private Long idModLocalidades;
    private Long idModModuloDatas;
    private Long idModRRHH;
    private Long idModTerceros;
    private Long idModTercerosClientesParametros;
    private Long idModTercerosProvedoresParametros;
    private Long idModTesoreria;
    private Long idModVentas;
    private Long idModVentasClientesGrupos;
    private Long idModVentasCotizaciones;
    private Long idModVentasGuias;
    private Long idModVentasPedidos;
    private Long idModVentasRetenciones;
    private Long idModVentasVendedores;
    private Long idModVentasZonas;
}
