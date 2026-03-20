package com.calero.lili.core.comprobantesWs.ws.services;

import com.calero.lili.core.comprobantesWs.ws.dtos.recepcion.RecepcionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import recepcion.ws.sri.gob.ec.*;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RecepcionServiceImpl {
    public static final String ESTADO_RECIBIDA = "RECIBIDA";
    public static final String ESTADO_DEVUELTA = "DEVUELTA";
    public RespuestaSolicitud enviar(RecepcionRequestDto request) {

        byte[] decodedBytes = Base64.getDecoder().decode(request.getFileXml());

        // Inicializa el array de bytes
//        byte[] archivoBytes1 = new byte[0];

            // Convierte el string a un array de bytes
//        try {
//            archivoBytes1 = decodedBytes.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }

//        System.out.println("entra");
//        File xmlFile=new File("feFirmados\\0806202301171440623600120010010000000021234567818.XML");
//
//        byte[] archivoBytes = new byte[0];
//        try {
//            archivoBytes = ArchivoUtils.convertirArchivoAByteArray(xmlFile);
//        } catch (IOException e) {
//            System.out.println("error archivoooo");
//            throw new RuntimeException(e);
//        }

        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService(request.getAmbiente());
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        RespuestaSolicitud result = port.validarComprobante(decodedBytes);

//        System.out.println(result.getEstado());
//        System.out.println(result.getComprobantes());
//
//
//        if (result.getEstado().equals(ESTADO_RECIBIDA)){
//            System.out.println(ESTADO_RECIBIDA);
//        }
//        if (result.getEstado().equals(ESTADO_DEVUELTA)){
//            //dto.setEstado(ESTADO_DEVUELTA);
//            System.out.println(ESTADO_DEVUELTA);
//            List<Comprobante> comprobante =result.getComprobantes().getComprobante();
//            List<RecepcionResponseDto.Comprobantes> listaComprobantesDto = new ArrayList<>();
//            for(Comprobante com : comprobante){
//                RecepcionResponseDto.Comprobantes comprobanteDto = new RecepcionResponseDto.Comprobantes();
//                comprobanteDto.setClaveAcceso(com.getClaveAcceso());
//                System.out.println(com.getMensajes().getMensaje());
//                    List<Mensaje> listaMensajes = com.getMensajes().getMensaje();
//                    for(Mensaje men : listaMensajes){
//                        System.out.println(men.getIdentificador());
//                        System.out.println(men.getMensaje());
//                        RecepcionResponseDto.Mensajes mensajeDto = new RecepcionResponseDto.Mensajes();
//                        mensajeDto.setTipo(men.getTipo());
//                        mensajeDto.setIdentificador(men.getIdentificador());
//                        mensajeDto.setMensaje(men.getMensaje());
//                        mensajeDto.setInformacionAdicional(men.getInformacionAdicional());
//                    }
//                listaComprobantesDto.add(comprobanteDto);
//            }
//        }
        return result;
    }

}
