package com.calero.lili.api.modComprasItemsMedidas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface GeItemsMedidasRepository extends JpaRepository<GeItemsMedidasEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeItemsMedidasEntity e " +
            "WHERE e.idData = :idData  AND e.idUnidadMedida = :id")
    void deleteByMedida(Long idData, UUID id);

    @Query("SELECT e " +
            "FROM GeItemsMedidasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idUnidadMedida = :id")
    GeItemsMedidasEntity findById(Long idData, UUID id);

    @Query(
            value = "SELECT entity " +
                    "FROM GeItemsMedidasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    ":filter IS NULL OR LOWER(entity.unidadMedida) LIKE LOWER(CONCAT('%', :filterContent, '%')) "
            ,
            countQuery = "SELECT COUNT(1) FROM GeItemsMedidasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    ":filter IS NULL OR LOWER(entity.unidadMedida) LIKE LOWER(CONCAT('%', :filterContent, '%')) "
    )
    Page<GeItemsMedidasEntity> findAllPaginate(Long idData, String filter, String filterContent, Pageable pageable);

    @Query("SELECT e " +
            "FROM GeItemsMedidasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.unidadMedida = :unidadMedida")
    GeItemsMedidasEntity findByName(Long idData, String unidadMedida);


}