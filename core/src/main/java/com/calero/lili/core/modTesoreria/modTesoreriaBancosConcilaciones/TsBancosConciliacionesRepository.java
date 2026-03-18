package com.calero.lili.core.modTesoreria.modTesoreriaBancosConcilaciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface TsBancosConciliacionesRepository extends JpaRepository<TsBancosConciliacionesEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM TsBancosConciliacionesEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idConciliacion = :idConciliacion ")
    Optional<TsBancosConciliacionesEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idConciliacion);

    @Query(
            value = "SELECT entity " +
                    "FROM TsBancosConciliacionesEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa",
            countQuery = "SELECT COUNT(entity) " +
                    "FROM TsBancosConciliacionesEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa")
    Page<TsBancosConciliacionesEntity> findAllByIdDataAndIdEmpresa(@Param("idData") Long idData,
                                                                   @Param("idEmpresa") Long idEmpresa,
                                                                   Pageable pageable);

}
