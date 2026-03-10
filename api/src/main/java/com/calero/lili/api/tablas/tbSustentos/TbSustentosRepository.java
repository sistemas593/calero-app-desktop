package com.calero.lili.api.tablas.tbSustentos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbSustentosRepository extends JpaRepository<TbSustentosEntity, String> {

    @Query(value = "SELECT entity "+
            "FROM TbSustentosEntity entity " +
            "where entity.codigoSustento = :codigoSustento " )

    Optional<TbSustentosEntity> findById(String codigoSustento);

    @Query(
            value = "SELECT entity "+
                    "FROM TbSustentosEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoSustento) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.sustento) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) "
                    ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM TbSustentosEntity entity "+
                    "WHERE " +
                    "((:filter IS NULL OR LOWER(entity.codigoSustento) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.sustento) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
                    ""
    )
    Page<TbSustentosEntity> findAllPaginate(String filter, String filterContent, Pageable pageable);
}
