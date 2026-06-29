package com.calero.lili.core.modTesoreria.modTesoreriaCajas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TsCajasRepository extends JpaRepository<TsCajasEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM TsCajasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.codigoCaja = :codigoCaja ")
    Optional<TsCajasEntity> findByCodigoCaja(@Param("idData") Long idData,
                                             @Param("idEmpresa") Long idEmpresa,
                                             @Param("codigoCaja") String codigoCaja);


    @Query(value = "SELECT entity " +
            "FROM TsCajasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idCaja = :idCaja ")
    Optional<TsCajasEntity> findById(@Param("idData") Long idData,
                                     @Param("idEmpresa") Long idEmpresa,
                                     @Param("idCaja") UUID idCaja);


    @Query(
            value = """
                    SELECT entity
                    FROM TsCajasEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """,
            countQuery = """
                    SELECT COUNT(entity)
                    FROM TsCajasEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """)
    Page<TsCajasEntity> findAllPaginate(@Param("idData") Long idData,
                                        @Param("idEmpresa") Long idEmpresa,
                                        Pageable pageable);


}
