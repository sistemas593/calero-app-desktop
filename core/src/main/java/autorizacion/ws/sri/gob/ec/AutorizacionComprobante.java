package autorizacion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "autorizacionComprobante",
        propOrder = {"claveAccesoComprobante"}
)
public class AutorizacionComprobante {
    protected String claveAccesoComprobante;

    public AutorizacionComprobante() {
    }

    public String getClaveAccesoComprobante() {
        return this.claveAccesoComprobante;
    }

    public void setClaveAccesoComprobante(String value) {
        this.claveAccesoComprobante = value;
    }
}
