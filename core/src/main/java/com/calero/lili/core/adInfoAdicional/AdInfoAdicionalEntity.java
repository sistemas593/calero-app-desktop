package com.calero.lili.core.adInfoAdicional;

import com.calero.lili.core.Auditable;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ad_info_adicional")
@Where(clause = "deleted = false")
public class AdInfoAdicionalEntity extends Auditable {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idInfoAdicional;

    @Column(name = "id_data")
    private Long idData;

    @Column(name = "id_empresa")
    private Long idEmpresa;

    @Enumerated(EnumType.STRING)
    private TipoDocumentoSerie documento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<AdInformacionAdicional> informacionAdicional;


}
