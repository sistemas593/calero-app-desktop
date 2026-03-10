package com.calero.lili.api.modComprasOrden;

import com.calero.lili.api.modComprasOrden.projection.OneProjection;
import com.calero.lili.api.modComprasOrden.projection.TotalesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Repository
public interface OrdenComprasRepository extends JpaRepository<CpOrdenComprasEntity, UUID> , JpaSpecificationExecutor<CpOrdenComprasEntity> {

    @Query( value= "SELECT entity " +
            "FROM CpOrdenComprasEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "entity.idCompra = :idCompra ")
    Optional<CpOrdenComprasEntity> findByIdEntity(Long idData, Long idEmpresa, UUID idCompra);

    @Transactional
    @Modifying
    @Query("DELETE FROM CpOrdenComprasEntity e " +
            "WHERE e.idData = :idData AND e.idEmpresa = :idEmpresa AND e.idCompra = :idCompra")
    void deleteById(Long idData, Long idEmpresa, UUID idCompra);


    @Query( value= "SELECT id_compra as idCompra " +
            "FROM cp_orden_compras entity " +
            "WHERE (entity.id_Data = :idData)  AND " +
            "(entity.id_Empresa = :idEmpresa) AND " +
            "entity.secuencial = :secuencial LIMIT 1", nativeQuery = true)
    Optional<OneProjection> findExistBySecuencial(Long idData, Long idEmpresa, String secuencial);


        @Query( value= "SELECT entity " +
            "FROM CpOrdenComprasEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
            "(:secuencial IS NULL OR entity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )"
,
                countQuery = "SELECT COUNT(1) "+
                        "FROM CpOrdenComprasEntity entity "+
                        "WHERE ( entity.idData = :idData)  AND " +
                        "(entity.idEmpresa = :idEmpresa) AND " +
                        "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
                        "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND "+
                        "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
                        "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )"
        )
    Page<CpOrdenComprasEntity> findAllPaginate(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta,  String secuencial, Pageable pageable);

    @Query(
            value = "SELECT valoresEntity.codigo as codigo," +
                    "valoresEntity.codigo_porcentaje as codigoPorcentaje, " +
                    "sum(valoresEntity.base_imponible) as totalBaseImponible, " +
                    "sum(valoresEntity.valor) as totalValor " +
                    "FROM cp_orden_compras entity " +
                    "INNER JOIN cp_orden_compras_valores valoresEntity ON entity.id_compra = valoresEntity.id_compra " +
                    "WHERE ( entity.id_data = :idData)  AND cp_orden_compras.anulada = false " +
                    "(entity.id_empresa = :idEmpresa) AND " +
                    "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND " +
                    "(:secuencial IS NULL OR entity.secuencial = :secuencial ) AND "+
                    "( cast(:fechaEmisionDesde as date) is null OR entity.fecha_emision >= :fechaEmisionDesde ) AND " +
                    "( cast(:fechaEmisionHasta as date) is null OR entity.fecha_emision <= :fechaEmisionHasta ) " +
                    "GROUP BY valoresEntity.codigo, valoresEntity.codigo_porcentaje", nativeQuery = true
    )
    List<TotalesProjection> totalValores(Long idData, Long idEmpresa, String sucursal, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String secuencial);


    @Query( value= "SELECT entity " +
            "FROM CpOrdenComprasEntity entity " +
            "WHERE entity.idData = :idData  AND " +
            "entity.idEmpresa = :idEmpresa AND " +
            "(:sucursal IS NULL OR entity.sucursal = :sucursal) AND "+
            "(" +
            "(:secuencial IS NULL OR entity.secuencial = :secuencial) AND " +
            "( cast(:fechaEmisionDesde as date) is null OR entity.fechaEmision >= :fechaEmisionDesde ) AND " +
            "( cast(:fechaEmisionHasta as date) is null OR entity.fechaEmision <= :fechaEmisionHasta )  " +
            ")")
    List<CpOrdenComprasEntity> findAll(Long idData, Long idEmpresa, LocalDate fechaEmisionDesde, LocalDate fechaEmisionHasta, String secuencial, String sucursal);



    @Query("""
            SELECT entity
            FROM CpOrdenComprasEntity entity
            WHERE entity.idData = :idData
              AND entity.idEmpresa = :idEmpresa
              AND  entity.fechaEmision >= :fechaEmisionDesde
              AND  entity.fechaEmision <= :fechaEmisionHasta""")
    List<CpOrdenComprasEntity> findAllDates(@Param("idData") Long idData, @Param("idEmpresa") Long idEmpresa,
                                            @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
                                            @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta);

}
