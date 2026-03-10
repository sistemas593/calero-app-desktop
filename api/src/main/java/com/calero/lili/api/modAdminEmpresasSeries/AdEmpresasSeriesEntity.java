package com.calero.lili.api.modAdminEmpresasSeries;

import com.calero.lili.api.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosEntity;
import com.calero.lili.api.modAuditoria.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_empresas_series")
@Where(clause = "deleted = false")
public class AdEmpresasSeriesEntity extends Auditable {
         
    private Long idData;
    private Long idEmpresa;

    @Id
    private UUID idSerie;
    private String serie;
    private String nombreComercial;
    private String direccionEstablecimiento;
    private String ciudad;
    private String telefono1;
    private String telefono2;

    @JoinColumn(name = "id_serie", referencedColumnName = "idSerie")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdEmpresasSeriesDocumentosEntity> documentosEntity = new ArrayList<>();

}
