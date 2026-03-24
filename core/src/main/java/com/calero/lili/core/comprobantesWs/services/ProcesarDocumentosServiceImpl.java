package com.calero.lili.core.comprobantesWs.services;

import autorizacion.ws.sri.gob.ec.Autorizacion;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
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
import com.calero.lili.core.modAdminEmpresas.AdEmpresaEntity;
import com.calero.lili.core.modAdminEmpresas.AdEmpresasRepository;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.CpLiquidacionesEntity;
import com.calero.lili.core.modCompras.modComprasLiquidaciones.LiquidacionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.ComprasRetencionesRepository;
import com.calero.lili.core.modCompras.modComprasRetenciones.CpRetencionesEntity;
import com.calero.lili.core.modVentas.VtVentaEntity;
import com.calero.lili.core.modVentas.VtVentasRepository;
import com.calero.lili.core.modVentasGuias.VtGuiaEntity;
import com.calero.lili.core.modVentasGuias.VtGuiasRepository;
import com.calero.lili.core.utils.AESUtils;
import com.calero.lili.core.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recepcion.ws.sri.gob.ec.Comprobante;
import recepcion.ws.sri.gob.ec.Mensaje;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;
import xades4j.SignXmlString;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private final ProcesarEnvioCorreoServiceImpl procesarEnvioCorreo;
    private final ProcesarFirmarXmlServiceImpl procesarFirmarXmlService;
    @Autowired
    private final SignXmlString signXmlString;

    private final ProcesarPausarServiceImpl procesarPausarService;

//    private static String projectId = "caleroapp";
//    private static String bucketName = "caleroapp-bucket-sgn";

    private final AdEmpresasRepository adEmpresasRepository;
    private final BuscarDatosEmpresa buscarDatosEmpresa;
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

    public RespuestaProcesoGetDto procesarFacNcNd(Long idData, Long idEmpresa, UUID id, String origenCertificado) {

        System.out.println("Inicio del proceso");

        DatosEmpresaDto datosEmpresaDto = null;

        switch (origenCertificado) {

            case "WEB" -> datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

            case "LOC" -> datosEmpresaDto = obtenerLocalDatosEmpresa(idData, idEmpresa);
        }


        // LISTO PARA ENVIAR RECEPCION
        VtVentaEntity venta1 = vtVentaRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Documento de venta {0} no exists", id)));

        if (!venta1.getEstadoDocumento().equals(EstadoDocumento.ENV) && !venta1.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        System.out.println("1. estado del documento en la base de datos:" + venta1.getEstadoDocumento());
        String claveAcceso = venta1.getClaveAcceso();
        Integer ambiente = Integer.valueOf(venta1.getAmbiente());
        String estadoDocumento = venta1.getEstadoDocumento().toString();
        String comprobante = venta1.getComprobante();

        RespuestaProceso respuestaProceso = null;
        respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante, datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd());
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("procesarFacNcNd 1. Guardar fc/nc/nd como recibida");
                venta1.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    System.out.println("procesarFacNcNd Guardar fc/nc/nd como autorizada");

                    venta1.setEstadoDocumento(EstadoDocumento.AUT);
                    venta1.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    venta1.setComprobante(respuestaProceso.getComprobante());
                    venta1.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(venta1.getEmail())) {

                        if (!venta1.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(venta1.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(venta1.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(venta1.getTercero().getTercero());

                            switch (venta1.getTipoVenta()) {
                                case "FAC":
                                    envioCorreoDto.setCodigoDocumento("01");
                                    break;
                                case "NCR":
                                    envioCorreoDto.setCodigoDocumento("04");
                                    break;
                                case "NDB":
                                    envioCorreoDto.setCodigoDocumento("05");
                            }

                            envioCorreoDto.setSecuencial(venta1.getSecuencial());
                            envioCorreoDto.setSerie(venta1.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
                            envioCorreoDto.setEmail(venta1.getEmail());
                            Integer respuestaEnvioCorreo = procesarEnvioCorreo.enviarCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            venta1.setEmailEstado(respuestaEnvioCorreo);
                            respuestaProceso.setEmailEstado(respuestaEnvioCorreo);

                        }
                    }
                }

                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesarFacNcNd Guardar fc/nc/nd como NO autorizada");
                    venta1.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        venta1.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                vtVentaRepository.save(venta1);
                break;
            case "DEV":
                System.out.println("2. Guardar fc/nc/nd como devuelto");
                venta1.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    venta1.setMensajes(respuestaProceso.getMensajes());
                }
                vtVentaRepository.save(venta1);
                break;
        }

        return responderProceso(respuestaProceso);

    }


    /**
     * Obtiene los datos de la empresa desde archivos locales (modo LOC).
     * Lee el .p12 y el logo desde las rutas almacenadas en la entidad empresa.
     *
     * @param idData    identificador del tenant
     * @param idEmpresa identificador de la empresa
     * @param password  contraseña del certificado .p12 proporcionada por el llamador
     * @return DatosEmpresaDto con el stream del certificado, la contraseña y los bytes del logo
     */
    /**
     * Carga el certificado .p12 y el logo desde cualquier ruta del sistema de archivos local.
     * Las rutas se configuran en la entidad empresa (rutaArchivoFirma, rutaLogo).
     *
     * Ejemplos de rutas válidas:
     *   Windows : C:\Users\Ismael\Documents\firma.p12
     *   Linux   : /home/ismael/firmas/empresa.p12
     *   Relativa: firmas/empresa.p12  (relativa al directorio de trabajo)
     *
     * @param idData    identificador del tenant
     * @param idEmpresa identificador de la empresa
     * @param password  contraseña del certificado .p12
     * @return DatosEmpresaDto con el stream del certificado, la contraseña y los bytes del logo
     */
    /**
     * Lee el .p12 y el logo desde las rutas guardadas en la entidad empresa (BD).
     * La contraseña viene por parámetro; si es nula usa la almacenada en la entidad.
     */
    private DatosEmpresaDto obtenerLocalDatosEmpresa(Long idData, Long idEmpresa) {

        AdEmpresaEntity empresa = adEmpresasRepository.findById(idData, idEmpresa)
                .orElseThrow(() -> new GeneralException(
                        MessageFormat.format("Data {0}, Empresa {1} no existe", idData, idEmpresa)));

        String pwd = AESUtils.decrypt(empresa.getContraseniaFirma());

        String rutaFirma = empresa.getRutaArchivoFirma();
        if (rutaFirma == null || rutaFirma.isBlank()) {
            throw new GeneralException(
                    "La empresa no tiene configurada la ruta del archivo de firma (.p12). " +
                            "Configure la ruta en la sección 'Firma y Envío' del formulario de empresa.");
        }

        Path pathP12 = Paths.get(rutaFirma);
        if (!Files.exists(pathP12)) {
            throw new GeneralException(MessageFormat.format(
                    "El archivo .p12 no existe en la ruta configurada: {0}", pathP12.toAbsolutePath()));
        }

        InputStream inputStreamFirma;
        try {
            inputStreamFirma = new FileInputStream(pathP12.toFile());
        } catch (Exception e) {
            throw new GeneralException(MessageFormat.format(
                    "Error al leer el archivo .p12: {0}", e.getMessage()));
        }

        // ── 4. Leer logo desde la ruta guardada en BD (opcional) ──────────────
        byte[] imageBytes = null;
        String rutaLogo = empresa.getRutaLogo();
        if (rutaLogo != null && !rutaLogo.isBlank()) {
            Path pathLogo = Paths.get(rutaLogo);
            if (Files.exists(pathLogo)) {
                try {
                    imageBytes = Files.readAllBytes(pathLogo);
                } catch (Exception e) {
                    System.out.println("Advertencia: no se pudo leer el logo: " + e.getMessage());
                }
            }
        }

        return DatosEmpresaDto.builder()
                .inputStreamFileSgn(inputStreamFirma)
                .imageBytes(imageBytes)
                .pwd(pwd)
                .build();
    }

    public RespuestaProcesoGetDto procesarGuiaRemision(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Inicio del proceso");

        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        // LISTO PARA ENVIAR RECEPCION
        VtGuiaEntity venta1 = vtGuiasRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Guia Remision {0} no exists", id)));

        if (!venta1.getEstadoDocumento().equals(EstadoDocumento.ENV) && !venta1.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }


        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + venta1.getEstadoDocumento());
        String claveAcceso = venta1.getClaveAcceso();
        Integer ambiente = Integer.valueOf(venta1.getAmbiente());
        String estadoDocumento = venta1.getEstadoDocumento().toString();
        String comprobante = venta1.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante, datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd());
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar guia como recibida");
                venta1.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    venta1.setEstadoDocumento(EstadoDocumento.AUT);
                    venta1.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    venta1.setComprobante(respuestaProceso.getComprobante());
                    venta1.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(venta1.getEmail())) {

                        if (!venta1.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(venta1.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(venta1.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(venta1.getDestinatario().getTercero());
                            envioCorreoDto.setCodigoDocumento("06");
                            envioCorreoDto.setSecuencial(venta1.getSecuencial());
                            envioCorreoDto.setSerie(venta1.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
                            envioCorreoDto.setEmail(venta1.getEmail());

                            Integer respuestaEnvioCorreo = procesarEnvioCorreo.enviarCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            venta1.setEmailEstado(respuestaEnvioCorreo);
                            respuestaProceso.setEmailEstado(respuestaEnvioCorreo);
                        }
                    }

                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar guia como NO autorizada");
                    venta1.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        venta1.setMensajes(respuestaProceso.getMensajes());
                    }
                }


                vtGuiasRepository.save(venta1);
                break;
            case "DEV":
                System.out.println("2. Guardar guia como devuelto");
                venta1.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    venta1.setMensajes(respuestaProceso.getMensajes());
                }
                vtGuiasRepository.save(venta1);
                break;
        }
        return responderProceso(respuestaProceso);
    }


    public RespuestaProcesoGetDto procesarLiquidacion(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Inicio del proceso");
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        // LISTO PARA ENVIAR RECEPCION
        CpLiquidacionesEntity venta1 = liquidacionesRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Liquidacion {0} no exists", id)));

        if (!venta1.getEstadoDocumento().equals(EstadoDocumento.ENV) && !venta1.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + venta1.getEstadoDocumento());
        String claveAcceso = venta1.getClaveAcceso();
        Integer ambiente = Integer.valueOf(venta1.getAmbiente());
        String estadoDocumento = venta1.getEstadoDocumento().toString();
        String comprobante = venta1.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante, datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd());
        respuestaProceso.setEmailEstado(0);

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar liquidacion como recibida");
                venta1.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    venta1.setEstadoDocumento(EstadoDocumento.AUT);
                    venta1.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                    venta1.setComprobante(respuestaProceso.getComprobante());
                    venta1.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(venta1.getEmail())) {

                        if (!venta1.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(venta1.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacion());
                            envioCorreoDto.setFechaAutorizacion(venta1.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(venta1.getProveedor().getTercero());
                            envioCorreoDto.setCodigoDocumento("03");
                            envioCorreoDto.setSecuencial(venta1.getSecuencial());
                            envioCorreoDto.setSerie(venta1.getSerie());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmision()));
                            envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
                            envioCorreoDto.setEmail(venta1.getEmail());

                            Integer respuestaEnvioCorreo = procesarEnvioCorreo.enviarCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            venta1.setEmailEstado(respuestaEnvioCorreo);
                            respuestaProceso.setEmailEstado(respuestaEnvioCorreo);
                        }
                    }
                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar liquidacion como NO autorizada");
                    venta1.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        venta1.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                liquidacionesRepository.save(venta1);
                break;
            case "DEV":
                System.out.println("2. Guardar liquidacion como devuelto");
                venta1.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    venta1.setMensajes(respuestaProceso.getMensajes());
                }
                liquidacionesRepository.save(venta1);
                break;
        }
        return responderProceso(respuestaProceso);
    }

    public RespuestaProcesoGetDto procesarComprobanteRetencion(Long idData, Long idEmpresa, UUID id) {

        System.out.println("Inicio del proceso");
        DatosEmpresaDto datosEmpresaDto = buscarDatosEmpresa.buscarEmpresa(idData, idEmpresa);

        // LISTO PARA ENVIAR RECEPCION
        CpRetencionesEntity venta1 = comprasRetencionesRepository.findByIdEntity(idData, idEmpresa, id, null, null).
                orElseThrow(() -> new GeneralException(MessageFormat.format("Comprobante retencion {0} no exists", id)));

        if (!venta1.getEstadoDocumento().equals(EstadoDocumento.ENV) && !venta1.getEstadoDocumento().equals(EstadoDocumento.REC)) {
            throw new GeneralException("El estado de documento no es para enviar o recibido");
        }

        // SI EL DOCUMENTO ESTA PARA ENVIAR
        System.out.println("1. estado del documento en la base de datos:" + venta1.getEstadoDocumento());
        String claveAcceso = venta1.getClaveAcceso();
        Integer ambiente = Integer.valueOf(venta1.getAmbiente());
        String estadoDocumento = venta1.getEstadoDocumento().toString();
        String comprobante = venta1.getComprobante();

        // OJOOOOO ENVIAR DOCUMENTOS CON ESTADO ENV O REC
        RespuestaProceso respuestaProceso = procesarDocumento(estadoDocumento, claveAcceso, ambiente, comprobante, datosEmpresaDto.getInputStreamFileSgn(), datosEmpresaDto.getPwd());

        System.out.println("Respuesta envio:" + respuestaProceso.getEstadoEnvio());
        System.out.println("Respuesta autorizacion:" + respuestaProceso.getEstadoAutorizacion());
        switch (respuestaProceso.getEstadoEnvio()) {
            case "REC":
                System.out.println("1. Guardar retencion como recibida");
                venta1.setEstadoDocumento(EstadoDocumento.REC);
                if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                    venta1.setEstadoDocumento(EstadoDocumento.AUT);
                    venta1.setNumeroAutorizacionRetencion(respuestaProceso.getNumeroAutorizacion());
                    venta1.setComprobante(respuestaProceso.getComprobante());
                    venta1.setFechaAutorizacion(respuestaProceso.getFechaAutorizacion());

                    if (!Objects.isNull(venta1.getEmail())) {

                        if (!venta1.getEmail().isEmpty()) {
                            EnvioCorreoDto envioCorreoDto = new EnvioCorreoDto();
                            envioCorreoDto.setComprobante(venta1.getComprobante());
                            envioCorreoDto.setNumeroAutorizacion(venta1.getNumeroAutorizacionRetencion());
                            envioCorreoDto.setFechaAutorizacion(venta1.getFechaAutorizacion());
                            envioCorreoDto.setNombreReceptor(venta1.getProveedor().getTercero());
                            envioCorreoDto.setCodigoDocumento("07");
                            envioCorreoDto.setSecuencial(venta1.getSecuencialRetencion());
                            envioCorreoDto.setSerie(venta1.getSerieRetencion());
                            envioCorreoDto.setFechaEmision(DateUtils.toString(venta1.getFechaEmisionRetencion()));
                            envioCorreoDto.setClaveAcceso(venta1.getClaveAcceso());
                            envioCorreoDto.setEmail(venta1.getEmail());

                            Integer respuestaEnvioCorreo = procesarEnvioCorreo.enviarCorreo(envioCorreoDto, datosEmpresaDto.getImageBytes());
                            venta1.setEmailEstado(respuestaEnvioCorreo);
                            respuestaProceso.setEmailEstado(respuestaEnvioCorreo);
                        }
                    }
                }
                if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                    System.out.println("procesar Guardar retencion como NO autorizada");
                    venta1.setEstadoDocumento(EstadoDocumento.NOA);
                    if (!Objects.isNull(respuestaProceso.getMensajes())) {
                        venta1.setMensajes(respuestaProceso.getMensajes());
                    }
                }

                comprasRetencionesRepository.save(venta1);
                break;
            case "DEV":
                System.out.println("2. Guardar retencion como devuelto");
                venta1.setEstadoDocumento(EstadoDocumento.DEV);
                if (!Objects.isNull(respuestaProceso.getMensajes())) {
                    venta1.setMensajes(respuestaProceso.getMensajes());
                }
                comprasRetencionesRepository.save(venta1);
                break;
        }
        return responderProceso(respuestaProceso);
    }

    public RespuestaProceso procesarDocumento(String estadoDocumento, String claveAcceso, Integer ambiente, String comprobante, InputStream inputStreamFileSgn, String pwd) {
        RespuestaProceso respuestaProceso = new RespuestaProceso();
        switch (estadoDocumento) {
            case "ENV":
                System.out.println("procesarDocumento 1. Estado en la BDD ENV Enviar documento electronico");

                RespuestaProceso respuestaEnvio = procesarEnvio(claveAcceso, ambiente, comprobante, inputStreamFileSgn, pwd);

                respuestaProceso.setEstadoEnvio(respuestaEnvio.getEstadoEnvio().toString());

                // SI ESTA DEVUELTA AQUI ASIGNO LOS MENSAJES SI EXISTEN
                if (!Objects.isNull(respuestaEnvio.getMensajes())) {
                    respuestaProceso.setMensajes(respuestaEnvio.getMensajes());
                }

                System.out.println(respuestaProceso.getEstadoEnvio());
                respuestaProceso.setEstadoEnvio(respuestaProceso.getEstadoEnvio());
                if (respuestaProceso.getEstadoEnvio().equals("REC")) {
                    System.out.println("procesarDocumento: PROCESO DE AUTORIZACION");
                    //////////// PASO 2 AUTORIZACION
                    RespuestaAutorizacion respuestaAutorizacion1 = procesarAutorizacion(claveAcceso, ambiente);

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
                RespuestaAutorizacion respuestaAutorizacion1 = procesarAutorizacion(claveAcceso, ambiente);

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

    public RespuestaProceso procesarEnvio(String claveAcceso, Integer ambiente, String comprobante, InputStream inputStreamFileSgn, String pwd) {
        RespuestaProceso respuestaProceso = new RespuestaProceso();

        RespuestaComprobante result = consultarDocumento(claveAcceso, ambiente);

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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String comprobanteFirmadoBase64 = Base64.getEncoder().encodeToString(comprobanteFirmado.getBytes(StandardCharsets.UTF_8));
            // ENVIAR A AUTORIZAR
            RecepcionRequestDto requestDto = new RecepcionRequestDto();
            //String string = jsonNode.get("xmlFirmado").asText();
            requestDto.setFileXml(comprobanteFirmadoBase64);

            requestDto.setAmbiente(ambiente.toString());

            RespuestaEnvio respuestaEnvio = enviarAutorizar(requestDto);
            // si esta devuelta aqui seteo los mensajes si existen auto
            if (!Objects.isNull(respuestaEnvio.getMensajes())) {
                respuestaProceso.setMensajes(respuestaEnvio.getMensajes());
            }

            procesarPausarService.pausar();

            if (respuestaEnvio.getEstadoEnvio().equals(EstadoDocumento.REC)) {
                respuestaProceso.setEstadoEnvio("REC");
                RespuestaComprobante result2 = consultarDocumento(claveAcceso, ambiente);
                if (result2.getNumeroComprobantes() == null || result2.getNumeroComprobantes().equals("0")) {
                    System.out.println("procesarEnvioTodos ENVIADO ESTE MOMENTO / No existe aun un documento con la clave de acceso.");
                } else {
                    System.out.println("procesarEnvioTodos ENVIADO ESTE MOMENTO / ahora YA existe un documento con la clave de acceso.");
                }
            }
            if (respuestaEnvio.getEstadoEnvio().equals(EstadoDocumento.DEV)) {
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


    public RespuestaAutorizacion procesarAutorizacion(String claveAcceso, Integer ambiente) {
        RespuestaAutorizacion respuestaAutorizacion = new RespuestaAutorizacion();
        RespuestaComprobante result = consultarDocumento(claveAcceso, ambiente);
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
    public RespuestaComprobante consultarDocumento(String claveAcceso, Integer ambiente) {
        System.out.println("consultarDocumento. " + claveAcceso);
        //Consultar el documento
        AutorizacionRequestDto request = new AutorizacionRequestDto();
        request.setAmbiente(ambiente.toString());
        request.setClaveAcceso(claveAcceso);
        RespuestaComprobante result = autorizacionService.consulta(request);
        return result;
    }

    public RespuestaEnvio enviarAutorizar(RecepcionRequestDto requestDto) {
        RespuestaEnvio respuestaEnvio = new RespuestaEnvio();
        System.out.println("Enviando documento");

        RespuestaSolicitud responseDto = recepcionService.enviar(requestDto);
        List<Mensajes> mensajesLista = new ArrayList<>();
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
        respuestaEnvio.setMensajes(mensajesLista);
        return respuestaEnvio;
    }


    //public static final String ESTADO_RECIBIDA = "RECIBIDA";
    //public static final String ESTADO_DEVUELTA = "DEVUELTA";
    public static final String ESTADO_AUTORIZADO = "AUTORIZADO";
    public static final String ESTADO_NO_AUTORIZADO = "NO AUTORIZADO";


    public RespuestaProcesoGetDto responderProceso(RespuestaProceso respuestaProceso) {
        System.out.println("respuesta proceso get dto . ");
        RespuestaProcesoGetDto respuestaProcesoGetDto = new RespuestaProcesoGetDto();

        if (respuestaProceso.getEstadoEnvio().equals("DEV")) {
            respuestaProcesoGetDto.setEstadoDocumento("DEV");
            //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
        }
        if (respuestaProceso.getEstadoEnvio().equals("REC")) {
            if (respuestaProceso.getEstadoAutorizacion().equals("NOA")) {
                respuestaProcesoGetDto.setEstadoDocumento("NOA");
                //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
            }
            if (respuestaProceso.getEstadoAutorizacion().equals("AUT")) {
                respuestaProcesoGetDto.setEstadoDocumento("AUT");
                //respuestaProcesoGetDto.setMensajes(respuestaProceso.getMensajes());
                respuestaProcesoGetDto.setNumeroAutorizacion(respuestaProceso.getNumeroAutorizacion());
                respuestaProcesoGetDto.setEmailEstado(respuestaProceso.getEmailEstado());
            }
        }
        return respuestaProcesoGetDto;
    }


}
