package com.calero.lili.core.tablas.tbFormasPagoSri;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbFormasPagoSriRepository extends JpaRepository<TbFormaPagoSriEntity, String> {

    @Query(value = "SELECT entity "+
            "FROM TbFormaPagoSriEntity entity " +
            "where entity.codigoFormaPagoSri = :codigo " )

    Optional<TbFormaPagoSriEntity> findById(@Param("codigo") String codigo);

    @Query(
            value = "SELECT entity "+
                    "FROM TbFormaPagoSriEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoFormaPagoSri) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.formaPagoSri) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) "
                    ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TbFormaPagoSriEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoFormaPagoSri) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.formaPagoSri) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
                    ""
    )
    Page<TbFormaPagoSriEntity> findAllPaginate(@Param("filter") String filter, @Param("filterContent") String filterContent, Pageable pageable);
}
