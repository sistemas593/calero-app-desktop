package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesMovimientos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TsComprobanteRepository extends JpaRepository<TsComprobantesEntity, UUID> {



    @Query(value = "SELECT entity " +
            "FROM TsComprobantesEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idComprobante = :idComprobante ")
    Optional<TsComprobantesEntity> findByIdEntity(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idComprobante") UUID idComprobante);


    @Query(
            value = "SELECT entity " +
                    "FROM TsComprobantesEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa",
            countQuery = "SELECT COUNT(entity) " +
                    "FROM TsComprobantesEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa")
    Page<TsComprobantesEntity> findAllByIdDataAndIdEmpresa(@Param("idData") Long idData,
                                                           @Param("idEmpresa") Long idEmpresa,
                                                           Pageable pageable);


}
