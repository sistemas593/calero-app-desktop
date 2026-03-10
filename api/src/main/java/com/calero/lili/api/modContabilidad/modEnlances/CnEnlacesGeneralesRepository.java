package com.calero.lili.api.modContabilidad.modEnlances;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CnEnlacesGeneralesRepository extends JpaRepository<CnEnlacesGeneralesEntity, UUID> {


    @Query("SELECT e " +
            "FROM CnEnlacesGeneralesEntity e WHERE " +
            "e.idEnlace = :idEnlace")
    Optional<CnEnlacesGeneralesEntity> findByIdEnlace(UUID idEnlace);


    @Query(
            value = "SELECT entity FROM CnEnlacesGeneralesEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.detalle) LIKE CONCAT('%', LOWER(:filter), '%'))",
            countQuery = "SELECT COUNT(entity) FROM CnEnlacesGeneralesEntity entity " +
                    "WHERE (:filter IS NULL OR " +
                    "LOWER(entity.detalle) LIKE CONCAT('%', LOWER(:filter), '%'))"
    )
    Page<CnEnlacesGeneralesEntity> findAllPaginate(@Param("filter") String filter, Pageable pageable);


    @Query("SELECT e " +
            "FROM CnEnlacesGeneralesEntity e WHERE " +
            "e.codigo = :codigo")
    Optional<CnEnlacesGeneralesEntity> findByCodigo(String codigo);


}
