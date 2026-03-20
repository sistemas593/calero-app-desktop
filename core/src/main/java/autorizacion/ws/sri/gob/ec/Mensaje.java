package autorizacion.ws.sri.gob.ec;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "mensaje",
        propOrder = {"identificador", "mensaje", "informacionAdicional", "tipo"}
)
public class Mensaje {
    protected String identificador;
    protected String mensaje;
    protected String informacionAdicional;
    protected String tipo;

    public Mensaje() {
    }

    public String getIdentificador() {
        return this.identificador;
    }

    public void setIdentificador(String value) {
        this.identificador = value;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String value) {
        this.mensaje = value;
    }

    public String getInformacionAdicional() {
        return this.informacionAdicional;
    }

    public void setInformacionAdicional(String value) {
        this.informacionAdicional = value;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(String value) {
        this.tipo = value;
    }
}
