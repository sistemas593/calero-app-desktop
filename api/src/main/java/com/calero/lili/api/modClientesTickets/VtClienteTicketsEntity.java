package com.calero.lili.api.modClientesTickets;

import com.calero.lili.api.modTerceros.GeTerceroEntity;
import com.calero.lili.core.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vt_clientes_tickets")
@Where(clause = "deleted = false")
public class VtClienteTicketsEntity extends Auditable {


    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private UUID idTicket;

    @Column(name = "id_data")
    private Long idData;

    private String asunto;
    private String contacto;
    private String detalle;
    private String respaldo;
    private LocalDate fecha;
    private String estado;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTercero")
    private GeTerceroEntity cliente;


}
