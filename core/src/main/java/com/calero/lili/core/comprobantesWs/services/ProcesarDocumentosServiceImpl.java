package com.calero.lili.core.comprobantesWs.services;

import autorizacion.ws.sri.gob.ec.Autorizacion;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.adLogs.AdLogsServiceImpl;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import com.calero.lili.core.adProcesoAutorizacion.AdProcesoAutorizacionService;
import com.calero.lili.core.apiSitac.repositories.AdMailsConfigRepository;
import com.calero.lili.core.apiSitac.repositories.entities.AdMailConfigEntity;
import com.calero.lili.core.apiSitac.services.EmailSender;
import com.calero.lili.core.apiSitac.services.GenerarBody;
import com.calero.lili.core.comprobantesPdf.comprobantesGetXmlDto.EnvioCorreoDto;
import com.calero.lili.core.comprobantesWs.RespuestaAutorizacion;
import com.calero.lili.core.comprobantesWs.RespuestaEnvio;
import com.calero.lili.core.comprobantesWs.RespuestaProceso;
import com.calero.lili.core.comprobantesWs.RespuestaProcesoGetDto;
import com.calero.lili.core.comprobantesWs.dto.DatosEmpresaDto;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.dtos.recepcion.RecepcionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.AutorizacionServiceImpl;
import com.calero.lili.core.comprobantesWs.ws.services.RecepcionServiceImpl;
import com.calero.lili.core.dtos.Mensajes;
import com.calero.lili.core.enums.EstadoDocumento;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.modAdminlistaNegra.ExcluirCorreosListaNegraServiceImpl;
import com.calero.lili.core.modClientesConfiguraciones.dto.StCorreoRequestDto;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.LiquidacionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recepcion.ws.sri.gob.ec.Comprobante;
import recepcion.ws.sri.gob.ec.Mensaje;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;
import xades4j.SignXmlString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor

public class ProcesarDocumentosServiceImpl {
    @Autowired
    private final VtVentasRepository vtVentaRepository;
    private final VtGuiasRepository vtGuiasRepository;
    private final LiquidacionesRepository liquidacionesRepository;
    private final ComprasRetencionesRepository comprasRetencionesRepository;

    private final RecepcionServiceImpl recepcionService;
    private final AutorizacionServiceImpl autorizacionService;
    private final SetearCorreoServiceImpl procesarEnvioCorreo;
    @Autowired
    private final SignXmlString signXmlString;

    private final ProcesarPausarServiceImpl procesarPausarService;
    private final AdLogsServiceImpl adLogsService;
    private final AdProcesoAutorizacionService adProcesoAutorizacionService;
    private final GenerarBody generarBody;
    private final ExcluirCorreosListaNegraServiceImpl excluirCorreosListaNegraService;
    private final AdMailsConfigRepository adConfigRepository;
    private final EmailSender emailSender;

//    private static String projectId = "caleroapp";
//    private static String bucketName = "caleroapp-bucket-sgn";




//    public DatosEmpresaDto buscarEmpresa(Long idData, Long idEmpresa){
//        String sgn = "data00001/file001.p12";
//        Optional<AdEmpresaEntity> empresa = adEmpresasRepository.findById(idData,idEmpresa);
//
//        if (!empresa.isPresent()) {
//            throw new GeneralException(MessageFormat
//                    .format("Data {1}, Empresa {1} no existe",idData, idEmpresa));
//        }
//
//            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//            BlobId blobId = BlobId.of(bucketName, "data00001/logo-fe-0001.jpg");
//            Blob blob = storage.get(blobId);
//            if (blob == null || !blob.exists()) {
//                throw new GeneralException("El archivo especificado no existe en el bucket.");
//            }
//            ReadChannel reader = blob.reader();
//            InputStream inputStream = Channels.newInputStream(reader);
//        byte[] imageBytes = null;
//            try {
//                imageBytes = inputStream.readAllBytes();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        return DatosEmpresaDto.builder()
//                .sgn(sgn)
//                .pwd(empresa.get().getContraseniaFirma())
//                .imageBytes(imageBytes)
//                .build();
//
//    }

    public RespuestaProcesoGetDto procesarFacNcNd(VtVentaEntity documento, AdLogsRequestDto logs, DatosEmpresaDto datosEmpresaDto) {

        System.out.println("Inicio del proceso");

        if (!documento.getEstadoDocumento().equals(EstadoDocumento.ENV) && !documento.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }


        System.out.println("1. estado del documento en la base de datos:" + documento.getEstadoDocumento());
        String claveAcceso = documento.getClaveAcceso();
        Integer ambiente = Integer.valueOf(documento.getAmbiente());
        String estadoDocumento = documento.getEstadoDocumento().toString();
        String comprobante = documento.getComprobante();

        RespuestaProceso respuestaProceso = null;
        respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante,
                datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd(), logs);
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());


        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("procesarFacNcNd 1. Guardar fc/nc/nd como recibida");
                documento.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    System.out.println("procesarFacNcNd Guardar fc/nc/nd como autorizada");

                    documento.setEstadoDocumento(EstadoDocumento.AUT);
                    documento.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    documento.setComprobante(respuestaProceso.getComprobante());
                    documento.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(documento.getEmail())) {

                        if (!documento.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(documento.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(documento.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(documento.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(documento.getTercero().getTercero());

                            switch (documento.getTipoVenta()) {
                                case "FAC":
                                    envioCorreoDto.setCodigoDocumento("01");
                                    break;
                                case "NCR":
                                    envioCorreoDto.setCodigoDocumento("04");
                                    break;
                                case "NDB":
                                    envioCorreoDto.setCodigoDocumento("05");
                            }

                            envioCorreoDto.setSecuencial(documento.getSecuencial());
                            envioCorreoDto.setSerie(documento.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(documento.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(documento.getClaveAcceso());
                            envioCorreoDto.setEmail(documento.getEmail());
                            System.out.println("Enviar correo con la siguiente informacion: " + envioCorreoDto.toString());
                            StCorreoRequestDto informacionCorreo = procesarEnvioCorreo.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            documento.setEmailEstado(2);
                            respuestaProceso.setEmailEstado(2);
                            excluirCorreosListaNegraService.validarCorreosEnvio(informacionCorreo);
                            if (!informacionCorreo.getTo().isEmpty()) {
                                if (datosEmpresaDto.getOrigenDatos().equals("WEB")) {
                                    AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
                                    String jsonBody = generarBody.generarBodyCorreo(informacionCorreo, adConfigMailEntity);
                                    emailSender.send(jsonBody, adConfigMailEntity);
                                }
                            }

                        }
                    }
                }

                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesarFacNcNd Guardar fc/nc/nd como NO autorizada");
                    documento.setEstadoDocumento(EstadoDocumento.NOA);
                    if (Objects.nonNull(respuestaProceso.getMensajes())) {
                        documento.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                vtVentaRepository.save(documento);
                break;
            case "DEV":
                System.out.println("2. Guardar fc/nc/nd como devuelto");
                documento.setEstadoDocumento(EstadoDocumento.DEV);
                if (Objects.nonNull(respuestaProceso.getMensajes())) {
                    documento.setMensajes(respuestaProceso.getMensajes());
                    // TODO COLOCAR COMO UN SOLO MENSAJE
                    // adLogsService.saveLog(logs, respuestaProceso.getMensajes(), "E");
                }
                vtVentaRepository.save(documento);
                break;
        }

        return responderProceso(respuestaProceso, documento.getIdVenta());

    }


    public RespuestaProcesoGetDto procesarGuiaRemision(VtGuiaEntity guia, AdLogsRequestDto logs, DatosEmpresaDto datosEmpresaDto) {

        System.out.println("Inicio del proceso");


        if (!guia.getEstadoDocumento().equals(EstadoDocumento.ENV) && !guia.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + guia.getEstadoDocumento());
        String claveAcceso = guia.getClaveAcceso();
        Integer ambiente = Integer.valueOf(guia.getAmbiente());
        String estadoDocumento = guia.getEstadoDocumento().toString();
        String comprobante = guia.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante,
                datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd(), logs);
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar guia como recibida");
                guia.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    guia.setEstadoDocumento(EstadoDocumento.AUT);
                    guia.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    guia.setComprobante(respuestaProceso.getComprobante());
                    guia.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(guia.getEmail())) {

                        if (!guia.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(guia.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(guia.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(guia.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(guia.getDestinatario().getTercero());
                            envioCorreoDto.setCodigoDocumento("06");
                            envioCorreoDto.setSecuencial(guia.getSecuencial());
                            envioCorreoDto.setSerie(guia.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(guia.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(guia.getClaveAcceso());
                            envioCorreoDto.setEmail(guia.getEmail());

                            StCorreoRequestDto informacionCorreo = procesarEnvioCorreo.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            guia.setEmailEstado(2);
                            respuestaProceso.setEmailEstado(2);
                            if (!informacionCorreo.getTo().isEmpty()) {
                                if (datosEmpresaDto.getOrigenDatos().equals("WEB")) {
                                    AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
                                    String jsonBody = generarBody.generarBodyCorreo(informacionCorreo, adConfigMailEntity);
                                    emailSender.send(jsonBody, adConfigMailEntity);
                                }
                            }
                        }
                    }

                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar guia como NO autorizada");
                    guia.setEstadoDocumento(EstadoDocumento.NOA);
                    if (Objects.nonNull(respuestaProceso.getMensajes())) {
                        // TODO GUARDAR EN EL LOG EL MENSAJE
                        guia.setMensajes(respuestaProceso.getMensajes());
                    }
                }


                vtGuiasRepository.save(guia);
                break;
            case "DEV":
                System.out.println("2. Guardar guia como devuelto");
                guia.setEstadoDocumento(EstadoDocumento.DEV);
                if (Objects.nonNull(respuestaProceso.getMensajes())) {
                    guia.setMensajes(respuestaProceso.getMensajes());
                }
                vtGuiasRepository.save(guia);
                break;
        }
        return responderProceso(respuestaProceso, guia.getIdGuia());
    }


    public RespuestaProcesoGetDto procesarLiquidacion(CpLiquidacionesEntity liquidacion, AdLogsRequestDto log, DatosEmpresaDto datosEmpresaDto) {

        System.out.println("Inicio del proceso");

        // LISTO PARA ENVIAR RECEPCION

        if (!liquidacion.getEstadoDocumento().equals(EstadoDocumento.ENV) && !liquidacion.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + liquidacion.getEstadoDocumento());
        String claveAcceso = liquidacion.getClaveAcceso();
        Integer ambiente = Integer.valueOf(liquidacion.getAmbiente());
        String estadoDocumento = liquidacion.getEstadoDocumento().toString();
        String comprobante = liquidacion.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante,
                datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd(), log);
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar liquidacion como recibida");
                liquidacion.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    liquidacion.setEstadoDocumento(EstadoDocumento.AUT);
                    liquidacion.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    liquidacion.setComprobante(respuestaProceso.getComprobante());
                    liquidacion.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(liquidacion.getEmail())) {

                        if (!liquidacion.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(liquidacion.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(liquidacion.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(liquidacion.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(liquidacion.getProveedor().getTercero());
                            envioCorreoDto.setCodigoDocumento("03");
                            envioCorreoDto.setSecuencial(liquidacion.getSecuencial());
                            envioCorreoDto.setSerie(liquidacion.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(liquidacion.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(liquidacion.getClaveAcceso());
                            envioCorreoDto.setEmail(liquidacion.getEmail());

                            StCorreoRequestDto informacionCorreo = procesarEnvioCorreo.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            liquidacion.setEmailEstado(2);
                            respuestaProceso.setEmailEstado(2);
                            if (!informacionCorreo.getTo().isEmpty()) {
                                if (datosEmpresaDto.getOrigenDatos().equals("WEB")) {
                                    AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
                                    String jsonBody = generarBody.generarBodyCorreo(informacionCorreo, adConfigMailEntity);
                                    emailSender.send(jsonBody, adConfigMailEntity);
                                }
                            }
                        }
                    }
                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar liquidacion como NO autorizada");
                    liquidacion.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        liquidacion.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                liquidacionesRepository.save(liquidacion);
                break;
            case "DEV":
                System.out.println("2. Guardar liquidacion como devuelto");
                liquidacion.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    liquidacion.setMensajes(respuestaProceso.getMensajes());
                }
                liquidacionesRepository.save(liquidacion);
                break;
        }
        return responderProceso(respuestaProceso, liquidacion.getIdLiquidacion());
    }

    public RespuestaProcesoGetDto procesarComprobanteRetencion(CpRetencionesEntity retencion, AdLogsRequestDto log, DatosEmpresaDto datosEmpresaDto) {

        System.out.println("Inicio del proceso");

        if (!retencion.getEstadoDocumento().equals(EstadoDocumento.ENV) && !retencion.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + retencion.getEstadoDocumento());
        String claveAcceso = retencion.getClaveAcceso();
        Integer ambiente = Integer.valueOf(retencion.getAmbiente());
        String estadoDocumento = retencion.getEstadoDocumento().toString();
        String comprobante = retencion.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante,
                datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd(), log);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar retencion como recibida");
                retencion.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    retencion.setEstadoDocumento(EstadoDocumento.AUT);
                    retencion.setNumeroAutorizacionRetencion(respuestaProceso.getNumeroAutorizacion());
                    retencion.setComprobante(respuestaProceso.getComprobante());
                    retencion.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(retencion.getEmail())) {

                        if (!retencion.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(retencion.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(retencion.getNumeroAutorizacionRetencion());
                            envioCorreoDto.setFechaAutorizacion(retencion.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(retencion.getProveedor().getTercero());
                            envioCorreoDto.setCodigoDocumento("07");
                            envioCorreoDto.setSecuencial(retencion.getSecuencialRetencion());
                            envioCorreoDto.setSerie(retencion.getSerieRetencion());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(retencion.getFechaEmisionRetencion()));
                            envioCorreoDto.setClaveAcceso(retencion.getClaveAcceso());
                            envioCorreoDto.setEmail(retencion.getEmail());

                            StCorreoRequestDto informacionCorreo = procesarEnvioCorreo.seterarRequestCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            retencion.setEmailEstado(2);
                            respuestaProceso.setEmailEstado(2);
                            if (!informacionCorreo.getTo().isEmpty()) {
                                if (datosEmpresaDto.getOrigenDatos().equals("WEB")) {
                                    AdMailConfigEntity adConfigMailEntity = adConfigRepository.findByIdConfig(Long.valueOf(1));
                                    String jsonBody = generarBody.generarBodyCorreo(informacionCorreo, adConfigMailEntity);
                                    emailSender.send(jsonBody, adConfigMailEntity);
                                }
                            }
                        }
                    }
                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar retencion como NO autorizada");
                    retencion.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        retencion.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                comprasRetencionesRepository.save(retencion);
                break;
            case "DEV":
                System.out.println("2. Guardar retencion como devuelto");
                retencion.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    retencion.setMensajes(respuestaProceso.getMensajes());
                }
                comprasRetencionesRepository.save(retencion);
                break;
        }
        return responderProceso(respuestaProceso, retencion.getIdRetencion());
    }

    public RespuestaProceso procesarDocumento(String estadoDocumento, String claveAcceso, Integer ambiente,
                                              String comprobante, InputStream inputStreamFileSgn, String pwd, AdLogsRequestDto logs) {
        RespuestaProceso respuestaProceso = new RespuestaProceso();
        switch (estadoDocumento) {
            case "ENV":
                System.out.println("procesarDocumento 1. Estado en la BDD ENV Enviar documento electronico");

                adLogsService.saveLog(logs, "Inicio proceso de envio al WS del SRI", "I");
                comprobarProcesoAutorizacion(claveAcceso);

                RespuestaProceso respuestaEnvio = procesarEnvio(claveAcceso, ambiente, comprobante, inputStreamFileSgn, pwd, logs);


                respuestaProceso.setEstadoEnvio(Objects.nonNull(respuestaEnvio.getEstadoEnvio()) ? respuestaEnvio.getEstadoEnvio() : "");

                // SI ESTA DEVUELTA AQUI ASIGNO LOS MENSAJES SI EXISTEN
                if (!Objects.isNull(respuestaEnvio.getMensajes())) {
                    respuestaProceso.setMensajes(respuestaEnvio.getMensajes());
                    respuestaProceso.setMensajesRecepcion(respuestaEnvio.getMensajes());
                }

                System.out.println(respuestaProceso.getEstadoEnvio());
                respuestaProceso.setEstadoEnvio(respuestaProceso.getEstadoEnvio());
                if (respuestaProceso.getEstadoEnvio().equals("REC")) {
                    System.out.println("procesarDocumento: PROCESO DE AUTORIZACION");
                    //////////// PASO 2 AUTORIZACION
                    RespuestaAutorizacion respuestaAutorizacion1 = procesarAutorizacion(claveAcceso, ambiente, logs);

                    if (respuestaAutorizacion1.getEstadoAutorizacion().equals("AUT")) {
                        respuestaProceso.setEstadoAutorizacion(respuestaAutorizacion1.getEstadoAutorizacion());
                        respuestaProceso.setNumeroAutorizacion(respuestaAutorizacion1.getNumeroAutorizacion());
                        respuestaProceso.setFechaAutorizacion(respuestaAutorizacion1.getFechaAutorizacion());
                        respuestaProceso.setComprobante(respuestaAutorizacion1.getComprobante());
                        if (!Objects.isNull(respuestaAutorizacion1.getMensajes())) {
                            respuestaProceso.setMensajes(respuestaAutorizacion1.getMensajes());
                        }

                    }

                    if (respuestaAutorizacion1.getEstadoAutorizacion().equals("NOA")) {
                        respuestaProceso.setEstadoAutorizacion(respuestaAutorizacion1.getEstadoAutorizacion());
                        //respuestaProceso.setNumeroAutorizacion(respuestaAutorizacion1.getNumeroAutorizacion());
                        //respuestaProceso.setFechaAutorizacion(respuestaAutorizacion1.getFechaAutorizacion());
                        respuestaProceso.setComprobante(respuestaAutorizacion1.getComprobante());
                        if (!Objects.isNull(respuestaAutorizacion1.getMensajes())) {
                            respuestaProceso.setMensajes(respuestaAutorizacion1.getMensajes());
                        }
                    }
                }

                break;
            case "REC":
                System.out.println("procesarDocumento 2. Documento electronico recibido");
                respuestaProceso.setEstadoEnvio("REC");
                //respuestaProceso.setEstadoAutorizacion("");

                System.out.println("procesarDocumento: PROCESO DE AUTORIZACION");
                //////////// PASO 2 AUTORIZACION
                RespuestaAutorizacion respuestaAutorizacion1 = procesarAutorizacion(claveAcceso, ambiente, logs);

                if (respuestaAutorizacion1.getEstadoAutorizacion().equals("AUT")) {
                    respuestaProceso.setEstadoAutorizacion(respuestaAutorizacion1.getEstadoAutorizacion());
                    respuestaProceso.setNumeroAutorizacion(respuestaAutorizacion1.getNumeroAutorizacion());
                    respuestaProceso.setFechaAutorizacion(respuestaAutorizacion1.getFechaAutorizacion());
                    respuestaProceso.setComprobante(respuestaAutorizacion1.getComprobante());
                    if (!Objects.isNull(respuestaAutorizacion1.getMensajes())) {
                        respuestaProceso.setMensajes(respuestaAutorizacion1.getMensajes());
                    }
                }

                if (respuestaAutorizacion1.getEstadoAutorizacion().equals("NOA")) {
                    respuestaProceso.setEstadoAutorizacion(respuestaAutorizacion1.getEstadoAutorizacion());
                    //respuestaProceso.setNumeroAutorizacion(respuestaAutorizacion1.getNumeroAutorizacion());
                    //respuestaProceso.setFechaAutorizacion(respuestaAutorizacion1.getFechaAutorizacion());
                    respuestaProceso.setComprobante(respuestaAutorizacion1.getComprobante());
                    if (!Objects.isNull(respuestaAutorizacion1.getMensajes())) {
                        respuestaProceso.setMensajes(respuestaAutorizacion1.getMensajes());
                    }
                }

                break;
        }
        return respuestaProceso;
    }

    private void comprobarProcesoAutorizacion(String claveAcceso) {
        if (adProcesoAutorizacionService.existsByClaveAcceso(claveAcceso)) {
            procesarPausarService.pausarProcesoAutorizacion();
            adProcesoAutorizacionService.deleteByClaveAcceso(claveAcceso);
        }
        adProcesoAutorizacionService.create(claveAcceso);

    }

    public RespuestaProceso procesarEnvio(String claveAcceso, Integer ambiente, String comprobante,
                                          InputStream inputStreamFileSgn, String pwd, AdLogsRequestDto log) {
        RespuestaProceso respuestaProceso = new RespuestaProceso();

        RespuestaComprobante result = new RespuestaComprobante();
        if (log.getValidacionPrevia()) {
            result = consultarDocumento(claveAcceso, ambiente, log);
            if (Objects.isNull(result)) {
                return respuestaProceso;
            }
        } else {
            result.setNumeroComprobantes("0");
        }


        if (result.getNumeroComprobantes() == null || result.getNumeroComprobantes().equals("0")) {
            System.out.println("procesarEnvioTodos Validacion previa, no existe un documento con la clave de acceso.");

            // WS JAVA8
//            // FIRMAR
//            JsonNode jsonNode = procesarFirmarXmlService.firmarXml(comprobante);
//            // ENVIAR A AUTORIZAR
//            RecepcionRequestDto requestDto = new RecepcionRequestDto();
//            String string = jsonNode.get("xmlFirmado").asText();
//            requestDto.setFileXml(string);

            // JAVA 21
            // FIRMAR
            // Create a new GCS client and get the blob object from the blob ID
//            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
//            BlobId blobId = BlobId.of(bucketName, sgn);
//            Blob blob = storage.get(blobId);
//
//            if (blob == null || !blob.exists()) {
//                throw new GeneralException("El archivo especificado no existe en el bucket.");
//            }
//
//            ReadChannel reader = blob.reader();
//            InputStream is = Channels.newInputStream(reader);

            String comprobanteFirmado = null;
            try {
                comprobanteFirmado = signXmlString.sign(comprobante, inputStreamFileSgn, pwd);
                System.out.println(comprobanteFirmado);
            } catch (IOException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("password")) {
                    adLogsService.saveLog(log, "Error al firmar el comprobante: Contraseña incorrecta para el archivo de firma digital.", "E");
                    respuestaProceso.setEstadoEnvio("ENV");
                    return respuestaProceso;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String comprobanteFirmadoBase64 = Base64.getEncoder().encodeToString(comprobanteFirmado.getBytes(StandardCharsets.UTF_8));
            // ENVIAR A AUTORIZAR
            RecepcionRequestDto requestDto = new RecepcionRequestDto();
            //String string = jsonNode.get("xmlFirmado").asText();
            requestDto.setFileXml(comprobanteFirmadoBase64);

            requestDto.setAmbiente(ambiente.toString());

            RespuestaEnvio respuestaEnvio = enviarAutorizar(requestDto, log);
            // si esta devuelta aqui seteo los mensajes si existen auto
            if (!Objects.isNull(respuestaEnvio.getMensajes())) {
                respuestaProceso.setMensajes(respuestaEnvio.getMensajes());
            }


            procesarPausarService.pausar();

            if (respuestaEnvio.getEstadoEnvio().equals(EstadoDocumento.REC)) {
                respuestaProceso.setEstadoEnvio("REC");
                adLogsService.saveLog(log, "Documento recibido por WS del SRI", "I");
                RespuestaComprobante result2 = consultarDocumento(claveAcceso, ambiente, log);
                if (result2.getNumeroComprobantes() == null || result2.getNumeroComprobantes().equals("0")) {
                    System.out.println("procesarEnvioTodos ENVIADO ESTE MOMENTO / No existe aun un documento con la clave de acceso.");
                } else {
                    System.out.println("procesarEnvioTodos ENVIADO ESTE MOMENTO / ahora YA existe un documento con la clave de acceso.");
                }
            }
            if (respuestaEnvio.getEstadoEnvio().equals(EstadoDocumento.DEV)) {
                adLogsService.saveLog(log, "Documento devuelto por WS del SRI", "E");
                respuestaProceso.setEstadoEnvio("DEV");

            }


        } else {
            if (Integer.parseInt(result.getNumeroComprobantes()) > 0) {
                System.out.println("procesarEnvioTodos Validacion previa, Ya existe un documento con la clave de acceso.");
                respuestaProceso.setEstadoEnvio(EstadoDocumento.REC.toString());
            }
        }
        return respuestaProceso;
    }


    public RespuestaAutorizacion procesarAutorizacion(String claveAcceso, Integer ambiente, AdLogsRequestDto log) {

        RespuestaAutorizacion respuestaAutorizacion = new RespuestaAutorizacion();
        RespuestaComprobante result = consultarDocumento(claveAcceso, ambiente, log);
        if (result.getNumeroComprobantes() == null || result.getNumeroComprobantes().equals("0")) {
            System.out.println("No existe un documento con la clave de acceso.");
        } else {
            System.out.println("Si existe un documento en el ws.");

            List<Autorizacion> listaAut = result.getAutorizaciones().getAutorizacion();

            // REVISAR SI EXISTEN VARIOS DOCUMENTOS Y DENTRO DE ESE UNO AUTORIZADO DEJAR SOLO ESE
            for (Autorizacion aut : listaAut) {
                System.out.println(aut.getEstado());
                respuestaAutorizacion.setComprobante(aut.getComprobante());
                if (aut.getEstado().toString().equals(ESTADO_AUTORIZADO)) {
                    respuestaAutorizacion.setEstadoAutorizacion("AUT");
                    respuestaAutorizacion.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                    respuestaAutorizacion.setFechaAutorizacion(aut.getFechaAutorizacion().toString());
                    respuestaAutorizacion.setComprobante(aut.getComprobante());
                }
                if (aut.getEstado().toString().equals(ESTADO_NO_AUTORIZADO)) {
                    respuestaAutorizacion.setEstadoAutorizacion("NOA");
                    respuestaAutorizacion.setNumeroAutorizacion(aut.getNumeroAutorizacion());
                    respuestaAutorizacion.setFechaAutorizacion(aut.getFechaAutorizacion().toString());
                    respuestaAutorizacion.setComprobante(aut.getComprobante());

                    List<Mensajes> mensajesLista = new ArrayList<>();
                    List<autorizacion.ws.sri.gob.ec.Mensaje> listaMensajes = aut.getMensajes().getMensaje();
                    for (autorizacion.ws.sri.gob.ec.Mensaje men : listaMensajes) {
                        System.out.println(men.getIdentificador());
                        System.out.println(men.getMensaje());
                        Mensajes mensajesPer = new Mensajes();
                        mensajesPer.setIdentificador(men.getIdentificador());
                        mensajesPer.setTipo(men.getTipo());
                        mensajesPer.setInformacionAdicional(men.getInformacionAdicional());
                        mensajesPer.setMensaje(men.getMensaje());
                        mensajesLista.add(mensajesPer);
                    }
                    respuestaAutorizacion.setMensajes(mensajesLista);
                }
            }
        }
        return respuestaAutorizacion;
    }

//    public void procesarTodos() {
//        LocalDate desde = LocalDate.now().minusDays(30);
//        LocalDate hasta = LocalDate.now().minusDays(1);
//        System.out.println(desde);
//        System.out.println(hasta);
//
//
//
//        // LISTO PARA ENVIAR RECEPCION
//        List<VtVentaEntity> ventas1 = vtVentaRepository.findAllFacturasGenerar(1L, 1L, desde, hasta, "ENV");
//        for (VtVentaEntity venta : ventas1) {
//            procesarRecepcion(venta);
//        }
//
//        // RECIBIDAS AUTORIZACION
//        List<VtVentaEntity> ventas2 = vtVentaRepository.findAllFacturasGenerar(1l, 1l, desde, hasta, "REC");
//        for (VtVentaEntity venta : ventas2) {
//            procesarAutorizacion(venta);
//        }
//
//        // AUTORIZADAS ENVIAR CORREO
//        List<VtVentaEntity> ventas3 = vtVentaRepository.findAllFacturasGenerarCorreo(1l, 1l, desde, hasta);
//        for (VtVentaEntity venta : ventas3) {
//            procesarEnvioCorreo(venta);
//        }
//
//    }

    @Transactional
    public RespuestaComprobante consultarDocumento(String claveAcceso, Integer ambiente, AdLogsRequestDto log) {

        // CONSULTA DOCUMENTO LOG
        adLogsService.saveLog(log, "Inicia  de consulta de documento existente en el SRI con clave de acceso: " + claveAcceso, "I");
        System.out.println("consultarDocumento. " + claveAcceso);

        //Consultar el documento
        AutorizacionRequestDto request = new AutorizacionRequestDto();
        request.setAmbiente(ambiente.toString());
        request.setClaveAcceso(claveAcceso);
        RespuestaComprobante result = null;
        try {
            result = autorizacionService.consulta(request);
        } catch (Exception ex) {
            adLogsService.saveLog(log, "El WS de consulta de documentos del SRI no esta disponible", "E");
        }
        return result;

    }

    public RespuestaEnvio enviarAutorizar(RecepcionRequestDto requestDto, AdLogsRequestDto log) {

        RespuestaEnvio respuestaEnvio = new RespuestaEnvio();
        System.out.println("Enviando documento");

        RespuestaSolicitud responseDto = null;
        try {
            responseDto = recepcionService.enviar(requestDto);
        } catch (Exception ex) {
            adLogsService.saveLog(log, "El WS de recepcion de documentos del SRI no esta disponible", "E");
        }

        List<Mensajes> mensajesLista = new ArrayList<>();
        if (Objects.nonNull(responseDto)) {
            if (responseDto.getEstado().equals("RECIBIDA")) {
                System.out.println("RECIBIDA");
                respuestaEnvio.setEstadoEnvio(EstadoDocumento.REC);
            }

            if (responseDto.getEstado().equals("DEVUELTA")) {
                System.out.println("DEVUELTA");
                respuestaEnvio.setEstadoEnvio(EstadoDocumento.DEV);
                List<Comprobante> comprobante = responseDto.getComprobantes().getComprobante();
                for (Comprobante com : comprobante) {
                    System.out.println(com.getMensajes().getMensaje());
                    List<Mensaje> listaMensajes = com.getMensajes().getMensaje();
                    for (Mensaje men : listaMensajes) {

                        System.out.println(men.getIdentificador());
                        System.out.println(men.getMensaje());

                        Mensajes mensajesPer = new Mensajes();
                        mensajesPer.setIdentificador(men.getIdentificador());
                        mensajesPer.setTipo(men.getTipo());
                        mensajesPer.setInformacionAdicional(men.getInformacionAdicional());
                        mensajesPer.setMensaje(men.getMensaje());

                        mensajesLista.add(mensajesPer);
                    }
                    respuestaEnvio.setMensajes(mensajesLista);
                }
            }
        } else {
            respuestaEnvio.setEstadoEnvio(EstadoDocumento.ENV);
        }

        respuestaEnvio.setMensajes(mensajesLista);
        return respuestaEnvio;
    }


    //public static final String ESTADO_RECIBIDA = "RECIBIDA";
    //public static final String ESTADO_DEVUELTA = "DEVUELTA";
    public static final String ESTADO_AUTORIZADO = "AUTORIZADO";
    public static final String ESTADO_NO_AUTORIZADO = "NO AUTORIZADO";


    public RespuestaProcesoGetDto responderProceso(RespuestaProceso respuestaProceso, UUID idDocumento) {
        System.out.println("respuesta proceso get dto . ");
        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (respuestaProceso.getEstadoEnvio().equals("ENV")) {
            respuestaProcesoGetDto.setEstadoDocumento("ENV");
            respuestaProcesoGetDto.setIdDocumento(idDocumento);
            respuestaProcesoGetDto.setNumeroAutorizacion("");
            respuestaProcesoGetDto.setEmailEstado(0);
        }

        if (respuestaProceso.getEstadoEnvio().equals("DEV")) {
            respuestaProcesoGetDto.setEstadoDocumento("DEV");
            respuestaProcesoGetDto.setIdDocumento(idDocumento);
            //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
        }
        if (respuestaProceso.getEstadoEnvio().equals("REC")) {
            if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                respuestaProcesoGetDto.setEstadoDocumento("NOA");
                respuestaProcesoGetDto.setIdDocumento(idDocumento);
                //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
            }
            if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                respuestaProcesoGetDto.setEstadoDocumento("AUT");
                //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
                respuestaProcesoGetDto.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                respuestaProcesoGetDto.setEmailEstado(respuestaProceso.getEmailEstado());
                respuestaProcesoGetDto.setIdDocumento(idDocumento);
            }
        }
        return respuestaProcesoGetDto;
    }


}
