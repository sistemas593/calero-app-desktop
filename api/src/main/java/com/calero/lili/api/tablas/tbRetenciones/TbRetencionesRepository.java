package com.calero.lili.api.tablas.tbRetenciones;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbRetencionesRepository extends JpaRepository<TbRetencionEntity, String> {

    @Query(value = "SELECT entity "+
            "FROM TbRetencionEntity entity " +
            "where entity.codigo = :codigo " )

    Optional<TbRetencionEntity> findById(String codigo);

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
    Page<TbRetencionEntity> findAllPaginate(String filter, String filterContent, Pageable pageable);
}
