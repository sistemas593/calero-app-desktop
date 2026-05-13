package com.calero.lili.core.modAdModulos;

import com.calero.lili.core.modComprasItemsCategorias.GeItemsCategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdModuloRepository extends JpaRepository<AdModulosEntity, Long> {

    @Query("SELECT e " +
            "FROM AdModulosEntity e " +
            "WHERE e.idModulo = :idModulo")
    Optional<AdModulosEntity> getFindById(@Param("idModulo") Long idModulo);

    @Query("""
       SELECT e
       FROM AdModulosEntity e
       WHERE e.idModulo IN :idsModulos
       """)
    List<AdModulosEntity> findAllByIds(@Param("idsModulos") List<Long> idsModulos);
}
