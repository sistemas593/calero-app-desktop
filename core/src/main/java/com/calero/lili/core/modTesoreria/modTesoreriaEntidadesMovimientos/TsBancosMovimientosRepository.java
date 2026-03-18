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
public interface TsBancosMovimientosRepository extends JpaRepository<TsBancosMovimentosEntity, UUID> {



    @Query(value = "SELECT entity " +
            "FROM TsBancosMovimentosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idMovimiento = :idMovimiento ")
    Optional<TsBancosMovimentosEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idMovimiento);


    @Query(
            value = "SELECT entity " +
                    "FROM TsBancosMovimentosEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa",
            countQuery = "SELECT COUNT(entity) " +
                    "FROM TsBancosMovimentosEntity entity " +
                    "WHERE entity.idData = :idData AND entity.idEmpresa = :idEmpresa")
    Page<TsBancosMovimentosEntity> findAllByIdDataAndIdEmpresa(@Param("idData") Long idData,
                                                               @Param("idEmpresa") Long idEmpresa,
                                                               Pageable pageable);


}
