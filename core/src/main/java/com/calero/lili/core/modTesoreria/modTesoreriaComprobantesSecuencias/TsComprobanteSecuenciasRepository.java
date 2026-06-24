package com.calero.lili.core.modTesoreria.modTesoreriaComprobantesSecuencias;

import com.calero.lili.core.enums.TipoComprobanteSecuencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TsComprobanteSecuenciasRepository extends JpaRepository<TsComprobantesSecuenciasEntity, UUID> {

    @Query(value = "SELECT entity " +
            "FROM TsComprobantesSecuenciasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.cajas.idCaja = :idCaja and " +
            "entity.tipo = :tipo and " +
            "entity.anio = :anio ")
    Optional<TsComprobantesSecuenciasEntity> findByIdCajaAndTipoAndAnio(@Param("idData") Long idData,
                                                                        @Param("idEmpresa") Long idEmpresa,
                                                                        @Param("idCaja") UUID idCaja,
                                                                        @Param("tipo") TipoComprobanteSecuencia tipo,
                                                                        @Param("anio") Integer anio);


    @Query(value = "SELECT entity " +
            "FROM TsComprobantesSecuenciasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idComprobanteSecuencia = :idComprobanteSecuencia ")
    Optional<TsComprobantesSecuenciasEntity> findById(@Param("idData") Long idData,
                                                      @Param("idEmpresa") Long idEmpresa,
                                                      @Param("idComprobanteSecuencia") UUID idComprobanteSecuencia);


    @Query(value = "SELECT entity " +
            "FROM TsComprobantesSecuenciasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa")
    List<TsComprobantesSecuenciasEntity> findAll(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa);


}
