package com.calero.lili.core.utils;

import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ValidacionDocumentosGeneral {

    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;

    public static void validarSizeSecuencial(String secuencial) {
        if (secuencial.length() != 9) {
            throw new GeneralException("El secuencial debe ser solo de 9 dígitos");
        }
    }


    public String generarSecuencial(Long idData, Long idEmpresa, UUID serie, TipoDocumentoSerie tipoDocumento) {

        Integer ultimoNumero = adEmpresasSeriesDocumentosRepository.actualizarUltimoNumeroSecuencia(idData, idEmpresa,
                serie, tipoDocumento.name());

        if (Objects.isNull(ultimoNumero)) {
            throw new GeneralException("El secuencial no existe para el tipo de documento: " + tipoDocumento.name() +
                    " con serie " + serie);
        }

        return String.format("%09d", ultimoNumero);
    }

}
