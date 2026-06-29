package com.calero.lili.core.modTesoreria.modTesoriaCajasUsuarios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TsCajasUsuariosRepository extends JpaRepository<TsCajasUsuariosEntity, UUID> {

    @Query(value = "SELECT entity " +
            "FROM TsCajasUsuariosEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.cajas.idCaja = :idCaja and " +
            "entity.idUsuario = :idUsuario")
    Optional<TsCajasUsuariosEntity> findByIdCaja(@Param("idData") Long idData,
                                                 @Param("idEmpresa") Long idEmpresa,
                                                 @Param("idCaja") UUID idCaja,
                                                 @Param("idUsuario") Long idUsuario);


    @Query(value = "SELECT entity " +
            "FROM TsCajasUsuariosEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idCajaUsuario = :idCajaUsuario ")
    Optional<TsCajasUsuariosEntity> findById(@Param("idData") Long idData,
                                             @Param("idEmpresa") Long idEmpresa,
                                             @Param("idCajaUsuario") UUID idCajaUsuario);


    @Query(
            value = """
                    SELECT entity
                    FROM TsCajasUsuariosEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """,
            countQuery = """
                    SELECT COUNT(entity)
                    FROM TsCajasUsuariosEntity entity
                    WHERE entity.idData = :idData
                    AND entity.idEmpresa = :idEmpresa
                    """)
    Page<TsCajasUsuariosEntity> findAllPaginate(@Param("idData") Long idData,
                                                @Param("idEmpresa") Long idEmpresa,
                                                Pageable pageable);

}
