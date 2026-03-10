package com.calero.lili.core.modAdminEmpresas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdEmpresasRepository extends JpaRepository<AdEmpresaEntity, Long> {

    @Query(value = "SELECT nextval('ad_empresas_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    @Query(value = "SELECT entity " +
            "FROM AdEmpresaEntity entity " +
            "where entity.idData = ?1 and " +
            "entity.idEmpresa = ?2 ")
    Optional<AdEmpresaEntity> findById(Long idData, Long idEmpresa);

    @Query(
            value = "SELECT entity " +
                    "FROM AdEmpresaEntity entity " +
                    "WHERE entity.idData = :idData AND " +
                    "(:filter IS NULL OR LOWER(entity.razonSocial) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) AND " +
                    "(:estado IS NULL OR entity.estado = :estado ) "
            ,
            countQuery = "SELECT COUNT(1) " +
                    "FROM AdEmpresaEntity  entity " +
                    "WHERE ( entity.idData = :idData) AND " +
                    "(:filter IS NULL OR LOWER(entity.razonSocial) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.ruc) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) AND " +
                    "(:estado IS NULL OR entity.estado = :estado ) "
    )
    Page<AdEmpresaEntity> findAllPaginate(Long idData, String filter, String filterContent, Integer estado, Pageable pageable);

    @Query(value = "SELECT entity " +
            "FROM AdEmpresaEntity entity " +
            "WHERE entity.ruc = :ruc ")
    Optional<AdEmpresaEntity> findByRuc(String ruc);

}
