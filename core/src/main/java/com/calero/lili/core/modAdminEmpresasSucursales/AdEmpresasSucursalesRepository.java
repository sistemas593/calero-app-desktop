package com.calero.lili.core.modAdminEmpresasSucursales;

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
public interface AdEmpresasSucursalesRepository extends JpaRepository<AdEmpresasSucursalesEntity, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM AdEmpresasSucursalesEntity e WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idSucursal = :idSucursal")
    void deleteById(Long idData, Long idEmpresa, UUID idSucursal);

    @Query(value = "SELECT entity "+
            "FROM AdEmpresasSucursalesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.sucursal = :sucursal")
    Optional<AdEmpresasSucursalesEntity> findfirstByIdDataAndIdEmpresaAAndSucursal(Long idData, Long idEmpresa, String sucursal);

    @Query(value = "SELECT entity "+
            "FROM AdEmpresasSucursalesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idSucursal = :idSucursal")
    Optional<AdEmpresasSucursalesEntity> findById(Long idData, Long idEmpresa, UUID idSucursal);
    @Query(
            value = "SELECT entity "+
                    "FROM AdEmpresasSucursalesEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.sucursal) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR "+
                    ":filter IS NULL OR LOWER(entity.nombreSucursal) LIKE LOWER(CONCAT('%', :filterContent, '%'))  "+
                    ")",
            countQuery = "SELECT COUNT(1) "+
                    "FROM AdEmpresasSucursalesEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.sucursal) LIKE LOWER(CONCAT('%', :filterContent, '%')) or "+
                    ":filter IS NULL OR LOWER(entity.nombreSucursal) LIKE LOWER(CONCAT('%', :filterContent, '%'))  "+
                    ")"

    )
    Page<AdEmpresasSucursalesEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent, Pageable pageable);
}
