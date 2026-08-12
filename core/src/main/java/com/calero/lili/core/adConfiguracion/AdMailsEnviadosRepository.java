package com.calero.lili.core.adConfiguracion;

import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface AdMailsEnviadosRepository extends JpaRepository<AdMailEnviadosEntity, Long> {


    @Query(
            value = """
                    SELECT e 
                    FROM AdMailEnviadosEntity e
                    WHERE (:clave1 IS NULL OR e.clave1 = :clave1)
                      AND (:codigoDocumento IS NULL OR e.codigoDocumento = :codigoDocumento)
                      AND (:serie IS NULL OR e.serie = :serie)
                      AND (:secuencial IS NULL OR e.secuencial = :secuencial)
                      AND (:correo IS NULL OR e.mailTo LIKE CONCAT('%', :correo, '%'))
                      AND (cast(:fechaInicial AS timestamp) IS NULL OR e.fecha >= :fechaInicial)
                      AND (cast(:fechaFinal AS timestamp) IS NULL OR e.fecha <= :fechaFinal)
                    """,
            countQuery = """
                    SELECT COUNT(e)
                    FROM AdMailEnviadosEntity e
                    WHERE (:clave1 IS NULL OR e.clave1 = :clave1)
                      AND (:codigoDocumento IS NULL OR e.codigoDocumento = :codigoDocumento)
                      AND (:serie IS NULL OR e.serie = :serie)
                      AND (:secuencial IS NULL OR e.secuencial = :secuencial)
                      AND (:correo IS NULL OR e.mailTo LIKE CONCAT('%', :correo, '%'))
                      AND (cast(:fechaInicial AS timestamp) IS NULL OR e.fecha >= :fechaInicial)
                      AND (cast(:fechaFinal AS timestamp) IS NULL OR e.fecha <= :fechaFinal)
                    """
    )
    Page<AdMailEnviadosEntity> findAllPaginate(@Param("clave1") String clave1,
                                               @Param("codigoDocumento") String codigoDocumento,
                                               @Param("serie") String serie,
                                               @Param("secuencial") String secuencial,
                                               @Param("correo") String correo,
                                               @Param("fechaInicial") LocalDateTime fechaInicial,
                                               @Param("fechaFinal") LocalDateTime fechaFinal,
                                               Pageable pageable);


    @Query("SELECT entity FROM AdMailEnviadosEntity entity where entity.mailTo = :correo and entity.serie =:serie" +
            " and entity.secuencial = :secuencial")
    Optional<AdMailEnviadosEntity> findByEmailAndSecuencialAndSerie(@Param("correo") String correo,
                                                                    @Param("serie") String serie,
                                                                    @Param("secuencial") String secuencial);


}
