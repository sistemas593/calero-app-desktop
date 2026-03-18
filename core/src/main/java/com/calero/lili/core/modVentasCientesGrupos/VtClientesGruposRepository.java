package com.calero.lili.core.modVentasCientesGrupos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VtClientesGruposRepository extends JpaRepository<VtClienteGrupoEntity, UUID> {



    @Query("select  e from VtClienteGrupoEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idGrupo = :idGrupo")
    Optional<VtClienteGrupoEntity> findByIdGrupo(Long idData, Long idEmpresa, UUID idGrupo);

    @Query(value = "SELECT entity " +
            "FROM VtClienteGrupoEntity entity " +
            "WHERE ( entity.idData = :idData)  AND " +
            "(entity.idEmpresa = :idEmpresa) AND " +
            "(:filter IS NULL OR LOWER(entity.grupo) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) ")
    List<VtClienteGrupoEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent);


    @Query("select  e from VtClienteGrupoEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.predeterminado = :predeterminado")
    Optional<VtClienteGrupoEntity> findByIdPredeterminado(Long idData, Long idEmpresa, Boolean predeterminado);

}
