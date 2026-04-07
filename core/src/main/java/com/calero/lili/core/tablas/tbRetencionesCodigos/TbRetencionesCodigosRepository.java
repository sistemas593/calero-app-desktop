package com.calero.lili.core.tablas.tbRetencionesCodigos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbRetencionesCodigosRepository extends JpaRepository<TbRetencionesCodigosEntity, Long> {

    @Query(value = "SELECT entity "+
            "FROM TbRetencionesCodigosEntity entity " +
            "where entity.codigoRetencion = :codigoDocumento " )
    Optional<TbRetencionesCodigosEntity> findByCodigo(@Param("codigoDocumento") String codigoDocumento);

    @Query(
            value = "SELECT entity "+
                    "FROM TbRetencionesCodigosEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.nombreRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) "
                    ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TbRetencionesCodigosEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.nombreRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
                    ""
    )
    Page<TbRetencionesCodigosEntity> findAllPaginate(@Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);
}
