package com.calero.lili.core.apiSitac.services;

import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.modAdDatasConfiguraciones.VtClientesConfiguracionesEntity;
import com.calero.lili.core.modAdDatasConfiguraciones.VtClientesConfiguracionesRepository;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StCorreoRequestDto;
import com.calero.lili.core.modAdDatasConfiguraciones.dto.StEmpresasEnviarCorreoResponseDto;
import com.calero.lili.core.apiSitac.repositories.AdMailsEnviadosRepository;
import com.calero.lili.core.apiSitac.repositories.AdMailsEnviadosTotalRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosEntity;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailEnviadosTotalEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StEnviarCorreoSitacServiceImpl {

    private final VtClientesConfiguracionesRepository clientesConfiguracionesRepository;
    private final GenerarBody generarBody;
    private final AdMailsEnviadosRepository adMailsEnviadosRepository;
    private final AdMailsEnviadosTotalRepository adMailsEnviadosTotalRepository;
    private final AdMailsConfigRepository adConfigRepository;
    private final EmailSender emailSender;

    public StEmpresasEnviarCorreoResponseDto enviarCorreoSitac(String clave, StCorreoRequestDto request) {

        // --1-- es ñ

        String to = request.getTo().trim();
        to = to.replace("--1--", "ñ");
        request.setTo(to);

        String enviar = "S";
        StEmpresasEnviarCorreoResponseDto dto = new StEmpresasEnviarCorreoResponseDto();
        Long idData = Long.valueOf(1);
        Optional<VtClientesConfiguracionesEntity> usuario = clientesConfiguracionesRepository.findByClave(clave);
        if (usuario.isPresent()) {
            if (usuario.get().getEnviarCorreos().equals("S")) {
                if (usuario.get().getFechaVencimiento().isBefore(LocalDate.now())) {
                    enviar = "N";
                    dto.setRespuesta("Servicio caducado");
                }
            } else {
                enviar = "N";
                dto.setRespuesta("Usuario no activado");
            }
        } else {
            enviar = "N";
            dto.setRespuesta("Usuario no registrado");
        }

        if (enviar.equals("S")) {
            List<String> valuesList = Arrays.asList("01", "03", "04", "05", "06", "07");
            if (!valuesList.contains(request.getCodigoDocumento())) {
                enviar = "N";
                dto.setRespuesta("El codigo de documento a enviar es incorrecto: " + request.getCodigoDocumento());
            }
        }

        if (enviar.equals("S")) {

            AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
            String jsonBody = generarBody.generarBodyCorreo(request, adConfigMailEntity);
            emailSender.send(jsonBody, adConfigMailEntity);
            dto.setRespuesta("Enviado ok");

            //if (clave.equals("1920274511419") || clave.equals("1409223410397") || clave.equals("1215204110011") ){
            //if (request.getCodigoDocumento().equals("01") && request.getSerie().equals("001002") ){
            long numeroCorreos = request.getTo().chars().filter(ch -> ch == ',').count() + 1;

            AdMailEnviadosEntity enviado = new AdMailEnviadosEntity();
            enviado.setClave1(clave);
            enviado.setCodigoDocumento(request.getCodigoDocumento());
            enviado.setSerie(request.getSerie());
            enviado.setSecuencial(request.getSecuencia());
            enviado.setMailTo(request.getTo());
            enviado.setTotal(numeroCorreos);
            enviado.setFecha(LocalDateTime.now());
            adMailsEnviadosRepository.save(enviado);

            String periodo = String.valueOf(LocalDate.now().getYear()) + '-' + StringUtils.leftPad(String.valueOf(LocalDate.now().getMonthValue()), 2, '0');
            System.out.println(periodo);
            Optional<AdMailEnviadosTotalEntity> existe = adMailsEnviadosTotalRepository.findByClaveAndPeriodo(clave, periodo);

            if (!existe.isPresent()) {
                AdMailEnviadosTotalEntity nuevo = new AdMailEnviadosTotalEntity();
                nuevo.setClave1(clave);
                nuevo.setPeriodo(periodo);
                nuevo.setTotal(numeroCorreos);
                adMailsEnviadosTotalRepository.save(nuevo);
            } else {
                Long total = existe.get().getTotal();
                existe.get().setTotal(total + numeroCorreos);
                adMailsEnviadosTotalRepository.save(existe.get());
            }

        }

        return dto;
    }

}
