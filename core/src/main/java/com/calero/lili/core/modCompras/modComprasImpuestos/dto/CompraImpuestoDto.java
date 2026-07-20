package com.calero.lili.core.modCompras.modComprasImpuestos.dto;

import com.calero.lili.core.modCompras.modCompras.dto.CompraImpuestosDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompraImpuestoDto {


    private String serie;
    private String secuencial;
    private String numeroAutorizacion;
    private String codigoSustento;
    private String codigoDocumento;

    private String pagoLocExt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PagoExterior pagoExterior;

    private List<Reembolso> reembolsos;

    private List<ValoresCompraImpuestoDto> valores;

    private String fechaEmision;

    private String fechaRegistro;

    private String serieRetencion;

    private String secuencialRetencion;

    private String fechaEmisionRetencion;

    private String numeroAutorizacionRetencion;

    private List<CompraImpuestosDto> compraImpuestos;
}
