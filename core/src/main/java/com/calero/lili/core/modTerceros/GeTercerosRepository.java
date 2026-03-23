package com.calero.lili.core.modTerceros;

import com.calero.lili.core.modTerceros.projections.GeTerceroProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeTercerosRepository extends JpaRepository<GeTerceroEntity, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeTerceroEntity e WHERE e.idData = :idData AND e.idTercero = :idTercero")
    void deleteByIdCliente(@Param("idData") Long idData, @Param("idTercero") UUID idTercero);

    @Query(value = "SELECT entity " +
            "FROM GeTerceroEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idTercero = :idTercero")
    Optional<GeTerceroEntity> findByIdCliente(@Param("idData") Long idData, @Param("idTercero") UUID idTercero);

    @Query(value = "SELECT id_tercero as idTercero " +
            "FROM ge_terceros entity " +
            "where entity.id_data = :idData and " +
            "entity.numero_identificacion = :numeroIdentificacion", nativeQuery = true)
    Optional<GeTerceroProjection> findExistByNumeroIdentificacion(@Param("idData") Long idData,
                                                                  @Param("numeroIdentificacion") String numeroIdentificacion);


    @Query(value = "SELECT entity " +
            "FROM GeTerceroEntity entity " +
            "where entity.idData = :idData and " +
            "entity.numeroIdentificacion = :numeroIdentificacion")
    Optional<GeTerceroEntity> getFindExistByNumeroIdentificacion(@Param("idData") Long idData, @Param("numeroIdentificacion") String numeroIdentificacion);


    @Query(
            value = "SELECT DISTINCT entity " +
                    "FROM GeTerceroEntity entity " +
                    "LEFT JOIN FETCH entity.geTercerosTipoEntities " +
                    "WHERE entity.idData = :idData " +
                    "AND ( " +
                    "    :tipo IS NULL OR EXISTS (SELECT 1 FROM GeTercerosTipoEntity gtt WHERE gtt.tercero = entity AND gtt.tipo = :tipo) " +
                    ") " +
                    "AND ( " +
                    "    :filter IS NULL OR " +
                    "    LOWER(entity.tercero) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "    LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "    LOWER(entity.numeroIdentificacion) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")",
            countQuery = "SELECT COUNT(entity) " +
                    "FROM GeTerceroEntity entity " +
                    "WHERE entity.idData = :idData " +
                    "AND ( " +
                    "    :tipo IS NULL OR EXISTS (SELECT 1 FROM GeTercerosTipoEntity gtt WHERE gtt.tercero = entity AND gtt.tipo = :tipo) " +
                    ") " +
                    "AND ( " +
                    "    :filter IS NULL OR " +
                    "    LOWER(entity.tercero) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "    LOWER(entity.email) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    "    LOWER(entity.numeroIdentificacion) LIKE LOWER(CONCAT('%', :filterContent, '%')) " +
                    ")")
    Page<GeTerceroEntity> findAllPaginate(@Param("idData") Long idData,
                                          @Param("filter") String filter,
                                          @Param("filterContent") String filterContent,
                                          @Param("tipo") Integer tipo,
                                          Pageable pageable);


}
