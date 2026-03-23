package com.calero.lili.core.tablas.tbPaises;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbPaisesRepository extends JpaRepository<TbPaisEntity, String> {

    @Query(value = "SELECT entity " +
            "FROM TbPaisEntity  entity " +
            "where entity.codigoPais = :codigo ")
    Optional<TbPaisEntity> findById(@Param("codigo") String codigo);

    @Query(value = "SELECT entity " +
            "FROM TbPaisEntity entity " +
            "WHERE " +
            "((:filter IS NULL OR LOWER(entity.codigoPais) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
            "(:filter IS NULL OR LOWER(entity.pais) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) ")
    List<TbPaisEntity> findAll(@Param("filter") String filter, @Param("filterContent") String filterContent);
}
