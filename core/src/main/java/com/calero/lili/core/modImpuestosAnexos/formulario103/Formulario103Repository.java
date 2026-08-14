package com.calero.lili.core.modImpuestosAnexos.formulario103;

import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modCompras.projection.AtsRetencionResumenProjection;
import com.calero.lili.core.modImpuestosAnexos.formulario103.projection.Formulario103Projection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface Formulario103Repository extends JpaRepository<CpRetencionesEntity, UUID> {

    @Query(value = """
            SELECT
                  crf.codigo_formulario AS codigoFormulario,
                  SUM(cic.base_imponible) AS baseImponible, 
                  SUM(cic.valor_retenido) AS valorRetenido
              FROM cp_impuestos ci
              JOIN cp_impuestos_codigos cic ON ci.id_impuestos = cic.id_impuestos
              LEFT JOIN  codigo_retencion_formulario crf ON crf.codigo_retencion = cic.codigo_retencion
              WHERE ci.fecha_registro >= :fechaInicio
                AND ci.fecha_registro <= :fechaFin
                AND ci.id_data = :idData
                AND ci.id_empresa = :idEmpresa
                AND ci.deleted = false
                AND cic.codigo = 'RTA'
              GROUP BY  crf.codigo_formulario
            """, nativeQuery = true)
    List<Formulario103Projection> obtenerRetencionesFormulario103(@Param("idData") Long idData,
                                                                  @Param("idEmpresa") Long idEmpresa,
                                                                  @Param("fechaInicio") LocalDate fechaInicio,
                                                                  @Param("fechaFin") LocalDate fechaFin);

}
