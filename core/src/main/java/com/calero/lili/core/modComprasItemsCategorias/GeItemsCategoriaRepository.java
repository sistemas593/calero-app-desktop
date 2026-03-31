package com.calero.lili.core.modComprasItemsCategorias;

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
public interface GeItemsCategoriaRepository extends JpaRepository<GeItemsCategoriaEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeItemsCategoriaEntity e " +
            "WHERE e.idData = :idData  AND e.idCategoria = :id")
    void deleteByMedida(@Param("idData") Long idData, @Param("id") UUID id);

    @Query("SELECT e " +
            "FROM GeItemsCategoriaEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idCategoria = :id")
    GeItemsCategoriaEntity findById(@Param("idData") Long idData, @Param("id") UUID id);

    @Query(
            value = "SELECT entity " +
                    "FROM GeItemsCategoriaEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.categoria) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
            ,
            countQuery = "SELECT COUNT(1) FROM GeItemsCategoriaEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.categoria) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
    )
    Page<GeItemsCategoriaEntity> findAllPaginate(@Param("idData") Long idData, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);


    @Query("SELECT e " +
            "FROM GeItemsCategoriaEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.categoria = :categoria")
    GeItemsCategoriaEntity findByName(@Param("idData") Long idData, @Param("categoria") String categoria);

}