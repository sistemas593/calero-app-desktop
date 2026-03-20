package com.calero.lili.core.modAdminlistaNegra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ad_mails_lista_negra")
public class AdMailListaNegraEntity {

    @Id
    private String email;
    private String motivo;
    private LocalDate fecha;

}
