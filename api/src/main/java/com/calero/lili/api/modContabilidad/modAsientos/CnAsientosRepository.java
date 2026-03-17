package com.calero.lili.api.modContabilidad.modAsientos;

import com.calero.lili.api.modContabilidad.modAsientos.projection.OneDetalleProjection;
import com.calero.lili.api.modContabilidad.modAsientos.projection.OneProjection;
import com.calero.lili.api.modContabilidad.modAsientos.projection.TotalesProjection;
import com.calero.lili.core.enums.TipoAsiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface CnAsientosRepository extends JpaRepository<CnAsientosEntity, UUID>, JpaSpecificationExecutor<CnAsientosEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CnAsientosEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idAsiento = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<CnAsientosEntity> findByIdEntity(@Param("idData") Long idData,
                                              @Param("idEmpresa") Long idEmpresa,
                                              @Param("idVenta") UUID idVenta,
                                              @Param("sucursal") String sucursal,
                                              @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM CnAsientosEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idAsiento = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT id_cotizacion as idVenta " +
            "FROM vt_cotizaciones vtVentasEntity " +
            "WHERE (vtVentasEntity.id_Data = :idData)  AND " +
            "(vtVentasEntity.id_Empresa = :idEmpresa) AND " +
            "vtVentasEntity.secuencial = :secuencial LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(Long idData, Long idEmpresa, String secuencial);

    @Query(value = "SELECT  " +
            "detalle.id_item as idItem, " +
            "detalle.codigo_principal as codigoPrincipal, " +
            "detalle.codigo_auxiliar as codigoAuxiliar, " +
            "detalle.codigo_barras as codigoBarras, " +
            "detalle.unidad_medida as unidadMedida, " +
            "detalle.descripcion as descripcion, " +
            "detalle.detalles_adicionales as detallesAdicionales, " +
            "detalle.precio_unitario as precioUnitario, " +
            "detalle.cantidad as cantidad, " +
            "detalle.dscto_item as dsctoItem, " +
            "detalle.descuento as descuento, " +
            "detalle.subtotal_item as subtotalItem, " +
            "detalle.impuestos as impuestos " +
            " " +
            "FROM vt_cotizaciones vtVentasEntity INNER JOIN vt_cotizaciones_detalle detalle ON vtVentasEntity.id_cotizacion = detalle.id_cotizacion " +
            "WHERE vtVentasEntity.id_data = :idData  AND " +
            "vtVentasEntity.id_empresa = :idEmpresa AND " +
            "vtVentasEntity.id_cotizacion = :idVenta ", nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CnAsientosEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
            "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM CnAsientosEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
                    "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )")
    Page<CnAsientosEntity> findAllPaginate(Long idData, Long idEmpresa, String sucursal, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta, Pageable pageable);

//    @Query(
//            value = "SELECT " +
//                    "sum(vtVentaEntity.totalDescuento) as totalDescuento, " +
//                    "sum(vtVentaEntity.total) as total " +
//                    "FROM CnAsientosEntity vtVentaEntity " +
//                    "INNER JOIN VtClienteEntity clienteEntity ON vtVentaEntity.cliente.idTercero = clienteEntity.idTercero " +
//                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
//                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
//                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND "+
//                    "( cast(:fechaAsientoDesde as date) is null OR vtVentaEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
//                    "( cast(:fechaAsientoHasta as date) is null OR vtVentaEntity.fechaAsiento <= :fechaAsientoHasta )"
//    )
//    TotalCabeceraProjection totalCabecera(Long idData, Long idEmpresa, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta, String secuencial);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM vt_cotizaciones vtVentaEntity " +
                    "INNER JOIN vt_cotizaciones_valores valoresEntity ON vtVentaEntity.id_cotizacion = valoresEntity.id_cotizacion " +
                    "WHERE ( vtVentaEntity.id_data = :idData)  AND " +
                    "(vtVentaEntity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentaEntity.sucursal = :sucursal) AND " +
                    "(:numeroIdentificacion IS NULL OR vtVentaEntity.numero_identificacion = :numeroIdentificacion ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaAsientoDesde as date) is null OR vtVentaEntity.fecha_emision >= :fechaAsientoDesde ) AND " +
                    "( cast(:fechaAsientoHasta as date) is null OR vtVentaEntity.fecha_emision <= :fechaAsientoHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta, String numeroIdentificacion, String secuencial);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM CnAsientosEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "( cast(:fechaAsientoDesde as date) is null OR vtVentasEntity.fechaAsiento >= :fechaAsientoDesde ) AND " +
            "( cast(:fechaAsientoHasta as date) is null OR vtVentasEntity.fechaAsiento <= :fechaAsientoHasta )  " +
            ")"
    )
    List<CnAsientosEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaAsientoDesde, LocalDate fechaAsientoHasta);


    @Query("SELECT vtVentasEntity FROM CnAsientosEntity vtVentasEntity WHERE vtVentasEntity.idData = :idData AND" +
            " vtVentasEntity.idEmpresa = :idEmpresa AND" +
            " vtVentasEntity.numeroAsiento = :numeroAsiento AND" +
            " vtVentasEntity.tipoAsiento = :tipoAsiento")
    Optional<CnAsientosEntity> findByNumeroAsiento(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("numeroAsiento") String numeroAsiento,
                                                   @Param("tipoAsiento") TipoAsiento tipoAsiento);


    @Query("""
             SELECT COUNT(ca)
             FROM CnAsientosEntity ca
             WHERE ca.idData = :idData
               AND ca.idEmpresa = :idEmpresa
               AND ca.fechaAsiento BETWEEN :fechaInicio AND :fechaFin
            """)
    Long countByEmpresaAndFechaBetween(@Param("idData") Long idData,
                                       @Param("idEmpresa") Long idEmpresa,
                                       @Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin);


}
