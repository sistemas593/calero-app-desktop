package com.calero.lili.core.modTesoreria.modTesoreriaCierresCajas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TsCajasTpvRepository extends JpaRepository<TsCajasTpvEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM TsCajasTpvEntity e WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idCajaTpv = :idCajaTpv")
    void deleteByIdDataAndEmpresaAndidCajaTpv(Long idData, Long idEmpresa, Long idCajaTpv);

    @Query("SELECT e " +
            "FROM TsCajasTpvEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idCajaTpv = :idCajaTpv")
    TsCajasTpvEntity findByIdCajaTpv(Long idData, Long idEmpresa, UUID idCajaTpv);

    @Query(
            value = "SELECT entity "+
                    "FROM TsCajasTpvEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)",
            countQuery = "SELECT COUNT(1) "+
                    "FROM TsCajasTpvEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa)")
    Page<TsCajasTpvEntity> findAllPaginate(Long idData, Long idEmpresa, Pageable pageable);

}
