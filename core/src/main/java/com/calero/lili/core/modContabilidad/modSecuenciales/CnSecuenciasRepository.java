package com.calero.lili.core.modContabilidad.modSecuenciales;

import com.calero.lili.core.modContabilidad.modSecuenciales.projection.CnSecuenciasProjection;
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
public interface CnSecuenciasRepository extends JpaRepository<CnSecuenciasEntity, UUID> {

    @Query(value = "SELECT entity " +
            "FROM CnSecuenciasEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idSecuencia = :idSecuencia ")
    Optional<CnSecuenciasEntity> findById(@Param("idData") Long idData,
                                          @Param("idEmpresa") Long idEmpresa,
                                          @Param("idSecuencia") UUID idSecuencia);


    @Query(
            value = """
                    SELECT entity
                    FROM CnSecuenciasEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """,
            countQuery = """
                    SELECT COUNT(entity)
                    FROM CnSecuenciasEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """)
    Page<CnSecuenciasEntity> getFindAllPaginate(@Param("idData") Long idData,
                                                @Param("idEmpresa") Long idEmpresa,
                                                Pageable pageable);


    @Query(value = "SELECT entity.id_secuencia " +
            "FROM cn_secuencias entity " +
            "where entity.id_data = :idData and " +
            "entity.id_empresa = :idEmpresa and " +
            "entity.sucursal = :sucursal and " +
            "entity.anio = :anio and " +
            "entity.mes = :mes", nativeQuery = true)
    Optional<CnSecuenciasProjection> findByAnioMesSucursal(@Param("idData") Long idData,
                                                           @Param("idEmpresa") Long idEmpresa,
                                                           @Param("sucursal") String sucursal,
                                                           @Param("anio") Integer anio,
                                                           @Param("mes") Integer mes);


}
