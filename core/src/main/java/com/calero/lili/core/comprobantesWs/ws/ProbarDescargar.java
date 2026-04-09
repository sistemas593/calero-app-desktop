package com.calero.lili.core.comprobantesWs.ws;

import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import com.calero.lili.core.comprobantesWs.ws.dtos.autorizacion.AutorizacionRequestDto;
import com.calero.lili.core.comprobantesWs.ws.services.AutorizacionServiceImpl;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProbarDescargar {

    public static void main(String[] args) {
        AutorizacionRequestDto aut = new AutorizacionRequestDto();
        aut.setClaveAcceso("0703202601179270558400120010040000078690000805610");
        aut.setAmbiente("2");
        AutorizacionServiceImpl service = new AutorizacionServiceImpl();

        RespuestaComprobante res = null;
        try {
            res = service.consulta(aut);
        } catch (Exception ex) {
            System.out.println("El ws del SRI no esta disponible: " + ex.getMessage());
        }

        System.out.println(res);

    }
}
