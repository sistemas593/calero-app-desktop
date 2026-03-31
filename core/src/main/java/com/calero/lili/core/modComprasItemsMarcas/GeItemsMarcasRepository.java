package com.calero.lili.core.modComprasItemsMarcas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface GeItemsMarcasRepository extends JpaRepository<GeItemsMarcasEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeItemsMarcasEntity e " +
            "WHERE e.idData = :idData  AND e.idMarca = :id")
    void deleteByMedida(@Param("idData") Long idData, @Param("id") UUID id);

    @Query("SELECT e " +
            "FROM GeItemsMarcasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idMarca = :id")
    GeItemsMarcasEntity findById(@Param("idData") Long idData, @Param("id") UUID id);

    @Query(
            value = "SELECT entity " +
                    "FROM GeItemsMarcasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.marca) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
            ,
            countQuery = "SELECT COUNT(1) FROM GeItemsMarcasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.marca) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
    )
    Page<GeItemsMarcasEntity> findAllPaginate(@Param("idData") Long idData, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);


    @Query("SELECT e " +
            "FROM GeItemsMarcasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.marca = :marca")
    GeItemsMarcasEntity findByName(@Param("idData") Long idData, @Param("marca") String marca);

}