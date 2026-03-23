package com.calero.lili.core.modTerceros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeTercerosGruposProveedoresRepository extends JpaRepository<GeTercerosGruposProveedoresEntity, UUID> {

    @Query("select e from GeTercerosGruposProveedoresEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.tercero.idTercero = :idTercero")
    List<GeTercerosGruposProveedoresEntity> findByIdTercero(@Param("idData") Long idData,
                                                            @Param("idEmpresa") Long idEmpresa,
                                                            @Param("idTercero") UUID idTercero);

    @Query("SELECT e FROM GeTercerosGruposProveedoresEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.tercero.idTercero = :idTercero ")
    Optional<GeTercerosGruposProveedoresEntity> findByDataTercero(@Param("idData") Long idData,
                                                                  @Param("idEmpresa") Long idEmpresa,
                                                                  @Param("idTercero") UUID idTercero);


}


