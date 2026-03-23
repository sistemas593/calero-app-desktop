package com.calero.lili.core.modLocalidades.modCantones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CantonRepository extends JpaRepository<CantonEntity, UUID> {

    @Query("SELECT c FROM CantonEntity c")
    List<CantonEntity> getFindAll();

    @Query("SELECT c FROM CantonEntity c WHERE c.codigoCanton = :idCanton")
    CantonEntity getForFindById(@Param("idCanton") String idCanton);


    @Query(
            value = "SELECT c FROM CantonEntity c WHERE  c.provincia.codigoProvincia = :codProvincia")
    Page<CantonEntity> findAllPaginate(@Param("codProvincia") String codProvincia, Pageable pageable);


}