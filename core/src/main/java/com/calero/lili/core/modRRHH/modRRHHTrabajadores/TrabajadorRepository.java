package com.calero.lili.core.modRRHH.modRRHHTrabajadores;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrabajadorRepository extends JpaRepository<TrabajadorEntity, UUID> {

    @Query("SELECT c FROM TrabajadorEntity c WHERE c.tercero.idTercero = :idTercero")
    Optional<TrabajadorEntity> getForFindByIdTercero(@Param("idTercero") UUID idTercero);

    @Query("SELECT c FROM TrabajadorEntity c WHERE c.idTrabajador = :idTrabajador")
    Optional<TrabajadorEntity> getForFindById(@Param("idTrabajador") UUID idTrabajador);

}
