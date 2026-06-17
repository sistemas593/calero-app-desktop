package com.calero.lili.core.modVentas.reporteCredito;

import com.calero.lili.core.modVentas.reporteCredito.projection.DatosCrediticiosProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatosCrediticiosRepository extends JpaRepository<DatosCrediticiosEntity, UUID> {


    @Query(value = """
            SELECT
                dcc.periodo as periodo,
                gt.tipo_identificacion as tipoIdentificacion,
                gt.numero_identificacion as identificacionSujeto,
                gt.tercero as nombreSujeto,
                gt.tipo_personeria as claseSujeto,
                gt.codigo_provincia as codigoProvincia,
                gt.codigo_canton as codigoCanton,
                gt.codigo_parroquia as codigoParroquia,
                gt.sexo as sexo,
                gt.estado_civil as estadoCivil,
                gt.origen_ingresos as origenIngresos,
                dcd.numero_operacion as numeroOperacion,
                dcd.valor_operacion as valorOperacion,
                dcd.saldo_operacion as saldoOperacion,
                dcd.fecha_concesion as fechaConcesion,
                dcd.fecha_vencimiento as fechaVencimiento,
                dcd.fecha_exigible as fechaExigible,
                dcd.plazo_operacion as plazoOperacion,
                dcd.periodicidad_pago as periosidadPago,
                dcd.dias_morosidad as diasMorosidad,
                dcd.monto_morosidad as montoMorisidad,
                dcd.monto_interes_mora as montoInteresMora,
                dcd.valorxvencer1a30dias as valorPorVencer1a30Dias,
                dcd.valorxvencer31a90dias as valorPorVencer31a90Dias,
                dcd.valorxvencer91a180dias as valorPorVencer91a180Dias,
                dcd.valorxvencer181a360dias as valorPorVencer181a360Dias,
                dcd.valorxvencer_mas360dias as valorPorVencerMas360Dias,
                dcd.valor_vencido1a30dias as valorVencido1a30Dias,
                dcd.valor_vencido31a90dias as valorVencido31a90Dias,
                dcd.valor_vencido91a180dias as valorVencido91a180Dias,
                dcd.valor_vencido181a360dias as valorVencido181a360Dias,
                dcd.valor_vencido_mas360dias as valorVencidoMas360Dias,
                dcd.valor_demanda_judicial as valorDemandaJudicial,
                dcd.cartera_castigada as carteraCastigada,
                dcd.couta_credito as cuotaCredito,
                dcd.fecha_cancelacion as fechaCancelacion,
                dcd.forma_cancelacion as formaCancelacion
            FROM vt_datos_crediticios_cabecera dcc
            JOIN vt_datos_crediticios_detalle dcd
                ON dcc.id_datos_crediticios = dcd.id_datos_crediticios
            JOIN ge_terceros gt
                ON gt.id_tercero = dcd.id_tercero
            
            WHERE dcc.id_data = :idData 
            AND dcc.id_empresa =:idEmpresa 
                AND dcd.valor_operacion >= :saldoMinimo
                AND dcd.saldo_operacion > 0 
              AND dcc.periodo = :periodo
              ORDER BY gt.tercero ASC
            """, nativeQuery = true)
    List<DatosCrediticiosProjection> obtenerDatosCrediticios(@Param("idData") Long idData,
                                                             @Param("idEmpresa") Long idEmpresa,
                                                             @Param("saldoMinimo") BigDecimal saldoMinimo,
                                                             @Param("periodo") String periodo);


    @Query(value = "SELECT entity " +
            "FROM DatosCrediticiosEntity entity " +
            "where entity.idData = :idData and entity.idEmpresa = :idEmpresa and entity.periodo = :periodo")
    Optional<DatosCrediticiosEntity> findByPeriodo(@Param("idData") Long idData,
                                                   @Param("idEmpresa") Long idEmpresa,
                                                   @Param("periodo") String periodo);

}
