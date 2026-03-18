package com.calero.lili.core.modVentasPedidos;

import com.calero.lili.core.modVentasPedidos.projection.OneDetalleProjection;
import com.calero.lili.core.modVentasPedidos.projection.OneProjection;
import com.calero.lili.core.modVentasPedidos.projection.TotalCabeceraProjection;
import com.calero.lili.core.modVentasPedidos.projection.TotalesProjection;
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
public interface PedidosRepository extends JpaRepository<VtPedidoEntity, UUID>, JpaSpecificationExecutor<VtPedidoEntity> {

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtPedidoEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "vtVentasEntity.idPedido = :idVenta AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario)")
    Optional<VtPedidoEntity> findByIdEntity(@Param("idData") Long idData,
                                            @Param("idEmpresa") Long idEmpresa,
                                            @Param("idVenta") UUID idVenta,
                                            @Param("sucursal") String sucursal,
                                            @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query("DELETE FROM VtPedidoEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idPedido = :idVenta")
    void deleteById(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT id_pedido as idPedido " +
            "FROM vt_pedidos vtVentasEntity " +
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
            "FROM vt_pedidos vtVentasEntity INNER JOIN vt_pedidos_detalle detalle ON vtVentasEntity.id_pedido = detalle.id_pedido " +
            "WHERE vtVentasEntity.id_data = :idData  AND " +
            "vtVentasEntity.id_empresa = :idEmpresa AND " +
            "vtVentasEntity.id_pedido = :idVenta ", nativeQuery = true)
    List<OneDetalleProjection> findByIdVentaDetalle(Long idData, Long idEmpresa, UUID idVenta);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtPedidoEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM VtPedidoEntity vtVentasEntity " +
                    "WHERE ( vtVentasEntity.idData = :idData)  AND " +
                    "(vtVentasEntity.idEmpresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
                    "(:usuario IS NULL OR vtVentasEntity.createdBy = :usuario) AND " +
                    "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR vtVentasEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR vtVentasEntity.numeroAutorizacion = :numeroAutorizacion ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    Page<VtPedidoEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                         @Param("sucursal") String sucursal,
                                         @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("numeroIdentificacion") String numeroIdentificacion,
                                         @Param("serie") String serie,
                                         @Param("secuencial") String secuencial,
                                         @Param("numeroAutorizacion") String numeroAutorizacion,
                                         @Param("usuario") String usuario,
                                         Pageable pageable);

    @Query(
            value = "SELECT " +
                    "sum(vtVentaEntity.totalDescuento) as totalDescuento, " +
                    "sum(vtVentaEntity.total) as total " +
                    "FROM VtPedidoEntity vtVentaEntity " +
                    "INNER JOIN GeTerceroEntity clienteEntity ON vtVentaEntity.cliente.idTercero = clienteEntity.idTercero " +
                    "WHERE ( vtVentaEntity.idData = :idData)  AND " +
                    "(vtVentaEntity.idEmpresa = :idEmpresa) AND " +
                    "(:serie IS NULL OR vtVentaEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fechaEmision <= :fechaEmisionHasta )"
    )
    TotalCabeceraProjection totalCabecera(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String serie, String secuencial);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM vt_pedidos vtVentaEntity " +
                    "INNER JOIN vt_pedidos_valores valoresEntity ON vtVentaEntity.id_pedido = valoresEntity.id_pedido " +
                    "WHERE ( vtVentaEntity.id_data = :idData)  AND " +
                    "(vtVentaEntity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR vtVentaEntity.sucursal = :sucursal) AND " +
                    "(:numeroIdentificacion IS NULL OR vtVentaEntity.numero_identificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR vtVentaEntity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR vtVentaEntity.secuencial = :secuencial ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR vtVentaEntity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR vtVentaEntity.fecha_emision <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);

    @Query(value = "SELECT vtVentasEntity " +
            "FROM VtPedidoEntity vtVentasEntity " +
            "WHERE vtVentasEntity.idData = :idData  AND " +
            "vtVentasEntity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR vtVentasEntity.sucursal = :sucursal) AND " +
            "(" +
            "(:numeroIdentificacion IS NULL OR vtVentasEntity.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:serie IS NULL OR vtVentasEntity.serie = :serie) AND " +
            "(:secuencial IS NULL OR vtVentasEntity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR vtVentasEntity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR vtVentasEntity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
    )
    List<VtPedidoEntity> findAll(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String numeroIdentificacion, String serie, String secuencial);


}
