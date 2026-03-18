package com.calero.lili.core.modTerceros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface GeTercerosTipoRepository extends JpaRepository<GeTercerosTipoEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeTercerosTipoEntity e WHERE e.tercero.idTercero = :idTercero")
    void deleteByIdTercero(UUID idTercero);


    @Transactional
    @Modifying
    @Query("DELETE FROM GeTercerosTipoEntity e WHERE e.tercero.idTercero = :idTercero AND e.tipo = :tipo")
    void deleteByIdTerceroAndTipo(UUID idTercero, Integer tipo);

}
