package com.calero.lili.api.modCompras.modComprasRetenciones;

import com.calero.lili.api.modImpuestosProcesos.projection.RetencionReferenciaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CpRetencionReferenciasRepository extends JpaRepository<CpRetencionReferencias, UUID> {


    @Query(value = """
            SELECT 
                crr.id_referencia AS idReferencia,
                crr.id_retencion AS idRetencion,
                crr.serie AS serie,
                crr.secuencial AS secuencial,
                crr.numero_identificacion AS numeroIdentificacion,
                crr.codigo_documento AS codigoDocumento,
                crr.impuestos_codigos AS  impuestosCodigos,
                cr.fecha_emision_retencion as fechaRegistro
            FROM cp_retenciones cr
            JOIN cp_retenciones_referencias crr 
                ON crr.id_retencion = cr.id_retencion
            WHERE cr.fecha_emision_retencion >= CAST(:fechaEmisionDesde AS date)
              AND cr.fecha_emision_retencion <= CAST(:fechaEmisionHasta AS date)
              AND crr.id_impuestos IS NULL
            """, nativeQuery = true)
    List<RetencionReferenciaProjection> findAllProjection(
            @Param("fechaEmisionDesde") LocalDate fechaEmisionDesde,
            @Param("fechaEmisionHasta") LocalDate fechaEmisionHasta
    );

    // DEVOLVER IMPUESTOS CODIGOS Y VERIFICAR COMO HACER PARA CONVENTIR DE STRING A MI MODELO EN LA ENTIDAD 

}
