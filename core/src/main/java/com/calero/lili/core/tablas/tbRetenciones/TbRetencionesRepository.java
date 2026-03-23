package com.calero.lili.core.tablas.tbRetenciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbRetencionesRepository extends JpaRepository<TbRetencionEntity, String> {

    @Query(value = "SELECT entity "+
            "FROM TbRetencionEntity entity " +
            "where entity.codigo = :codigo " )

    Optional<TbRetencionEntity> findById(@Param("codigo") String codigo);

    @Query(
            value = "SELECT entity "+
                    "FROM TbRetencionEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigo) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.nombreRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) "
                    ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TbRetencionEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigo) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.nombreRetencion) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
                    ""
    )
    Page<TbRetencionEntity> findAllPaginate(@Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);
}
