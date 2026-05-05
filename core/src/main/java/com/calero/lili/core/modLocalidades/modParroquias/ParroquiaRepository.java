package com.calero.lili.core.modLocalidades.modParroquias;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParroquiaRepository extends JpaRepository<ParroquiaEntity, String> {


    @Query("SELECT c FROM ParroquiaEntity c WHERE c.codigoParroquia = :codigoParroquia")
    Optional<ParroquiaEntity> getForFindById(@Param("codigoParroquia") String codigoParroquia);

    @Query("""
            SELECT c 
            FROM ParroquiaEntity c 
            WHERE (:codigoCanton IS NULL OR c.canton.codigoCanton = :codigoCanton)
            AND (:filterContent IS NULL OR LOWER(c.parroquia) LIKE LOWER(CONCAT('%', :filterContent, '%'))) """)
    List<ParroquiaEntity> getFindAll(
            @Param("codigoCanton") String codigoCanton,
            @Param("filterContent") String filterContent);


    /*@Query(value = "SELECT c FROM ParroquiaEntity c WHERE (:codigoCanton IS NULL OR c.canton.codigoCanton = :codigoCanton)" )
    Page<ParroquiaEntity> findAllPaginate(@Param("codigoCanton") String codigoCanton, Pageable pageable);*/


}
