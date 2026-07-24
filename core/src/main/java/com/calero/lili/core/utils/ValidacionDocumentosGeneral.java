package com.calero.lili.core.utils;

import com.calero.lili.core.enums.TipoDocumentoSerie;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminEmpresasSeriesDocumentos.AdEmpresasSeriesDocumentosRepository;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosEntity;
import com.calero.lili.core.modComprasItemsImpuesto.GeImpuestosItemsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ValidacionDocumentosGeneral {

    private final AdEmpresasSeriesDocumentosRepository adEmpresasSeriesDocumentosRepository;
    private final GeImpuestosItemsRepository geImpuestosItemsRepository;


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

    public String validarSerie(String serie) {

        if (Objects.nonNull(serie)) {

            if (serie.length() != 6) {
                return "La serie solo debe tener 6 dígitos";
            }

            if (!serie.matches("\\d+")) {
                return "La serie solo deben ser números";
            }


            String primerosTres = serie.substring(0, 3);
            int valor = Integer.parseInt(primerosTres);
            if (valor == 0) {
                return "Los tres primeros dígitos de la serie no pueden ser igual a cero";
            }

            String segundoTres = serie.substring(3, 6);
            int valor2 = Integer.parseInt(segundoTres);
            if (valor2 == 0) {
                return "Los últimos dígitos de la serie no pueden ser igual a cero";
            }

        } else {
            return "El número de la serie no existe";
        }

        return "";
    }

    public String validarNumeroAutorizacion(String numeroAutorizacion) {
        if (numeroAutorizacion.length() == 49 || numeroAutorizacion.length() == 10) {

            if (!numeroAutorizacion.matches("\\d+")) {
                return "El número de autorización no puede contener caracteres que no sean númericos";
            }

        } else {
            return "El número de autorización no cumple con la cantidad de dígitos 10/49";
        }
        return "";
    }


    public GeImpuestosEntity existeImpuesto(String clave) {

        Optional<GeImpuestosEntity> impuesto = geImpuestosItemsRepository.findCodigoAndCodigoPorcentaje(clave);
        if (impuesto.isPresent()) {
            return impuesto.get();
        } else {
            throw new GeneralException(MessageFormat.format("El impuesto con codigo y codigo porcentaje {0}, no existe", clave));
        }
    }

}
