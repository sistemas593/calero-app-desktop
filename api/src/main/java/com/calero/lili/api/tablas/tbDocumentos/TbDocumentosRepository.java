package com.calero.lili.api.tablas.tbDocumentos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbDocumentosRepository extends JpaRepository<TbDocumentoEntity, String> {

    @Query(value = "SELECT entity "+
            "FROM TbDocumentoEntity entity " +
            "where entity.codigoDocumento = :codigoDocumento " )
    Optional<TbDocumentoEntity> findById(String codigoDocumento);

    @Query(
            value = "SELECT entity "+
                    "FROM TbDocumentoEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoDocumento) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.documento) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) "
                    ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TbDocumentoEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoDocumento) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.documento) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
                    ""
    )
    Page<TbDocumentoEntity> findAllPaginate(String filter, String filterContent, Pageable pageable);
}
