package com.calero.lili.core.modAdminEmpresasSeriesDocumentos;

import com.calero.lili.core.enums.FormatoDocumento;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_empresas_series_documentos")
public class AdEmpresasSeriesDocumentosEntity {

    // mover empresas series
    @Id
    private UUID idDocumento;
    private Long idData;
    private Long idEmpresa;
    private String documento;
    private String numeroAutorizacion;
    private String secuencial;
    private String desde;
    private String hasta;
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private FormatoDocumento formatoDocumento;

}
