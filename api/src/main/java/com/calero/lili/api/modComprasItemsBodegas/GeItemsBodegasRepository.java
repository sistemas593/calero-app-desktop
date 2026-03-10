package com.calero.lili.api.modComprasItemsBodegas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface GeItemsBodegasRepository extends JpaRepository<IvBodegaEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM IvBodegaEntity e " +
            "WHERE " +
            "e.idData = :idData  AND " +
            "e.idEmpresa = :idEmpresa  AND " +
            "e.idBodega = :id")
    void deleteById(Long idData, Long idEmpresa, UUID id);

    @Query("SELECT e " +
            "FROM IvBodegaEntity e " +
            "WHERE " +
            "e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idBodega = :id")
    IvBodegaEntity findById(Long idData, Long idEmpresa, UUID id);

    @Query(
            value = "SELECT entity " +
                    "FROM IvBodegaEntity entity " +
                    "WHERE " +
                    "entity.idData = :idData AND " +
                    "entity.idEmpresa = :idEmpresa AND " +
                    ":filter IS NULL OR LOWER(entity.bodega) LIKE LOWER(CONCAT('%', :filterContent, '%')) "
            ,
            countQuery = "SELECT COUNT(1) FROM IvBodegaEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "entity.idEmpresa = :idEmpresa AND " +
                    ":filter IS NULL OR LOWER(entity.bodega) LIKE LOWER(CONCAT('%', :filterContent, '%')) "
    )
    Page<IvBodegaEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent, Pageable pageable);
}