package com.calero.lili.api.modAdminEmpresasSeries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdEmpresasSeriesRepository extends JpaRepository<AdEmpresasSeriesEntity, UUID> {


    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSeriesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.idSerie = :idSerie ")
    Optional<AdEmpresasSeriesEntity> findById(Long idData, Long idEmpresa, UUID idSerie);

    @Query(value = "SELECT entity " +
            "FROM AdEmpresasSeriesEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.serie = :serie ")
    Optional<AdEmpresasSeriesEntity> findBySerie(Long idData, Long idEmpresa, String serie);

    @Query(
            value = "SELECT entity "+
                    "FROM AdEmpresasSeriesEntity entity "+
                    "WHERE (entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) and " +
                    "(" +
                    "(:filter IS NULL OR LOWER(entity.serie) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.nombreComercial) LIKE LOWER(CONCAT('%', :filterContent, '%')))" +
                    ") ",
            countQuery = "SELECT COUNT(1) "+
                    "FROM AdEmpresasSeriesEntity   entity "+
                    "WHERE (entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) and " +
                    "(" +
                    ":filter IS NULL OR LOWER(entity.serie) LIKE LOWER(CONCAT('%', :filterContent, '%')) OR " +
                    ":filter IS NULL OR LOWER(entity.nombreComercial) LIKE LOWER(CONCAT('%', :filterContent, '%'))" +
                    ") ")
    Page<AdEmpresasSeriesEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent, Pageable pageable);
}
