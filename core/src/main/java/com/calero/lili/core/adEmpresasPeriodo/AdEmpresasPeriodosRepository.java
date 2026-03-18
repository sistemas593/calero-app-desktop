package com.calero.lili.core.adEmpresasPeriodo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AdEmpresasPeriodosRepository extends JpaRepository<AdEmpresaPeriodoEntity, UUID> {

    @Query(
            value = "SELECT entity " +
                    "FROM AdEmpresaPeriodoEntity entity " +
                    "WHERE (entity.idData = :idData) AND " +
                    "(entity.idEmpresa = :idEmpresa)",
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdEmpresaPeriodoEntity entity " +
                    "WHERE ( entity.idData = :idData) AND " +
                    "(entity.idEmpresa = :idEmpresa)")
    Page<AdEmpresaPeriodoEntity> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable);


    @Query("SELECT c FROM AdEmpresaPeriodoEntity c WHERE c.idPeriodo = :idPeriodo AND c.idData = :idData AND c.idEmpresa = :idEmpresa")
    Optional<AdEmpresaPeriodoEntity> findIdPeriodoAndIdDataAndIdEmpresa(@Param("idPeriodo") UUID idPeriodo,
                                                                       @Param("idData") Long idData,
                                                                       @Param("idEmpresa") Long idEmpresa);

    @Query("SELECT c FROM AdEmpresaPeriodoEntity c WHERE c.idPeriodo = :idPeriodo")
    Optional<AdEmpresaPeriodoEntity> findIdPeriodo(@Param("idPeriodo") UUID idPeriodo);


}
