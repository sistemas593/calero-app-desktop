package com.calero.lili.core.modCompras.modComprasImpuestos;

import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.modCompras.impuestosXml.CpImpuestosFacturasOneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.ComprasImpuestoProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.OneProjection;
import com.calero.lili.core.modCompras.modComprasImpuestos.projection.TotalesProjection;
import com.calero.lili.core.modCompras.projection.AtsProjection;
import com.calero.lili.core.modCompras.projection.AtsRetencionResumenProjection;
import com.calero.lili.core.modCompras.service.CpImpuestoAtsProjection;
import com.calero.lili.core.modCompras.service.CpImpuestosCodigosAtsProjection;
import com.calero.lili.core.modCompras.service.CpRetencionesProjection;
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
import java.util.Set;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface CpImpuestosRepository extends JpaRepository<CpImpuestosEntity, UUID>, JpaSpecificationExecutor<CpImpuestosEntity> {

    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idImpuestos = :idLiquidacion AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR entity.createdBy = :usuario)")
    Optional<CpImpuestosEntity> findByIdEntity(@Param("idData") Long idData,
                                               @Param("idEmpresa") Long idEmpresa,
                                               @Param("idLiquidacion") UUID idLiquidacion,
                                               @Param("sucursal") String sucursal,
                                               @Param("usuario") String usuario);


    @Transactional
    @Modifying
    @Query("DELETE FROM CpImpuestosEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idImpuestos = :id")
    void deleteById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("id") UUID id);

    @Query(value = "SELECT id_impuestos as idImpuestos " +
            "FROM cp_impuestos entity " +
            "LEFT JOIN ge_terceros gt on gt.id_tercero = entity.id_proveedor " +
            "WHERE (entity.id_Data = :idData)  AND " +
            "(entity.id_Empresa = :idEmpresa) AND " +
            "gt.numero_identificacion = :numeroIdentificacion AND " +
            "entity.serie = :serie AND " +
            "entity.secuencial = :secuencial AND " +
            "entity.numero_autorizacion = :numeroAutorizacion AND " +
            "entity.codigo_sustento = :codigoSustento AND entity.deleted = false " +
            "LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("numeroIdentificacion") String numeroIdentificacion,
                                                  @Param("serie") String serie,
                                                  @Param("secuencial") String secuencial,
                                                  @Param("numeroAutorizacion") String numeroAutorizacion,
                                                  @Param("codigoSustento") String codigoSustento);

    @Query(
            value = "SELECT entity " +
                    "FROM CpImpuestosEntity entity " +
                    "LEFT JOIN entity.tercero gt " +
                    "WHERE ( entity.idData = :idData) AND " +
                    "(entity.idEmpresa = :idEmpresa) AND entity.deleted = false AND " +
                    "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
                    "(:usuario IS NULL OR entity.createdBy = :usuario) AND " +
                    "(:documento IS NULL OR entity.documento = :documento) AND " +
                    "(:numeroIdentificacion IS NULL OR gt.numeroIdentificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR entity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR entity.numeroAutorizacion = :numeroAutorizacion ) AND " +
                    "(:destino IS NULL OR entity.destino = :destino ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta ) AND " +
                    "( cast(:fechaRegistroDesde as date) is null OR entity.fechaRegistro >= :fechaRegistroDesde ) AND " +
                    "( cast(:fechaRegistroHasta as date) is null OR entity.fechaRegistro <= :fechaRegistroHasta ) "
    )
    Page<CpImpuestosEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                            @Param("sucursal") String sucursal,
                                            @Param("documento") DocumentoEnum documento,
                                            @Param("numeroIdentificacion") String numeroIdentificacion,
                                            @Param("serie") String serie,
                                            @Param("secuencial") String secuencial,
                                            @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                            @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                            @Param("fechaRegistroDesde") LocalDate fechaRegistroDesde,
                                            @Param("fechaRegistroHasta") LocalDate fechaRegistroHasta,
                                            @Param("numeroAutorizacion") String numeroAutorizacion,
                                            @Param("destino") String destino,
                                            @Param("usuario") String usuario,
                                            Pageable pageable);


    @Query(
            value = "SELECT " +
                    "valoresEntity.codigo as codigo, " +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible," +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM cp_impuestos entity " +
                    "LEFT JOIN ge_terceros gt on gt.id_tercero = entity.id_proveedor " +
                    "INNER JOIN cp_impuestos_valores valoresEntity ON entity.id_impuestos = valoresEntity.id_impuestos " +
                    "WHERE ( entity.id_data = :idData) AND " +
                    "(entity.id_empresa = :idEmpresa) AND entity.deleted = false AND " +
                    "(:documento IS NULL OR entity.documento = :documento) AND " +
                    "(:numeroIdentificacion IS NULL OR gt.numero_identificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR entity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR entity.numero_autorizacion = :numeroAutorizacion ) AND " +
                    "(:destino IS NULL OR entity.destino = :destino ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fecha_emision <= :fechaEmisionHasta ) AND " +
                    "( cast(:fechaRegistroDesde as date) is null OR entity.fecha_registro >= :fechaRegistroDesde ) AND " +
                    "( cast(:fechaRegistroHasta as date) is null OR entity.fecha_registro <= :fechaRegistroHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje ", nativeQuery = true
    )
    List<TotalesProjection> totalValores(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                         @Param("documento") String documento,
                                         @Param("numeroIdentificacion") String numeroIdentificacion,
                                         @Param("serie") String serie,
                                         @Param("secuencial") String secuencial,
                                         @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                         @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                         @Param("fechaRegistroDesde") LocalDate fechaRegistroDesde,
                                         @Param("fechaRegistroHasta") LocalDate fechaRegistroHasta,
                                         @Param("numeroAutorizacion") String numeroAutorizacion,
                                         @Param("destino") String destino);


    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "LEFT JOIN entity.tercero gt " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "(" +
            "(:numeroIdentificacion IS NULL OR gt.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:serie IS NULL OR entity.serie = :serie) AND " +
            "(:secuencial IS NULL OR entity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )  " +
            ")"
    )
    List<CpImpuestosEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                    @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                    @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                    @Param("numeroIdentificacion") String numeroIdentificacion,
                                    @Param("serie") String serie,
                                    @Param("secuencial") String secuencial);

    @Query(value = "SELECT entity.id_impuestos as idImpuestos " +
            "FROM cp_impuestos entity " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND entity.deleted = false AND " +
            "entity.numero_autorizacion = :numeroAutorizacion", nativeQuery = true)
    Optional<CpImpuestosFacturasOneProjection> findExistByNumeroAutorizacion(@Param("idData") Long idData,
                                                                             @Param("idEmpresa") Long idEmpresa,
                                                                             @Param("numeroAutorizacion") String numeroAutorizacion);

    @Query(value = "SELECT " +
            "entity.id_impuestos as idImpuestos,  " +
            "entity.numero_autorizacion as numeroAutorizacion, " +
            "entity.fecha_autorizacion as fechaAutorizacion, " +
            "entity.documento as documento, " +
            "entity.destino as destino, " +
            "entity.comprobante as comprobante, " +
            "entity.serie as serie, " +
            "entity.secuencial as secuencial, " +
            "gt.numero_identificacion as numeroIdentificacion " +
            "FROM cp_impuestos  entity " +
            "LEFT JOIN ge_terceros gt on gt.id_tercero = entity.id_proveedor " +
            "WHERE (entity.id_data = :idData)  AND " +
            "(entity.id_empresa = :idEmpresa) AND entity.deleted = false AND " +
            "entity.id_impuestos = :id ", nativeQuery = true)
    Optional<CpImpuestosFacturasOneProjection> findXMLById(@Param("idData") Long idData,
                                                           @Param("idEmpresa") Long idEmpresa,
                                                           @Param("id") UUID id);

    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "WHERE (entity.idData = :idData)  AND " +
            "(entity.idEmpresa = :idEmpresa) AND " +
            "entity.idImpuestos = :id ")
    Optional<CpImpuestosEntity> findById(@Param("idData") Long idData,
                                         @Param("idEmpresa") Long idEmpresa,
                                         @Param("id") UUID id);


    @Query(
            value = "SELECT entity " +
                    "FROM CpImpuestosEntity entity " +
                    "LEFT JOIN entity.tercero gt " +
                    "WHERE ( entity.idData = :idData) AND " +
                    "(:idEmpresa IS NULL OR entity.idEmpresa = :idEmpresa) AND " +
                    "(:documento IS NULL OR entity.documento = :documento) AND " +
                    "(:numeroIdentificacion IS NULL OR gt.numeroIdentificacion = :numeroIdentificacion ) AND " +
                    "(:serie IS NULL OR entity.serie = :serie ) AND " +
                    "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND " +
                    "(:numeroAutorizacion IS NULL OR entity.numeroAutorizacion = :numeroAutorizacion ) AND " +
                    "(:destino IS NULL OR entity.destino = :destino ) AND " +
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta ) AND " +
                    "( cast(:fechaRegistroDesde as date) is null OR entity.fechaRegistro >= :fechaRegistroDesde ) AND " +
                    "( cast(:fechaRegistroHasta as date) is null OR entity.fechaRegistro <= :fechaRegistroHasta )"

    )
    List<CpImpuestosEntity> findAll(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                    @Param("documento") DocumentoEnum documento,
                                    @Param("numeroIdentificacion") String numeroIdentificacion,
                                    @Param("serie") String serie,
                                    @Param("secuencial") String secuencial,
                                    @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                    @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                    @Param("fechaRegistroDesde") LocalDate fechaRegistroDesde,
                                    @Param("fechaRegistroHasta") LocalDate fechaRegistroHasta,
                                    @Param("numeroAutorizacion") String numeroAutorizacion,
                                    @Param("destino") String destino);


    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "LEFT JOIN FETCH entity.codigosEntity " +
            "WHERE entity.idData = :idData " +
            "AND entity.idEmpresa = :idEmpresa " +
            "AND entity.idImpuestos IN :listIdFacturas")
    List<CpImpuestosEntity> findByListFacturasIdList(@Param("idData") Long idData,
                                                     @Param("idEmpresa") Long idEmpresa,
                                                     @Param("listIdFacturas") List<UUID> listIdFacturas);

    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "WHERE entity.idData = :idData " +
            "AND entity.idEmpresa = :idEmpresa " +
            "AND entity.idParent = :idParent")
    List<CpImpuestosEntity> findByIdParent(@Param("idData") Long idData,
                                           @Param("idEmpresa") Long idEmpresa,
                                           @Param("idParent") UUID idParent);


    @Query(value = """
            SELECT
                ci.documento AS documento,
            
                SUM(CASE 
                    WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '0' 
                    THEN civ.base_imponible ELSE 0 
                END) AS biTarifaCero,
            
                SUM(CASE 
                    WHEN civ.codigo = '2' AND civ.codigo_porcentaje IN ('2', '4', '5', '8', '3', '10') 
                    THEN civ.base_imponible ELSE 0 
                END) AS biBaseDiferenteCero,
            
                SUM(CASE 
                    WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '6' 
                    THEN civ.base_imponible ELSE 0 
                END) AS biBaseNoObjectoIva,
            
                SUM(CASE 
                    WHEN civ.codigo = '2' AND civ.codigo_porcentaje IN ('2', '4', '5', '8', '3', '10') 
                    THEN civ.valor ELSE 0 
                END) AS valorIva,
            
                COUNT(*) AS totalRegistros
            
            FROM cp_impuestos ci
            JOIN cp_impuestos_valores civ ON ci.id_impuestos = civ.id_impuestos
            WHERE ci.fecha_registro >= :fechaRegistroDesde 
              AND ci.fecha_registro <= :fechaRegistroHasta
              AND ci.id_data = :idData 
              AND ci.id_empresa = :idEmpresa
              AND ci.deleted = false
            GROUP BY ci.documento
            """, nativeQuery = true)
    List<AtsProjection> obtenerResumenCompras(@Param("idData") Long idData,
                                              @Param("idEmpresa") Long idEmpresa,
                                              @Param("fechaRegistroDesde") LocalDate fechaRegistroDesde,
                                              @Param("fechaRegistroHasta") LocalDate fechaRegistroHasta
    );


    @Query(value = """
            SELECT
                cic.codigo_retencion AS codigoRetencion,
                trc.nombre_retencion AS conceptoRetencion,
                SUM(cic.base_imponible) AS baseImponible, 
                SUM(cic.valor_retenido) AS valorRetenido,
                COUNT(*) AS registros
            FROM cp_impuestos ci
            JOIN cp_impuestos_codigos cic ON ci.id_impuestos = cic.id_impuestos
            LEFT JOIN tb_retenciones_codigos trc ON trc.codigo_retencion = cic.codigo_retencion
            WHERE ci.fecha_registro >= :fechaInicio
              AND ci.fecha_registro <= :fechaFin
              AND ci.id_data = :idData
              AND ci.id_empresa = :idEmpresa
              AND ci.deleted = false
              AND cic.codigo = 'RTA'
            GROUP BY cic.codigo_retencion, trc.nombre_retencion
            """, nativeQuery = true)
    List<AtsRetencionResumenProjection> obtenerRetencionesRenta(@Param("idData") Long idData,
                                                                @Param("idEmpresa") Long idEmpresa,
                                                                @Param("fechaInicio") LocalDate fechaInicio,
                                                                @Param("fechaFin") LocalDate fechaFin);


    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "LEFT JOIN entity.tercero gt " +
            "WHERE entity.serie = :serie " +
            "AND entity.secuencial = :secuencial " +
            "AND gt.numeroIdentificacion = :numeroIdentificacion")
    CpImpuestosEntity findBySerieAndSecuencialAndNumeroIdentificacion(@Param("serie") String serie,
                                                                      @Param("secuencial") String secuencial,
                                                                      @Param("numeroIdentificacion") String numeroIdentificacion);


    @Query(value = "SELECT ci.id_impuestos AS idImpuestos, ci.codigo_sustento AS codigoSustento," +
            " gt.tipo_identificacion AS tipoIdProv, gt.numero_identificacion AS idProv," +
            " ci.documento AS tipoComprobante, ci.fecha_registro AS fechaRegistro," +
            " ci.serie AS serie, ci.secuencial AS secuencial," +
            " ci.fecha_emision AS fechaEmision, ci.numero_autorizacion AS autorizacion," +
            " ci.pago_exterior as pagoExterior," +
            " ci.pago_loc_ext as pagoLocExt," +
            " ci.formas_pago_sri as formasPago," +
            " ci.id_retencion as idRetencion, " +
            " COALESCE(SUM(CASE WHEN civ.codigo = '2' AND " +
            "civ.codigo_porcentaje = '6' THEN civ.base_imponible ELSE 0 END), 0) AS baseNoGraIva," +
            " COALESCE(SUM(CASE WHEN civ.codigo = '2'" +
            " AND civ.codigo_porcentaje = '0' THEN civ.base_imponible ELSE 0 END), 0) AS baseImponible," +
            " COALESCE(SUM(CASE WHEN civ.codigo = '2' AND" +
            " civ.codigo_porcentaje IN ('2','4','5','8') THEN civ.base_imponible ELSE 0 END), 0) AS baseImpGrav," +
            " COALESCE(SUM(CASE WHEN civ.codigo = '2' " +
            "AND civ.codigo_porcentaje = '7' THEN civ.base_imponible ELSE 0 END), 0) AS baseImpExe," +
            " COALESCE(SUM(CASE WHEN civ.codigo = '2' " +
            "AND civ.codigo_porcentaje IN ('4','5','8') THEN civ.valor ELSE 0 END), 0) AS montoIva " +
            "FROM cp_impuestos ci JOIN cp_impuestos_valores civ ON civ.id_impuestos = ci.id_impuestos" +
            " JOIN ge_terceros gt ON gt.id_tercero = ci.id_proveedor " +
            "WHERE ci.id_data = :idData AND ci.id_empresa = :idEmpresa " +
            "AND (cast(:fechaRegistroDesde as date) IS NULL OR ci.fecha_registro >= :fechaRegistroDesde) " +
            "AND (cast(:fechaRegistroHasta as date) IS NULL OR ci.fecha_registro <= :fechaRegistroHasta) " +
            "GROUP BY ci.id_impuestos, ci.codigo_sustento, gt.tipo_identificacion, " +
            "gt.numero_identificacion, ci.documento, ci.fecha_registro, ci.serie, ci.secuencial," +
            " ci.fecha_emision, ci.numero_autorizacion, ci.pago_exterior," +
            " ci.formas_pago_sri",
            nativeQuery = true)
    List<CpImpuestoAtsProjection> findAllByDates(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa,
                                                 @Param("fechaRegistroDesde") LocalDate fechaRegistroDesde,
                                                 @Param("fechaRegistroHasta") LocalDate fechaRegistroHasta);


    @Query(value = "select " +
            "cic.id_impuestos, " +
            "cic.base_imponible, " +
            "cic.porcentaje_retener, " +
            "cic.codigo as codigo, " +
            "cic.codigo_retencion as codigoRetencion, " +
            "cic.valor_retenido as valorRetenido " +
            "from cp_impuestos_codigos cic " +
            "where cic.id_impuestos in (:idCpImpuestos) and " +
            "cic.id_data = :idData and cic.id_empresa = :idEmpresa",
            nativeQuery = true)
    List<CpImpuestosCodigosAtsProjection> findAllCpImpuestosCodigos(@Param("idData") Long idData,
                                                                    @Param("idEmpresa") Long idEmpresa,
                                                                    @Param("idCpImpuestos") List<UUID> idCpImpuestos);


    @Query(value = "select " +
            "cr.id_retencion as idRetencion, " +
            "cr.serie_retencion as serie, " +
            "cr.secuencial_retencion as secuencial, " +
            "cr.numero_autorizacion_retencion as autorizacion, " +
            "cr.fecha_emision_retencion  as fechaRetencion " +
            "from cp_retenciones cr " +
            "where cr.id_retencion in (:idRetenciones) and cr.id_data = :idData and cr.id_empresa = :idEmpresa ",
            nativeQuery = true)
    List<CpRetencionesProjection> findAllCpRetenciones(@Param("idData") Long idData,
                                                       @Param("idEmpresa") Long idEmpresa,
                                                       @Param("idRetenciones") List<UUID> idRetenciones);


    @Query(value = "SELECT ci.fecha_emision, " +
            "ci.fecha_registro, " +
            "coalesce(gt.tercero,'') as tercero, " +
            "coalesce(gt.numero_identificacion,'') as numero_identificacion, " +
            "coalesce(ci.documento,'') as documento, " +
            "coalesce(ci.serie,'') as serie, " +
            "coalesce(ci.numero_autorizacion,'') as numero_autorizacion, " +
            "coalesce(ci.secuencial,'') as secuencial, " +
            "ci.fecha_vencimiento, " +
            "coalesce(ci.concepto,'') as concepto, " +
            "coalesce(ci.devolucion_iva,'') as devolucion_iva, " +
            "coalesce(ci.referencia,'') as referencia, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '0' THEN civ.base_imponible END), 0) AS base_imponible_cero, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '0' THEN civ.tarifa END), 0) AS tarifa_iva_cero, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '0' THEN civ.valor END), 0) AS valor_iva_cero, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '4' THEN civ.base_imponible END), 0) AS base_imponible_quince, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '4' THEN civ.tarifa END), 0) AS tarifa_iva_quince, " +
            "coalesce(MAX(CASE WHEN civ.codigo = '2' AND civ.codigo_porcentaje = '4' THEN civ.valor END), 0) AS valor_iva_quince, " +
            "coalesce(MAX(case when civ.codigo = '2' and civ.codigo_porcentaje = '5' then civ.base_imponible end), 0) as base_imponible_cinco, " +
            "coalesce(MAX(case when civ.codigo = '2' and civ.codigo_porcentaje = '5' then civ.tarifa end), 0) as tarifa_iva_cinco, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '5' then civ.valor end), 0) as valor_iva_cinco, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '8' then civ.base_imponible end), 0) as base_imponible_ocho, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '8' then civ.tarifa end), 0) as tarifa_iva_ocho, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '8' then civ.valor end), 0) as valor_iva_ocho, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '6' then civ.base_imponible end), 0) as base_imponible_no_objecto, " +
            "coalesce(max(case when civ.codigo = '2' and civ.codigo_porcentaje = '7' then civ.base_imponible end), 0) as base_imponible_exento, " +
            "ci.fecha_autorizacion, " +
            "coalesce(cpr.codigo_documento_reemb,'') as codigo_documento_reemb, " +
            "coalesce(cpr.serie_reemb,'') as serie_reemb, " +
            "coalesce(cpr.secuencial_reemb, '') as secuencial_reemb, " +
            "coalesce(cpr.numero_autorizacion_reemb,'') as numero_autorizacion_reemb, " +
            "cpr.fecha_emision_reemb " +
            "FROM cp_impuestos ci " +
            "join cp_impuestos_valores civ on ci.id_impuestos = civ.id_impuestos " +
            "join ge_terceros gt on ci.id_proveedor = gt.id_tercero " +
            "left join cp_impuestos_reembolsos cpr on ci.id_impuestos = cpr.id_impuestos " +
            "where ci.id_data = :idData and ci.id_empresa = :idEmpresa " +
            "and ci.fecha_emision between :fechaDesde and :fechaHasta and ci.deleted = false " +
            "group by ci.fecha_emision, ci.fecha_registro, " +
            "gt.tercero, gt.numero_identificacion, " +
            "ci.documento, ci.serie, " +
            "ci.numero_autorizacion, " +
            "ci.secuencial,ci.fecha_vencimiento, " +
            "ci.concepto,ci.devolucion_iva, " +
            "ci.referencia, ci.fecha_autorizacion, " +
            "cpr.codigo_documento_reemb, cpr.serie_reemb, " +
            "cpr.secuencial_reemb, cpr.numero_autorizacion_reemb, cpr.fecha_emision_reemb;", nativeQuery = true)
    List<ComprasImpuestoProjection> getCompraImpuesto(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "WHERE entity.idData = :idData " +
            "AND entity.idEmpresa = :idEmpresa " +
            "AND entity.retencion.idRetencion = :idRetencion")
    List<CpImpuestosEntity> idRetencion(@Param("idData") Long idData,
                                        @Param("idEmpresa") Long idEmpresa,
                                        @Param("idRetencion") UUID idRetencion);

    @Query(value = "SELECT entity " +
            "FROM CpImpuestosEntity entity " +
            "WHERE (entity.idData = :idData)  AND " +
            "(entity.idEmpresa = :idEmpresa) AND " +
            "entity.idImpuestos in :listIds AND " +
            "entity.retencion.idRetencion is NULL AND " +
            "entity.origen <> 'RCC'")
    List<CpImpuestosEntity> findByInId(@Param("idData") Long idData,
                                       @Param("idEmpresa") Long idEmpresa,
                                       @Param("listIds") List<UUID> listIds);


    @Query(value = """
            SELECT ci.*
            FROM cp_impuestos ci
            INNER JOIN ge_terceros t
                ON t.id_tercero = ci.id_proveedor
            WHERE ci.id_data = :idData
              AND ci.id_empresa = :idEmpresa
              AND CONCAT(
                    t.numero_identificacion,
                    '-',
                    TO_CHAR(ci.fecha_emision, 'DD/MM/YYYY'),
                    '-',
                    ci.documento,
                    '-',
                    ci.serie,
                    '-',
                    ci.secuencial
                  ) IN (:codigos)
            """, nativeQuery = true)
    List<CpImpuestosEntity> findByCodigosCompuestos(@Param("idData") Long idData,
                                                    @Param("idEmpresa") Long idEmpresa,
                                                    @Param("codigos") Set<String> codigos);


}
