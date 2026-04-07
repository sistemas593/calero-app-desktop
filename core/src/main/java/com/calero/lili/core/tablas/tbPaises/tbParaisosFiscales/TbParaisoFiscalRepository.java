package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TbParaisoFiscalRepository extends JpaRepository<TbParaisoFiscalEntity, String> {

    @Query("SELECT p FROM TbParaisoFiscalEntity p WHERE p.codigo = :codigo")
    Optional<TbParaisoFiscalEntity> findByCodigo(@Param("codigo") String codigo);


    @Query(value = "SELECT entity " +
            "FROM TbParaisoFiscalEntity entity " +
            "WHERE " +
            "((:filter IS NULL OR LOWER(entity.codigo) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
            "(:filter IS NULL OR LOWER(entity.paraisoFiscal) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) ")
    List<TbParaisoFiscalEntity> findAll(@Param("filter") String filter, @Param("filterContent") String filterContent);

}
