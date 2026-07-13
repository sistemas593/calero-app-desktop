package com.calero.lili.core.modVentas.service;


import com.calero.lili.core.dtos.FormasPagoDto;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.facturas.dto.CreationFacturaRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;

@Service
@AllArgsConstructor
public class ValidarServiceImpl {


    public void validarNoModificacion(VtVentaEntity vtVentaEntity) {

        if (vtVentaEntity.getEstadoDocumento().equals(EstadoDocumento.AUT)) {
            throw new GeneralException("El documento no puede modificarse por que ya esta autorizado");
        }

    }
}
