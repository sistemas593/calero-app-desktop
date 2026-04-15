package com.calero.lili.core.modAdminEmpresasSucursales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AdEmpresasSucursalesRepository extends JpaRepository<AdEmpresasSucursalesEntity, Long> {


    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSucursalesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.sucursal = :sucursal")
    Optional<AdEmpresasSucursalesEntity> findfirstByIdDataAndIdEmpresaAAndSucursal(@Param("idData") Long idData,
                                                                                   @Param("idEmpresa") Long idEmpresa,
                                                                                   @Param("sucursal") String sucursal);

    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSucursalesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idSucursal = :idSucursal")
    Optional<AdEmpresasSucursalesEntity> findById(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("idSucursal") UUID idSucursal);

    @Query(
            value = "SELECT entity " +
                    "FROM AdEmpresasSucursalesEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.sucursal) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.nombreSucursal) LIKE LOWER(CONCAT('%', :filterContent, '%'))  " +
                    ")",
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdEmpresasSucursalesEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.sucursal) LIKE LOWER(CONCAT('%', :filterContent, '%')) or " +
                    ":filter IS NULL OR LOWER(entity.nombreSucursal) LIKE LOWER(CONCAT('%', :filterContent, '%'))  " +
                    ")"

    )
    Page<AdEmpresasSucursalesEntity> findAllPaginate(@Param("idData") Long idData,
                                                     @Param("idEmpresa") Long idEmpresa,
                                                     @Param("filter") String filter,
                                                     @Param("filterContent") String filterContent,
                                                     Pageable pageable);
}
