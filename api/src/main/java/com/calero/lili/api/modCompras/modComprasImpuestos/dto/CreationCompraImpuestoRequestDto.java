package com.calero.lili.api.modCompras.modComprasImpuestos.dto;

import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.enums.TipoIdentificacion;
import com.calero.lili.api.modCompras.dto.ImpuestoCodigoDto;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreationCompraImpuestoRequestDto {

    private UUID idImpuestos;

    @NotEmpty(message = "No existe la sucursal")
    private String sucursal;

    private String formatoDocumento;

    @NotEmpty(message = "No existe la serie")
    private String serie;
    @NotEmpty(message = "No existe el secuencial")
    private String secuencial;

    private String numeroAutorizacion;

    @NotEmpty(message = "No existe la fecha de emision")
    private String fechaEmision;

    @NotEmpty(message = "No existe la fecha de registro")
    private String fechaRegistro;

    private String fechaVencimiento;

    private UUID idTercero;

    private TipoIdentificacion tipoIdentificacion;
    private String numeroIdentificacion;
    private String tipoProveedor;
    private String relacionado;

    private String concepto;

    @Valid
    @NotEmpty(message = "No existen valores")
    private List<ValoresDto> valores; // Se realizo una clase unica, valoresEntity


    private String documentoElectronico;
    private String destino;

    private List<InformacionAdicionalDto> informacionAdicional;
    private List<FormasPagoDto> formasPagoSri;

    private String modTipoVenta;
    private String modSerie;
    private String modSecuencial;

    private List<Reembolso> reembolsos;

    private String tipoContribuyente;
    private String codigoDocumento;
    private String referencia;
    private String liquidar;
    private String devolucionIva;

    private String pagoLocExt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PagoExterior pagoExterior;

    private String tipoDocumento;
    private String codigoSustento;

    private List<ImpuestoCodigoDto> impuestoCodigos;

}
