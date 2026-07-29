package com.calero.lili.core.modCompras.modComprasImpuestos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CpImpuestoReembolsoRepository extends JpaRepository<CpImpuestosReembolsosEntity, UUID> {


    @
            Query(value = """
            SELECT cir.*
            FROM cp_impuestos_reembolsos cir
            WHERE cir.id_data = :idData
              AND cir.id_empresa = :idEmpresa
              AND CONCAT(
                    cir.numero_autorizacion_reemb,
                    '-',
                    cir.serie_reemb,
                    '-',
                    cir.secuencial_reemb
                  ) IN (:codigos)
            """, nativeQuery = true)
    List<CpImpuestosReembolsosEntity> findByCodigosCompuestos(@Param("idData") Long idData,
                                                              @Param("idEmpresa") Long idEmpresa,
                                                              @Param("codigos") Set<String> codigos);


}
