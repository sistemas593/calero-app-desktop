package com.calero.lili.core.modComprasItemsMedidas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeItemsMedidasRepository extends JpaRepository<GeItemsMedidasEntity, Long> {


    @Query("SELECT e " +
            "FROM GeItemsMedidasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idUnidadMedida = :id")
    GeItemsMedidasEntity findById(@Param("idData") Long idData, @Param("id") UUID id);

    @Query(
            value = "SELECT entity " +
                    "FROM GeItemsMedidasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.unidadMedida) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
            ,
            countQuery = "SELECT COUNT(1) FROM GeItemsMedidasEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.unidadMedida) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
    )
    Page<GeItemsMedidasEntity> findAllPaginate(@Param("idData") Long idData, @Param("filter") String filter,
                                               @Param("filterContent") String filterContent, Pageable pageable);

    @Query("SELECT e " +
            "FROM GeItemsMedidasEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.unidadMedida = :unidadMedida")
    GeItemsMedidasEntity findByName(@Param("idData") Long idData, @Param("unidadMedida") String unidadMedida);


}