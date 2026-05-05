package com.calero.lili.core.modLocalidades.modCantones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CantonRepository extends JpaRepository<CantonEntity, UUID> {

    @Query("""
            SELECT c 
            FROM CantonEntity c 
            WHERE (:codigoProvincia IS NULL OR c.provincia.codigoProvincia = :codigoProvincia)
            AND (:filterContent IS NULL OR LOWER(c.canton) LIKE LOWER(CONCAT('%', :filterContent, '%'))) """)
    List<CantonEntity> getFindAll(@Param("codigoProvincia") String codigoProvincia,
                                  @Param("filterContent") String filterContent);

    @Query("SELECT c FROM CantonEntity c WHERE c.codigoCanton = :idCanton")
    Optional<CantonEntity> getForFindById(@Param("idCanton") String idCanton);


    /*@Query(
            value = "SELECT c FROM CantonEntity c WHERE  c.provincia.codigoProvincia = :codProvincia")
    Page<CantonEntity> findAllPaginate(@Param("codProvincia") String codProvincia, Pageable pageable);*/


}