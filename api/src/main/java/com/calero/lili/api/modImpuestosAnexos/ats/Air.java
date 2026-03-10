package com.calero.lili.api.modImpuestosAnexos.ats;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class Air {

    public Air(){}

    private List<DetalleAir> detalleAirs;

    @XmlElementWrapper(name = "detalleAir")
    public List<DetalleAir> getDetalleAirs() {
        return detalleAirs;
    }
}
