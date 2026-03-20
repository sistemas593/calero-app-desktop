
package recepcion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "validarComprobante",
        propOrder = {"xml"}
)
public class ValidarComprobante {
    protected byte[] xml;

    public ValidarComprobante() {
    }

    public byte[] getXml() {
        return this.xml;
    }

    public void setXml(byte[] value) {
        this.xml = value;
    }
}
