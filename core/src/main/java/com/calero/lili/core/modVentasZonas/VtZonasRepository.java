package com.calero.lili.core.modVentasZonas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface VtZonasRepository extends JpaRepository<VtZonaEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM VtZonaEntity e WHERE e.idData = ?1 AND e.idEmpresa = ?2 AND e.idZona = ?3")
    void deleteById(Long idData, Long idEmpresa, UUID idZona);

    @Query("SELECT e FROM VtZonaEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idZona = :idZona")
    Optional<VtZonaEntity> findById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idZona") UUID idZona);

    @Query(
            value = "SELECT entity " +
                    "FROM VtZonaEntity entity "+
                    "WHERE ( entity.idData = :idData ) AND" +
                    "(entity.idEmpresa = :idEmpresa ) AND " +
                    "(:filter IS NULL OR LOWER(entity.zona) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
            ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM VtZonaEntity entity "+
                    "WHERE ( entity.idData = :idData) and "+
                    "(entity.idEmpresa = :idEmpresa ) AND " +
                    "(:filter IS NULL OR LOWER(entity.zona) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
    )
    Page<VtZonaEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);

}
