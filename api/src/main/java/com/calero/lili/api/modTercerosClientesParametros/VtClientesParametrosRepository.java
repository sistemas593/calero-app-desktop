package com.calero.lili.api.modTercerosClientesParametros;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface VtClientesParametrosRepository extends JpaRepository<VtClienteParametroEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM VtClienteParametroEntity e WHERE e.idData = :idData AND e.idEmpresa =:idEmpresa AND e.idParametro = :idParametro")
    void deleteByIdDataAndIdParametro(Long idData, Long idEmpresa, UUID idParametro);


    @Query(value = "SELECT entity " +
            "FROM VtClienteParametroEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idParametro = :idParametro ")
    Optional<VtClienteParametroEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idParametro);


    @Query(
            value = "SELECT entity " +
                    "FROM VtClienteParametroEntity entity " +
                    "WHERE ( entity.idData = :idData) AND " +
                    "(entity.idEmpresa = :idEmpresa)",
            countQuery = "SELECT COUNT(1) " +
                    "FROM VtClienteParametroEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)")
    Page<VtClienteParametroEntity> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable);

}
