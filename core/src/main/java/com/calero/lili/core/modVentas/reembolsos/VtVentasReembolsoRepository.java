package com.calero.lili.core.modVentas.reembolsos;

import com.calero.lili.core.modVentas.reembolsos.projection.TotalesProjection;
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
public interface VtVentasReembolsoRepository extends JpaRepository<VtVentaReembolsosEntity, UUID> {


    @Query("""
            SELECT r
            FROM VtVentaReembolsosEntity r
            WHERE r.idVentaReembolsos = :idVentaReembolsos""")
    Optional<VtVentaReembolsosEntity> findByIdEntity(@Param("idVentaReembolsos") UUID idVentaReembolsos);


    @Query("""
            SELECT r
            FROM VtVentaReembolsosEntity r
            WHERE r.numeroIdentificacionReemb = :numeroIdentificacionReemb
              AND r.serieReemb = :serieReemb
              AND r.secuencialReemb = :secuencialReemb
            """)
    Optional<VtVentaReembolsosEntity> findByDatosReembolso(@Param("numeroIdentificacionReemb") String numeroIdentificacionReemb,
                                                           @Param("serieReemb") String serieReemb,
                                                           @Param("secuencialReemb") String secuencialReemb);


    /**
     * El valor de utlizado no debe ser opcional teniendo en cuenta que 0 son para liquidaciones no utilzadas
     * el valor de 2 es para todas
     * y el valor de 1 para las que ya se usan.
     */
    @Query("SELECT entity " +
            "FROM VtVentaReembolsosEntity entity " +
            "WHERE (cast(:fechaEmisionDesde as date) IS NULL OR entity.fechaEmisionReemb >= :fechaEmisionDesde) " +
            "AND (cast(:fechaEmisionHasta as date) IS NULL OR entity.fechaEmisionReemb <= :fechaEmisionHasta) " +
            "AND (:secuencial IS NULL OR entity.secuencialReemb = :secuencial) " +
            "AND (:numeroIdentificacion IS NULL OR entity.numeroIdentificacionReemb = :numeroIdentificacion) " +
            "AND (:serie IS NULL OR entity.serieReemb = :serie) " +
            "AND ( :utilizado = 2 OR ( :utilizado = 0 AND entity.idVenta IS NULL ) OR ( :utilizado = 1 AND entity.idVenta IS NOT NULL ) )")
    Page<VtVentaReembolsosEntity> findAllPageable(@Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                                  @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                                  @Param("secuencial") String secuencial,
                                                  @Param("numeroIdentificacion") String numeroIdentificacion,
                                                  @Param("serie") String serie,
                                                  @Param("utilizado") Integer utilizado,
                                                  Pageable pageable);

    @Query("""
            SELECT r
            FROM VtVentaReembolsosEntity r
            WHERE r.idVentaReembolsos in :idsReembolsos
            """)
    List<VtVentaReembolsosEntity> findAllForIds(@Param("idsReembolsos") List<UUID> idsReembolsos);


    @Query(value = """
                SELECT 
                    vtrv.codigo AS codigo,
                    vtrv.codigo_porcentaje AS codigoPorcentaje,
                    SUM(vtrv.base_imponible) AS totalBaseImponible,
                    SUM(vtrv.valor) AS totalValor
                FROM vt_ventas_reembolsos vtr
                JOIN vt_ventas_reembolsos_valores vtrv 
                    ON vtr.id_venta_reembolsos = vtrv.id_venta_reembolsos
                WHERE (cast(:fechaEmisionDesde as date) IS NULL OR vtr.fecha_emision_reemb::date >= :fechaEmisionDesde)
                  AND (cast(:fechaEmisionHasta as date) IS NULL OR vtr.fecha_emision_reemb::date <= :fechaEmisionHasta)
                  AND (:secuencial IS NULL OR vtr.secuencial_reemb = :secuencial)
                  AND (:numeroIdentificacion IS NULL OR vtr.numero_identificacion_reemb = :numeroIdentificacion)
                  AND (:serie IS NULL OR vtr.serie_reemb = :serie)
                GROUP BY vtrv.codigo, vtrv.codigo_porcentaje
                ORDER BY vtrv.codigo, vtrv.codigo_porcentaje
            """, nativeQuery = true)
    List<TotalesProjection> totalValores(@Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("secuencial") String secuencial,
                                         @Param("numeroIdentificacion") String numeroIdentificacion,
                                         @Param("serie") String serie
    );


    @Query("""
            SELECT r
            FROM VtVentaReembolsosEntity r
            WHERE r.numeroAutorizacionReemb = :numeroAutorizacion""")
    Optional<VtVentaReembolsosEntity> findByAutorizacion(@Param("numeroAutorizacion") String numeroAutorizacion);


    @Query("SELECT entity " +
            "FROM VtVentaReembolsosEntity entity " +
            "WHERE (cast(:fechaEmisionDesde as date) IS NULL OR entity.fechaEmisionReemb >= :fechaEmisionDesde) " +
            "AND (cast(:fechaEmisionHasta as date) IS NULL OR entity.fechaEmisionReemb <= :fechaEmisionHasta)")
    List<VtVentaReembolsosEntity> getFindAll(@Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                             @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);


}
