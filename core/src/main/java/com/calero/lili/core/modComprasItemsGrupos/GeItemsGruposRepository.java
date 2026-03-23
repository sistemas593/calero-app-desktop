package com.calero.lili.core.modComprasItemsGrupos;

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
public interface GeItemsGruposRepository extends JpaRepository<GeItemGrupoEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeItemGrupoEntity e WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idGrupo = :idGrupo ")
    void deleteByIdGrupo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idGrupo") UUID idGrupo);

    @Query("SELECT e FROM GeItemGrupoEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idGrupo = :idGrupo ")
    GeItemGrupoEntity findByIdGrupo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idGrupo") UUID idGrupo);


    @Query(
            value = "SELECT entity " +
                    "FROM GeItemGrupoEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.grupo) LIKE LOWER(CONCAT('%', :filterContent, '%'))  " +
                    ")"
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM GeItemGrupoEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.grupo) LIKE LOWER(CONCAT('%', :filterContent, '%'))  " +
                    ")"

    )
    Page<GeItemGrupoEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);


    @Query("SELECT e FROM GeItemGrupoEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.grupo = :grupo")
    GeItemGrupoEntity findByNameGrupo(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("grupo") String grupo);
}
