package com.calero.lili.api.modCxP.XpFacturas;

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
public interface XpFacturasRepository extends JpaRepository<XpFacturasEntity, UUID> {

    @Query("SELECT c FROM XpFacturasEntity c WHERE c.idFactura = :idFactura")
    Optional<XpFacturasEntity> getForFindById(@Param("idFactura") UUID idFactura);


    @Query("SELECT c FROM XpFacturasEntity c " +
            "WHERE c.idFactura = :idFactura " +
            "AND c.idData = :idData " +
            "AND c.idEmpresa = :idEmpresa")
    Optional<XpFacturasEntity> getForFindByIdAndIdDataAndIdEmpresa(@Param("idFactura") UUID idFactura,
                                                                   @Param("idData") Long idData,
                                                                   @Param("idEmpresa") Long idEmpresa);


    @Query(value = "SELECT entity " +
            "FROM XpFacturasEntity entity " +
            "WHERE ( entity.idData = :idData) AND " +
            "(entity.idEmpresa = :idEmpresa) AND " +
            " (:idTercero IS NULL OR entity.proveedor.idTercero = :idTercero) AND "+
            "( cast(:fechaRegistroDesde as date) is null OR entity.fechaRegistro >= :fechaRegistroDesde ) AND " +
            "( cast(:fechaRegistroHasta as date) is null OR entity.fechaRegistro <= :fechaRegistroHasta ) ")
    Page<XpFacturasEntity> findAllPaginate(Long idData, Long idEmpresa, LocalDate fechaRegistroDesde,
                                           LocalDate fechaRegistroHasta,
                                           UUID idTercero,
                                           Pageable pageable);

    @Query("SELECT c FROM XpFacturasEntity c " +
            "WHERE c.idData = :idData " +
            "AND c.idEmpresa = :idEmpresa " +
            "AND c.tipoDocumento = :tipoDocumento " +
            "AND c.serie = :serie " +
            "AND c.secuencial = :secuencial")
    Optional<XpFacturasEntity> findByExistFactura(@Param("idData") Long idData,
                                                  @Param("idEmpresa") Long idEmpresa,
                                                  @Param("tipoDocumento") String tipoDocumento,
                                                  @Param("serie") String serie,
                                                  @Param("secuencial") String secuencial);

}
