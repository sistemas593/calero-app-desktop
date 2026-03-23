package com.calero.lili.core.modVentasVendedores;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface VtVendedoresRepository extends JpaRepository<VtVendedorEntity, UUID> {

//    Optional<VtVendedorEntity> findByIdDataAndIdEmpresaAndIdVendedor(Long idData, Long idEmpresa, UUID idVendedor);

    @Query(
            value = "SELECT entity "+
                    "FROM VtVendedorEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(entity.idVendedor = :idVendedor) "
    )
    Optional<VtVendedorEntity> findById(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("idVendedor") UUID idVendedor);


    @Query(
            value = "SELECT entity "+
                    "FROM VtVendedorEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(:filter IS NULL OR LOWER(entity.vendedor) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
            ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM VtVendedorEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    "(:filter IS NULL OR LOWER(entity.vendedor) LIKE LOWER(CONCAT('%', :filterContent, '%'))) "
    )
    Page<VtVendedorEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);

}
