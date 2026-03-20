package com.calero.lili.core.comprobantesWs.ws.services;

import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOffline;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobantesOfflineService;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutorizacionServiceImpl {

    public RespuestaComprobante consulta(AutorizacionRequestDto request) {

        // autorizacionComprobantesOfflineService
        //final String AUTORIZACIONCOMPROBANTESOFFLINESERVICE_WSDL_LOCATION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl" ;
        //final String URL_PRUEBAS_RECEPCION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl" ;

        // recepccionComprobantesOfflineService
        //final String URL_PRODUCCION_CONSULTAS = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl" ;
        //final String URL_PRODUCCION_RECEPCION = "https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl" ;

        String claveAccesoComprobante = request.getClaveAcceso();

        AutorizacionComprobantesOfflineService service = new AutorizacionComprobantesOfflineService(request.getAmbiente());
        AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
        RespuestaComprobante result = port.autorizacionComprobante(claveAccesoComprobante);

        System.out.println("Comprobantes: "+result.getNumeroComprobantes());
        System.out.println("Clave de acceso: "+result.getClaveAccesoConsultada());
        //System.out.println("Autorizaciones: "+result.getAutorizaciones());
        //System.out.println(result.getAutorizaciones().getAutorizacion());

//        List<Autorizacion> listaAutorizaciones = result.getAutorizaciones().getAutorizacion();

        AutorizacionResponseDto dto = new AutorizacionResponseDto();
        dto.setClaveAccesoConsultada(result.getClaveAccesoConsultada());
//        if (listaAutorizaciones.isEmpty()) {
//            System.out.println("No existe un documento con la clave de acceso.");
//            dto.setNumeroComprobantes(0);

//        } else {
//            System.out.println("SI EXISTEN DOCUMENTOS");
//            List<AutorizacionResponseDto.Autorizaciones> listaAutorizacionesDto = new ArrayList<>();
//            dto.setNumeroComprobantes(Integer.valueOf(result.getNumeroComprobantes()));
//            for (Autorizacion autorizacion : listaAutorizaciones) {
//                AutorizacionResponseDto.Autorizaciones autorizacionDto = new AutorizacionResponseDto.Autorizaciones();
//                autorizacionDto.setEstado("AUTORIZADO");
//                autorizacionDto.setAmbiente(autorizacion.getEstado());
//                autorizacionDto.setComprobante(autorizacion.getComprobante());
//                autorizacionDto.setAmbiente(autorizacion.getAmbiente());
//                autorizacionDto.setNumeroAutorizacion(autorizacion.getNumeroAutorizacion());
//                autorizacionDto.setFechaAutorizacion(String.valueOf(autorizacion.getFechaAutorizacion()));
//                listaAutorizacionesDto.add(autorizacionDto);
//
////                String estado = autorizacion.getEstado();
//
////                AutorizacionRespuestaLili au= new AutorizacionRespuestaLili();
//
////                au.setEstadoAutorizacion(estado);
////                au.setAutorizacion(autorizacion.getNumeroAutorizacion());
////                au.setFechaAutorizacion(autorizacion.getFechaAutorizacion().toString());
//
//                //autorizacion.getFechaAutorizacion() ;
//
////                if (autorizacion.getEstado().compareTo("AUTORIZADO") == 0) {
////                    au.setComprobante(autorizacion.getComprobante());
//                    //strMensaje.append("AUTORIZADO").append(".").append(autorizacion.getNumeroAutorizacion());
//
////                    autorizacion.setComprobante((new StringBuilder()).append("<![CDATA[").append(autorizacion.getComprobante()).append("]]>").toString());
////
////                    JAXBContext jc = null;
////                    try {
////                        jc = JAXBContext.newInstance(Autorizacion.class);
////                        Marshaller marshaller = jc.createMarshaller();
////                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
////                        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
////                        Writer writer = new FileWriter("C:\\web\\calero-app-back\\feAutorizados\\nuevo.xml");
////                        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
////                        marshaller.marshal(new JAXBElement<Autorizacion>(new QName("autorizacion"), Autorizacion.class, autorizacion), writer);
////                        writer.close();
////                    } catch (JAXBException e) {
////                        throw new RuntimeException(e);
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                }
//
////                if (autorizacion.getEstado().compareTo("NO AUTORIZADO") == 0) {
////
////                    List<Mensaje> mensajes = autorizacion.getMensajes().getMensaje();
//////                    String strMensaje = "";
////                    if (mensajes.isEmpty()) {
//////                        strMensaje.append("NO AUTORIZADO.E34.ERROR INTERNO.No se pudo emitir el documento.");
////                    } else {
////                        for (Mensaje mensaje : mensajes) {
//////                            strMensaje.append("NO AUTORIZADO").append(".").append(mensaje.getMensaje()).append(".").append(mensaje.getInformacionAdicional()).append("\n");
////                        }
////                    }
////
////                }
////                else {
//
//                    /*
//                    //GUARDAMOS COMPROBANTE NO AUTORIZADO
//                    autorizacion.setComprobante((new StringBuilder()).append("<![CDATA[").append(autorizacion.getComprobante()).append("]]>").toString());
//                    JAXBContext jc = JAXBContext.newInstance(Autorizacion.class);
//                    Marshaller marshaller = jc.createMarshaller();
//                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//                    marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
//                    Writer writer = new FileWriter(nombreArchivoNA);
//                    writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//                    marshaller.marshal(new JAXBElement<Autorizacion>(new QName("autorizacion"), Autorizacion.class, autorizacion), writer);
//                    writer.close();
//
//                    List<Mensaje> mensajes = autorizacion.getMensajes().getMensaje();
//                    if (mensajes.isEmpty()) {
//                        strMensaje.append("NO AUTORIZADO.E34.ERROR INTERNO.No se pudo emitir el documento.");
//                    } else {
//                        for (Mensaje mensaje : mensajes) {
//                            strMensaje.append("NO AUTORIZADO").append(".").append(mensaje.getMensaje()).append(".").append(mensaje.getInformacionAdicional()).append("\n");
//                        }
//                    }
//
//                     */
////                }
//            }
//            dto.setAutorizaciones(listaAutorizacionesDto);
//        }

        return result;
    }

}
