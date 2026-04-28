package com.calero.lili.core.modCompras.modComprasLiquidaciones.reembolsos;

import com.calero.lili.core.modCompras.impuestosXml.CpLiquidacionReembolsoOneProjection;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LiquidacionReembolsosRepository extends JpaRepository<CpLiquidacionesReembolsosEntity, UUID> {

    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.idData =:idData AND entity.idEmpresa =:idEmpresa " +
            "AND (cast(:fechaEmisionDesde as date) IS NULL OR entity.fechaEmisionReemb >= :fechaEmisionDesde) " +
            "AND (cast(:fechaEmisionHasta as date) IS NULL OR entity.fechaEmisionReemb <= :fechaEmisionHasta)")
    List<CpLiquidacionesReembolsosEntity> getFindAll(@Param("idData") Long idData,
                                                     @Param("idEmpresa") Long idEmpresa,
                                                     @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                                     @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);


    /**
     * El valor de utlizado no debe ser opcional teniendo en cuenta que 0 son para liquidaciones no utilzadas
     * el valor de 2 es para todas
     * y el valor de 1 para las que ya se usan.
     */
    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE (cast(:fechaEmisionDesde as date) IS NULL OR entity.fechaEmisionReemb >= :fechaEmisionDesde) " +
            "AND entity.idData = :idData AND entity.idEmpresa = :idEmpresa " +
            "AND (cast(:fechaEmisionHasta as date) IS NULL OR entity.fechaEmisionReemb <= :fechaEmisionHasta) " +
            "AND (:secuencial IS NULL OR entity.secuencialReemb = :secuencial) " +
            "AND (:numeroIdentificacion IS NULL OR entity.numeroIdentificacionReemb = :numeroIdentificacion) " +
            "AND (:serie IS NULL OR entity.serieReemb = :serie) " +
            "AND (:sucursal IS NULL OR entity.sucursal = :sucursal) " +
            "AND (:usuario IS NULL OR entity.createdBy = :usuario) " +
            "AND ( :utilizado = 2 OR ( :utilizado = 0 AND entity.idLiquidacion IS NULL ) OR ( :utilizado = 1 AND entity.idLiquidacion IS NOT NULL ) )")
    Page<CpLiquidacionesReembolsosEntity> findAllPageable(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                                          @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                                          @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                                          @Param("secuencial") String secuencial,
                                                          @Param("numeroIdentificacion") String numeroIdentificacion,
                                                          @Param("serie") String serie,
                                                          @Param("utilizado") Integer utilizado,
                                                          @Param("sucursal") String sucursal,
                                                          @Param("usuario") String usuario,
                                                          Pageable pageable);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.idLiquidacionReembolsos = :idLiquidacionReembolsos")
    Optional<CpLiquidacionesReembolsosEntity> findByIdLiquidacionReembolsos(@Param("idLiquidacionReembolsos") UUID idLiquidacionReembolsos);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.idLiquidacionReembolsos = :idLiquidacionReembolsos AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idData = :idData AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR entity.createdBy = :usuario)")
    Optional<CpLiquidacionesReembolsosEntity> findByIdEntity(@Param("idData") Long idData,
                                                             @Param("idEmpresa") Long idEmpresa,
                                                             @Param("idLiquidacionReembolsos") UUID idLiquidacionReembolsos,
                                                             @Param("sucursal") String sucursal,
                                                             @Param("usuario") String usuario);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.numeroIdentificacionReemb = :numeroIdentificacionReemb " +
            "AND entity.serieReemb = :serieReemb " +
            "AND entity.secuencialReemb = :secuencialReemb " +
            "AND entity.numeroAutorizacionReemb = :numeroAutorizacionReemb")
    Optional<CpLiquidacionesReembolsosEntity> findByIdentificacionAndDocumentoAndSecuencialAndAutorizacion(@Param("numeroIdentificacionReemb") String numeroIdentificacionReemb, @Param("serieReemb") String serieReemb, @Param("secuencialReemb") String secuencialReemb, @Param("numeroAutorizacionReemb") String numeroAutorizacionReemb);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.secuencialReemb = :secuencialReemb " +
            "AND entity.serieReemb = :serieReemb")
    Optional<CpLiquidacionesReembolsosEntity> findBySecuencialReembAndSerieReemb(@Param("secuencialReemb") String secuencialReemb, @Param("serieReemb") String serieReemb);


    @Query(value = """
                SELECT 
                    clrv.codigo AS codigo,
                    clrv.codigo_porcentaje AS codigoPorcentaje,
                    SUM(clrv.base_imponible) AS totalBaseImponible,
                    SUM(clrv.valor) AS totalValor
                FROM cp_liquidaciones_reembolsos clr
                JOIN cp_liquidaciones_reembolsos_valores clrv 
                    ON clr.id_liquidacion_reembolsos = clrv.id_liquidacion_reembolsos
                WHERE  (clr.id_data = :idData) AND (clr.id_empresa = :idEmpresa) 
                  AND (cast(:fechaEmisionDesde as date) IS NULL OR clr.fecha_emision_reemb::date >= :fechaEmisionDesde)
                  AND (cast(:fechaEmisionHasta as date) IS NULL OR clr.fecha_emision_reemb::date <= :fechaEmisionHasta)
                  AND (:secuencial IS NULL OR clr.secuencial_reemb = :secuencial)
                  AND (:numeroIdentificacion IS NULL OR clr.numero_identificacion_reemb = :numeroIdentificacion)
                  AND (:serie IS NULL OR clr.serie_reemb = :serie)
                  AND ( :utilizado = 2 OR ( :utilizado = 0 AND clr.id_liquidacion IS NULL ) OR ( :utilizado = 1 AND clr.id_liquidacion IS NOT NULL))
                  AND clr.deleted = false
                GROUP BY clrv.codigo, clrv.codigo_porcentaje
                ORDER BY clrv.codigo, clrv.codigo_porcentaje
            """, nativeQuery = true)
    List<TotalesProjection> totalValores(@Param("idData") Long idData,
                                         @Param("idEmpresa") Long idEmpresa,
                                         @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("secuencial") String secuencial,
                                         @Param("numeroIdentificacion") String numeroIdentificacion,
                                         @Param("serie") String serie,
                                         @Param("utilizado") Integer utilizado);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE entity.idData =:idData AND entity.idEmpresa =:idEmpresa AND entity.numeroAutorizacionReemb = :numeroAutorizacionReemb")
    Optional<CpLiquidacionesReembolsosEntity> findByAutorizacion(@Param("idData") Long idData,
                                                                 @Param("idEmpresa") Long idEmpresa,
                                                                 @Param("numeroAutorizacionReemb") String numeroAutorizacionReemb);


    @Query("SELECT entity " +
            "FROM CpLiquidacionesReembolsosEntity entity " +
            "WHERE (:listIdLiquidaciones IS NULL OR entity.idLiquidacionReembolsos IN :listIdLiquidaciones)")
    List<CpLiquidacionesReembolsosEntity> getFindAllByIds(@Param("listIdLiquidaciones") List<UUID> listIdLiquidaciones);


    @Query(value = "SELECT " +
            "entity.id_liquidacion_reembolsos as idLiquidacionReembolso,  " +
            "entity.numero_autorizacion_reemb as numeroAutorizacion, " +
            "entity.comprobante as comprobante, " +
            "entity.serie_reemb as serie, " +
            "entity.secuencial_reemb as secuencial, " +
            "entity.fecha_autorizacion_reemb as fechaAutorizacion , " +
            "entity.numero_identificacion_reemb as numeroIdentificacion "+
            "FROM cp_liquidaciones_reembolsos  entity " +
            "WHERE (entity.id_data = :idData)  AND entity.deleted = false AND " +
            "(entity.id_empresa = :idEmpresa) AND " +
            "entity.id_liquidacion_reembolsos = :id ", nativeQuery = true)
    Optional<CpLiquidacionReembolsoOneProjection> findXMLById(@Param("idData") Long idData,
                                                              @Param("idEmpresa") Long idEmpresa,
                                                              @Param("id") UUID id);

}
