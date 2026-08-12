package com.calero.lili.core.modCompras.modComprasImpuestos.dto;

import com.calero.lili.core.dtos.InformacionAdicionalDto;
import com.calero.lili.core.enums.DocumentoEnum;
import com.calero.lili.core.enums.FormaPagoSriEnum;
import com.calero.lili.core.enums.PagoLocalExterior;
import com.calero.lili.core.enums.SustentoCodigos;
import com.calero.lili.core.modCompras.dto.ImpuestoCodigoDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDto {

    private UUID idImpuestos;
    private String sucursal;
    private String fechaEmision;

    private String serie;
    private String secuencial;

    private UUID idTercero;
    private String terceroNombre;
    private String numeroIdentificacion;

    private String concepto;
    private String destino;
    private String tipoIdentificacion;
    private String tipoProveedor;
    private String relacionado;
    private String email;
    private String fechaVencimiento;

    private Boolean anulada;

    private List<ResponseValoresDto> valores;

    private String fechaAnulacion;

    private List<FormasPagoSri> formasPagoSri;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FormasPagoSri {
        private FormaPagoSriEnum formaPago;
        private BigDecimal total;
        private String plazo;
        private String unidadTiempo;
    }

    private String fechaAutorizacion;
    private String claveAcceso;

    private List<Reembolso> reembolsos;

    private String tipoContribuyente;
    private String referencia;
    private String liquidar;
    private String devolucionIva;

    private PagoLocalExterior pagoLocExt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PagoExterior pagoExterior;

    private String numeroAutorizacion;


    private String fechaRegistro;

    private String codigoDocumento;
    private DocumentoEnum documento;

    private SustentoCodigos codigoSustento;
    private String sustento;

    private List<ImpuestoCodigoDto> impuestoCodigos;
    private Boolean existeComprobante;

    private List<InformacionAdicionalDto> informacionAdicional;

    /*private TbDocumentosGetOneDto documento;
    private TbSustentosGetOneDto sustento;*/

}
