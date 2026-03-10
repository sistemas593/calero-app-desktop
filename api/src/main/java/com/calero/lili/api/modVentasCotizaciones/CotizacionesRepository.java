package com.calero.lili.api.modVentasCotizaciones;

import com.calero.lili.api.modVentasCotizaciones.projection.OneDetalleProjection;
import com.calero.lili.api.modVentasCotizaciones.projection.OneProjection;
import com.calero.lili.api.modVentasCotizaciones.projection.TotalCabeceraProjection;
import com.calero.lili.api.modVentasCotizaciones.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface CotizacionesRepository extends JpaRepository<VtCotizacionEntity, UUID> , JpaSpecificationExecutor<VtCotizacionEntity> {

    @Query( value= "SELECT vtVentasEntity " +
            "FROM VtCotizacionEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idCotizacion = :idVenta ")
    Optional<VtCotizacionEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idVenta);

    @Transactional
    @Modifying
    @Query("DELETE FROM VtCotizacionEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idCotizacion = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);

    @Query( value= "SELECT id_cotizacion as idVenta " +
            "FROM vt_cotizaciones vtVentasEntity " +
            "WHERE (vtVentasEntity.id_Data = :idData)  AND " +
            "(vtVentasEntity.id_Empresa = :idEmpresa) AND " +
            "vtVentasEntity.secuencial = :secuencial LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(Long idData, Long idEmpresa, String secuencial);

    @Query( value= "SELECT  " +
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
            "vtVentasEntity.id_cotizacion = :idVenta ",  nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(Long idData, Long idEmpresa, UUID idVenta);

        @Query( value= "SELECT vtVentasEntity " +
            "FROM VtCotizacionEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
                "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND "+
                "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND "+
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )"
,
                countQuery = "SELECT COUNT(1) "+
                        "FROM VtCotizacionEntity vtVentasEntity "+
                        "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                        "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                        "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND "+
                        "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND "+
                        "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial ) AND "+
                        "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                        "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )"
        )
    Page<VtCotizacionEntity> findAllPaginate(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String secuencial, Pageable pageable);

    @Query(
            value = "SELECT " +
                    "sum(vtVentaEntity.totalDescuento) as totalDescuento, " +
                    "sum(vtVentaEntity.total) as total " +
                    "FROM VtCotizacionEntity vtVentaEntity " +
                    "INNER JOIN GeTerceroEntity clienteEntity ON vtVentaEntity.cliente.idTercero = clienteEntity.idTercero " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND "+
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    TotalCabeceraProjection totalCabecera(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String secuencial);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM vt_cotizaciones vtVentaEntity " +
                    "INNER JOIN vt_cotizaciones_valores valoresEntity ON vtVentaEntity.id_cotizacion = valoresEntity.id_cotizacion " +
                    "WHERE ( vtVentaEntity.id_data = :idData)  AND " +
                    "(vtVentaEntity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentaEntity.sucursal = :sucursal) AND "+
                    "(:numeroIdentificacion IS NULL OR vtVentaEntity.numero_identificacion = :numeroIdentificacion ) AND "+
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND "+
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fecha_emision <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String secuencial);

    @Query( value= "SELECT vtVentasEntity " +
            "FROM VtCotizacionEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND "+
            "(" +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
            )
    List<VtCotizacionEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String secuencial);

}
