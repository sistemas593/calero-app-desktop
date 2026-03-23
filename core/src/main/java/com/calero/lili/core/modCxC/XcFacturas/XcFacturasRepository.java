package com.calero.lili.core.modCxC.XcFacturas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface XcFacturasRepository extends JpaRepository<XcFacturasEntity, UUID> {

    @Query("SELECT c FROM XcFacturasEntity c WHERE c.idFactura = :idFactura")
    Optional<XcFacturasEntity> getForFindById(@Param("idFactura") UUID idFactura);


    @Query("SELECT c FROM XcFacturasEntity c " +
            "WHERE c.idFactura = :idFactura AND " +
            "c.idData =:idData AND c.idEmpresa  =:idEmpresa AND " +
            "(:sucursal IS NULL OR c.sucursal = :sucursal) AND " +
            "(:usuario IS NULL OR c.createdBy = :usuario)")
    Optional<XcFacturasEntity> findByIdEntity(@Param("idData") Long idData,
                                              @Param("idEmpresa") Long idEmpresa,
                                              @Param("idFactura") UUID idFactura,
                                              @Param("sucursal") String sucursal,
                                              @Param("usuario") String usuario);

    @Query("SELECT c FROM XcFacturasEntity c " +
            "WHERE c.idFactura = :idFactura " +
            "AND c.idData = :idData " +
            "AND c.idEmpresa = :idEmpresa")
    Optional<XcFacturasEntity> getForFindByIdAndIdDataAndIdEmpresa(@Param("idFactura") UUID idFactura,
                                                                   @Param("idData") Long idData,
                                                                   @Param("idEmpresa") Long idEmpresa);


    @Query(value = "SELECT entity " +
            "FROM XcFacturasEntity entity " +
            "WHERE ( entity.idData = :idData) AND " +
            "(entity.idEmpresa = :idEmpresa) AND " +
            " (:idTercero IS NULL OR entity.cliente.idTercero = :idTercero) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta ) ")
    Page<XcFacturasEntity> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa, @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                           @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta,
                                           @Param("idTercero") UUID idTercero,
                                           Pageable pageable);


    @Query("SELECT c FROM XcFacturasEntity c " +
            "WHERE c.idData = :idData " +
            "AND c.idEmpresa = :idEmpresa " +
            "AND c.tipoDocumento = :tipoDocumento " +
            "AND c.serie = :serie " +
            "AND c.secuencial = :secuencial")
    Optional<XcFacturasEntity> findByExistFactura(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("tipoDocumento") String tipoDocumento,
                                                  @Param("serie") String serie,
                                                  @Param("secuencial") String secuencial);

}
