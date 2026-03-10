package com.calero.lili.api.modTercerosProvedoresParametros;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CpProveedoresParametrosRepository extends JpaRepository<CpProveedoresParametrosEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM CpProveedoresParametrosEntity e WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idParametro = :idParametro")
    void deleteByIdDataAndEmpresaAndIdParametro(Long idData, Long idEmpresa, UUID idParametro);


    @Query(value = "SELECT entity " +
            "FROM CpProveedoresParametrosEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idParametro = :idLiquidacion ")
    Optional<CpProveedoresParametrosEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idLiquidacion);

    @Query(
            value = "SELECT entity " +
                    "FROM CpProveedoresParametrosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)",
            countQuery = "SELECT COUNT(1) " +
                    "FROM CpProveedoresParametrosEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)")
    Page<CpProveedoresParametrosEntity> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable);

}

