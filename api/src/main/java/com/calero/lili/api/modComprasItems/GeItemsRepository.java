package com.calero.lili.api.modComprasItems;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeItemsRepository extends JpaRepository<GeItemEntity, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM GeItemEntity e " +
            "WHERE e.idData = :idData AND " +
            "e.idEmpresa = :idEmpresa AND " +
            "e.idItem = :idItem")
    void deleteByIdItem(Long idData, Long idEmpresa, UUID idItem);

        @Query(value = "SELECT entity " +
            "FROM GeItemEntity entity " +
            "WHERE entity.idData = :idData AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idItem = :idItem ")
    Optional<GeItemEntity> findByIdItem(Long idData, Long idEmpresa, UUID idItem);

    @Query(value = "SELECT entity " +
            "FROM GeItemEntity entity " +
            "where entity.idData = :idData and " +
            "entity.idEmpresa = :idEmpresa and " +
            "entity.codigoPrincipal = :codigoPrincipal")
    Optional<GeItemEntity> findByCodigoItem(Long idData, Long idEmpresa, String codigoPrincipal);
    @Query(
            value = "SELECT entity " +
                    "FROM GeItemEntity entity " +
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND "+
                    "(" +
                    "(:filter IS NULL OR LOWER(entity.descripcion) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.codigoBarras) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.codigoPrincipal) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) " +
                    ") "
            ,
            countQuery = "SELECT COUNT(1) "+
                    "FROM GeItemEntity entity "+
                    "WHERE ( entity.idData = :idData)  AND " +
                    "(entity.idEmpresa = :idEmpresa) AND "+
                    "(" +
                    "(:filter IS NULL OR LOWER(entity.descripcion) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.codigoBarras) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
                    "(:filter IS NULL OR LOWER(entity.codigoPrincipal) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) " +
                    ") "
    )
    Page<GeItemEntity> findAllPaginate(Long idData, Long idEmpresa, String filter, String filterContent, Pageable pageable);

//    @Query(
//            value = "SELECT " +
//                    "CAST(ge_items.id_item as varchar) as idItem, "+
//                    "ge_items.item as item, " +
//                    "ge_items.codigo_item as codigoItem, " +
//                    "CAST(ge_items.detalles_adicionales as varchar)  as detallesAdicionales, " +
//                    "CAST(ge_items.impuestos as varchar)  as impuestos, " +
//                    "100 as precioUnitario," +
//                    "10 as descuentoPorcentaje," +
//                    "0 as descuentoValor " +
//                    "FROM ge_items "+
//                    "WHERE (ge_items.id_Data = :idData) AND " +
//                    "((:filter IS NULL OR LOWER(ge_items.codigo_item) LIKE LOWER(CONCAT('%', :filterContent, '%')) ) OR " +
//                    "(:filter IS NULL OR LOWER(ge_items.item) LIKE LOWER(CONCAT('%', :filterContent, '%')) )) " +
//                    "LIMIT 100", nativeQuery = true
//    )
//    List<GeItemsProjection> findAll(Long idData, String filtro);

}
