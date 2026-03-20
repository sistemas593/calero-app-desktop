package recepcion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "validarComprobanteResponse",
        propOrder = {"respuestaRecepcionComprobante"}
)
public class ValidarComprobanteResponse {
    @XmlElement(
            name = "RespuestaRecepcionComprobante"
    )
    protected RespuestaSolicitud respuestaRecepcionComprobante;

    public ValidarComprobanteResponse() {
    }

    public RespuestaSolicitud getRespuestaRecepcionComprobante() {
        return this.respuestaRecepcionComprobante;
    }

    public void setRespuestaRecepcionComprobante(RespuestaSolicitud value) {
        this.respuestaRecepcionComprobante = value;
    }
}
