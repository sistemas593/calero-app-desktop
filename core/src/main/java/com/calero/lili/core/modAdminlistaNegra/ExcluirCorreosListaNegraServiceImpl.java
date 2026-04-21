package com.calero.lili.core.modAdminlistaNegra;

import com.calero.lili.core.modClientesConfiguraciones.dto.StCorreoRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ExcluirCorreosListaNegraServiceImpl {

    private final AdMailsListaNegraRepository adMailsListaNegraRepository;

    public void validarCorreosEnvio(StCorreoRequestDto request) {

        // Cambiando ; por , en caso de que este asi
        String emailsValidos = request.getTo().replace(";", ",");

        // enviando a un array todos los correos existentes en to
        String[] correosArray = emailsValidos.split(",");
        String to = "";

        for (String correo : correosArray) {
            correo = correo.trim();
            Optional<AdMailListaNegraEntity> adMailsListaNegraEntity = adMailsListaNegraRepository.findById(correo);
            if (adMailsListaNegraEntity.isEmpty()) {
                to = to + correo + ",";
            }
        }

        emailsValidos = to;
        if (!emailsValidos.isEmpty()) {
            request.setTo(emailsValidos.substring(0, emailsValidos.length() - 1));
        } else {
            request.setTo("");
        }
    }

}
