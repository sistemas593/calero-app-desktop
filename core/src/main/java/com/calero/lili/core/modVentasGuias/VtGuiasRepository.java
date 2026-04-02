package com.calero.lili.core.modVentasGuias;

import com.calero.lili.core.modCompras.impuestosXml.VtGuiaRemisionOneProjection;
import com.calero.lili.core.modVentasGuias.projection.OneDetalleProjection;
import com.calero.lili.core.modVentasGuias.projection.OneProjection;
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
public interface VtGuiasRepository extends JpaRepository<VtGuiaEntity, UUID>, JpaSpecificationExecutor<VtGuiaEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtGuiaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idGuia = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<VtGuiaEntity> findByIdEntity(@Param("idData") Long idData,
                                          @Param("idEmpresa") Long idEmpresa,
                                          @Param("idVenta") UUID idVenta,
                                          @Param("sucursal") String sucursal,
                                          @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM VtGuiaEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idGuia = :idVenta")
    void deleteById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idVenta") UUID idVenta);

    @Query(value = "SELECT id_guia as idGuia " +
            "FROM vt_guias vtVentasEntity " +
            "WHERE (vtVentasEntity.id_Data = :idData)  AND " +
            "(vtVentasEntity.id_Empresa = :idEmpresa) AND " +
            "vtVentasEntity.serie = :serie AND " +
            "vtVentasEntity.secuencial = :secuencial LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("serie") String serie, @Param("secuencial") String secuencial);

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
            "FROM vt_ventas vtVentasEntity INNER JOIN vt_ventas_detalle detalle ON vtVentasEntity.id_guia = detalle.id_guia " +
            "WHERE vtVentasEntity.id_data = :idData  AND " +
            "vtVentasEntity.id_empresa = :idEmpresa AND " +
            "vtVentasEntity.id_guia = :idVenta ", nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idVenta") UUID idVenta);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtGuiaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta ) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM VtGuiaEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta ) AND " +
                    "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)"
    )
    Page<VtGuiaEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("sucursal") String sucursal, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde, @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta, @Param("serie") String serie, @Param("secuencial") String secuencial, @Param("numeroAutorizacion") String numeroAutorizacion, @Param("usuario") String usuario, Pageable pageable);


    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtGuiaEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
    )
    List<VtGuiaEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("sucursal") String sucursal, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde, @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta, @Param("serie") String serie, @Param("secuencial") String secuencial);

    @Query(
            value = "SELECT vtVentaEntity " +
                    "FROM VtGuiaEntity vtVentaEntity " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "vtVentaEntity.formatoDocumento= 'E' AND " +
                    "vtVentaEntity.estadoDocumento= :estadoDocumento AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<VtGuiaEntity> findAllFacturasGenerar(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde, @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta, @Param("estadoDocumento") String estadoDocumento);

    @Query(
            value = "SELECT vtVentaEntity " +
                    "FROM VtGuiaEntity vtVentaEntity " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "vtVentaEntity.estadoDocumento='AUT' AND " +
                    /*"vtVentaEntity.emailEstado='0' AND " +*/
                    "vtVentaEntity.email<>'' AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    List<VtGuiaEntity> findAllFacturasGenerarCorreo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde, @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);

    @Query(value = "SELECT " +
            "entity.id_guia as idGuia,  " +
            "entity.estado_documento as estadoDocumento,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.comprobante as comprobante, " +
            "entity.serie as serie, " +
            "entity.secuencial as secuencial " +
            "FROM vt_guias  entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_guia = :id ", nativeQuery = true)
    Optional<VtGuiaRemisionOneProjection> findXMLById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("id") UUID id);


}
