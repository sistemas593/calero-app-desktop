package com.calero.lili.core.comprobantesWs.services;

import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Autorizacion;
import com.calero.lili.core.comprobantes.objetosXml.autorizacionFile.Mensaje;
import com.calero.lili.core.comprobantes.services.DeEmitidasComponentsServiceImpl;
import com.calero.lili.core.comprobantes.services.builder.CampoAutorizacionBuilder;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.AutorizacionServiceImpl;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationRequestDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListCreationResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirListExistRequestResponseDto;
import com.calero.lili.core.dtos.deRecibidos.CpImpuestosRecibirResponseDto;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@XmlRootElement
@Slf4j
public class DeEmitidasWsServiceImpl {

    @Autowired
    private final AutorizacionServiceImpl autorizacionService;
    private final DeEmitidasComponentsServiceImpl deEmitidasComponentsService;
    private final CampoAutorizacionBuilder campoAutorizacionBuilder;

    public CpImpuestosRecibirListExistRequestResponseDto verificarExisteListaClaves(Long idData, Long idEmpresa, CpImpuestosRecibirListExistRequestResponseDto request) {
        log.info("xxxxxx");

        request.getListaClavesAcceso().stream().forEach((c) -> {
            if (c.getClaveAcceso().length() == 49) {

                Boolean existeBdd = deEmitidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa, c.getClaveAcceso());
                if (existeBdd) {
                    c.setExiste("S");
                } else {
                    c.setExiste("N");
                }
            } else {
                c.setExiste("N");
            }
        });
        return request;
    }


    public CpImpuestosRecibirListCreationResponseDto createListClavesAcceso(Long idData, Long idEmpresa,
                                                                            CpImpuestosRecibirListCreationRequestDto request, String usuario) {
        log.info("xxxxxx");
        List<CpImpuestosRecibirResponseDto> listaRespuestas = new ArrayList<>();

        request.getListaClavesAcceso().stream().forEach((c) -> {

            if (c.getClaveAcceso().length() == 49) {
                if ((c.getClaveAcceso().startsWith("07", 8))) {

                    Boolean existeBdd = deEmitidasComponentsService.verificarExisteDocumentoElectronicoBdd(idData, idEmpresa, c.getClaveAcceso());
                    if (existeBdd.equals(Boolean.FALSE)) {
                        AutorizacionRequestDto autorizacion = new AutorizacionRequestDto();
                        autorizacion.setClaveAcceso(c.getClaveAcceso());
                        autorizacion.setAmbiente("2");

                        RespuestaComprobante result = null;
                        try {
                            result = autorizacionService.consulta(autorizacion);
                        } catch (Exception exception) {
                            log.info("El ws del SRI no esta disponible: " + exception.getMessage());
                        }

                        System.out.println("Comprobantes: " + result.getNumeroComprobantes());
                        System.out.println("Clave de acceso: " + result.getClaveAccesoConsultada());
                        System.out.println("Autorizaciones: " + result.getAutorizaciones());
                        System.out.println(result.getAutorizaciones().getAutorizacion());
                        List<autorizacion.ws.sri.gob.ec.Autorizacion> listaAutorizaciones = result.getAutorizaciones().getAutorizacion();
                        if (result.getNumeroComprobantes() == null) {
                            listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                                    .claveAcceso(c.getClaveAcceso())
                                    .error("Clave de acceso no encontrada")
                                    .exitoso("N")
                                    .build());
                        } else {
                            Autorizacion autorizacionDto = ProcesarClavesAutorizadoSri(listaAutorizaciones, listaRespuestas);
                            if (autorizacionDto.getEstado() != null) {

                                String message = deEmitidasComponentsService.guardarComprobante(idData, idEmpresa,
                                        campoAutorizacionBuilder.builder(autorizacionDto), "001", usuario);

                                if (!message.isEmpty()) {
                                    listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                                            .claveAcceso(c.getClaveAcceso())
                                            .exitoso("N")
                                            .error(message)
                                            .build());
                                }
                            }
                        }
                    }
                } else {
                    listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                            .claveAcceso(c.getClaveAcceso())
                            .exitoso("N")
                            .error("Solo se permite guardar comprobantes de retencion emitidos")
                            .build());

                }
            } else {
                listaRespuestas.add(CpImpuestosRecibirResponseDto.builder()
                        .claveAcceso(c.getClaveAcceso())
                        .exitoso("N")
                        .error("Clave de acceso incorrecta")
                        .build());
            }

        });

        return CpImpuestosRecibirListCreationResponseDto
                .builder()
                .resultados(listaRespuestas)
                .build();
    }


    public Autorizacion ProcesarClavesAutorizadoSri(List<autorizacion.ws.sri.gob.ec.Autorizacion> listaAutorizaciones, List<CpImpuestosRecibirResponseDto> listaRespuestas) {
        Autorizacion autorizacionDto = new Autorizacion();
        for (autorizacion.ws.sri.gob.ec.Autorizacion autorizacion : listaAutorizaciones) {

            if (autorizacion.getEstado().toUpperCase().compareTo("AUTORIZADO") == 0) {
                System.out.println("ENCONTRO UNO AUTORIZADO");

                autorizacionDto.setEstado(autorizacion.getEstado());
                autorizacionDto.setNumeroAutorizacion(autorizacion.getNumeroAutorizacion());
                autorizacionDto.setFechaAutorizacion(autorizacion.getFechaAutorizacion().toString());
                autorizacionDto.setAmbiente(autorizacion.getAmbiente());
                autorizacionDto.setComprobante((new StringBuilder()).append(autorizacion.getComprobante()).toString());

                Mensaje Det1 = new Mensaje();
                Det1.setIdentificador("60");
                Det1.setMensaje("ESTE PROCESO FUE REALIZADO EN EL AMBIENTE DE PRUEBAS");
                Det1.setTipo("INFORMATIVO");

                List<Mensaje> listaMensaje = new ArrayList<Mensaje>();
                listaMensaje.add(Det1);
                autorizacionDto.setMensaje(listaMensaje);

            }
        }
        return autorizacionDto;
    }

}
