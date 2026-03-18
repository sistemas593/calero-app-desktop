package com.calero.lili.core.modLocalidades.modProvincias;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProvinciaRepository extends JpaRepository<ProvinciaEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ProvinciaEntity pr WHERE pr.codigoProvincia = :idProvincia")
    void deleteById(String idProvincia);

    @Query("SELECT c FROM ProvinciaEntity c")
    List<ProvinciaEntity> getFindAll();

    @Query("SELECT c FROM ProvinciaEntity c WHERE c.codigoProvincia = :idProvincia")
    ProvinciaEntity getForFindById(@Param("idProvincia") String idProvincia);


    @Query(
            value = "SELECT c FROM ProvinciaEntity c WHERE :filterContent IS NULL OR LOWER(c.provincia) LIKE LOWER(CONCAT('%', :filterContent, '%'))",
            countQuery = "SELECT COUNT(c) FROM ProvinciaEntity c WHERE :filterContent IS NULL OR LOWER(c.provincia) LIKE LOWER(CONCAT('%', :filterContent, '%'))"
    )
    Page<ProvinciaEntity> findAllPaginate(@Param("filterContent") String filterContent, Pageable pageable);

}