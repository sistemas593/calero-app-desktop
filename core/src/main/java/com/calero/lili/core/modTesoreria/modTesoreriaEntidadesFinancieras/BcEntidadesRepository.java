package com.calero.lili.core.modTesoreria.modTesoreriaEntidadesFinancieras;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface BcEntidadesRepository extends JpaRepository<TsEntidadEntity, Long> {


    TsEntidadEntity findByIdDataAndIdEmpresaAndIdEntidad(Long idData, Long idEmpresa, UUID idBalance);

    @Query(
            value = "SELECT entity.idData as idData," +
                    "entity.idEmpresa as idEmpresa, " +
                    "entity.idEntidad as idEntidad," +
                    "entity.tipoEntidad as tipoEntidad," +
                    "entity.entidad as entidad," +
                    "entity.numeroCuenta as numeroCuenta," +
                    "entity.agencia as agencia," +
                    "entity.contacto as contacto," +
                    "entity.telefono1 as telefono1," +
                    "entity.telefono2 as telefono2," +
                    "entity.secuencialCheque as secuencialCheque," +
                    "entity.archivoCheque as archivoCheque," +
                    "entity.saldo as saldo "+
                    "FROM TsEntidadEntity entity "+
                    "WHERE ( entity.idData = :idData) AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    ":tipoEntidad IS NULL OR entity.tipoEntidad = :tipoEntidad AND "+
                    "(" +
                    ":filter IS NULL OR LOWER(entity.entidad) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR "+
                    ":filter IS NULL OR LOWER(entity.numeroCuenta) LIKE LOWER(CONCAT('%', :filterContent, '%'))  "+
                    ")"
            ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TsEntidadEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND " +
                    ":tipoEntidad IS NULL OR entity.tipoEntidad = :tipoEntidad AND "+
                    "(" +
                    ":filter IS NULL OR LOWER(entity.entidad) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR "+
                    ":filter IS NULL OR LOWER(entity.numeroCuenta) LIKE LOWER(CONCAT('%', :filterContent, '%'))  "+
                    ")"

    )
    Page<BcEntidadesProjection> findAllPaginate(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                                @Param("filter") String filter, @Param("filterContent") String filterContent,
                                                @Param("tipoEntidad") String tipoEntidad,
                                                Pageable pageable);

}
