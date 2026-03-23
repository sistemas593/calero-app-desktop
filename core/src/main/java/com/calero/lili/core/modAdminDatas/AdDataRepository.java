package com.calero.lili.core.modAdminDatas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface AdDataRepository extends JpaRepository<AdDataEntity, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM AdDataEntity e WHERE e.idData = :idData ")
    void deleteByIdData(@Param("idData") Long idData);

    Optional<AdDataEntity> findByIdData(@Param("idData") Long idData);

    // findfirstByIdDataProj
    // BUSCAR registro por id PARA DEVOLVER AL USUARIO LA INFO en projection
   /*
    @Query(value = "SELECT id_data as idData, data as data " +
            "FROM ad_datas " +
            "where id_data = :idData " +
            "LIMIT 1", nativeQuery = true)
    Optional<AdDataProjection> findfirstByIdDataProj(Long idData);
    */

    // findAllPaginate
    // buscar registros por filtros para devolver al usuario
    @Query(
            value = "SELECT entity " +
                    "FROM AdDataEntity entity " +
                    "WHERE " +
                    "(:filter IS NULL OR LOWER(entity.data) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) "
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdDataEntity entity " +
                    "WHERE " +
                    "(:filter IS NULL OR LOWER(entity.data) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) "
    )
    Page<AdDataEntity> findAllPaginate(@Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);
}
