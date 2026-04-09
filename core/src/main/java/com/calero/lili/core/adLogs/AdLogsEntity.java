package com.calero.lili.core.adLogs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_logs")
@Builder
public class AdLogsEntity {

    @Id
    private UUID idLog;

    private Long idData;

    private Long idEmpresa;

    private UUID idDocumento;

    @Column(columnDefinition = "text")
    private String mensajes;

    private LocalDateTime fechaHora;

    private String serie;

    private String secuencial;

    private String tipoDocumento;

}
