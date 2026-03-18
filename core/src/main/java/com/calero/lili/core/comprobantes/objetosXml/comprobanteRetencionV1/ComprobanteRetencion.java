package com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencionV1;

import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement // DEFINE RAIZ DEL DOC XML
@XmlType(propOrder = {"infoTributaria", "infoCompRetencion", "impuesto", "campoAdicional"})
public class ComprobanteRetencion {
    private String id;
    private String version;
    private InfoTributaria infoTributaria;
    private InfoCompRetencion infoCompRetencion;
    private List<Impuesto> impuesto;

    private List<CampoAdicional> campoAdicional;

    @XmlElementWrapper(name = "impuestos") // envoltorio
    public List<Impuesto> getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(List<Impuesto> impuesto) {
        this.impuesto = impuesto;
    }

    @XmlElementWrapper(name = "infoAdicional") // envoltorio
    public List<CampoAdicional> getCampoAdicional() {
        return campoAdicional;
    }

    public void setCampoAdicional(List<CampoAdicional> campoAdicional) {
        this.campoAdicional = campoAdicional;
    }

    public InfoCompRetencion getInfoCompRetencion() {
        return infoCompRetencion;
    }

    public void setInfoCompRetencion(InfoCompRetencion infoCompRetencion) {
        this.infoCompRetencion = infoCompRetencion;
    }

    public InfoTributaria getInfoTributaria() {
        return infoTributaria;
    }

    public void setInfoTributaria(InfoTributaria infoTributaria) {
        this.infoTributaria = infoTributaria;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static void main(String args[]){

        //////////////////////////////////////////
        ComprobanteRetencion f1= new ComprobanteRetencion();
        //////////////////////////////////////////

        //////////////////////////////////////////
        f1.setId("comprobante");
        f1.setVersion("1.0");
        InfoTributaria It=new InfoTributaria();
        It.setAmbiente("1");
        It.setClaveAcceso("2110201101179214673900110020010000000011234567813");
        It.setCodDoc("01");
        It.setDirMatriz("Pinta y Amazonas");
        It.setContribuyenteRimpe("CONTRIBUYENTE RÉGIMEN RIMPE");
        It.setEstab("001");
        It.setNombreComercial("comerc");
        It.setPtoEmi("001");
        It.setRazonSocial("Javier Cálero");
        It.setRuc("1714406236001");
        It.setSecuencial("000000124");
        It.setTipoEmision("1");
        f1.setInfoTributaria(It); // seteamos como valor de factura
        //////////////////////////////////////////

        //////////////////////////////////////////
        InfoCompRetencion If1=new InfoCompRetencion();
        If1.setFechaEmision("01/12/2013");
        If1.setDirEstablecimiento("Pamela Egas");
        If1.setObligadoContabilidad("NO");
        If1.setTipoIdentificacionSujetoRetenido("04");
        If1.setRazonSocialSujetoRetenido("CALERO ANDRADE RICARDO JAVIER ");
        If1.setIdentificacionSujetoRetenido("1714406236001");
        If1.setPeriodoFiscal("09/2018");

        f1.setInfoCompRetencion(If1);

        //////////////////////////////////////////
            Impuesto Tot1=new Impuesto();
            Tot1.setCodigo("1");
            Tot1.setCodigoRetencion("304A");
            Tot1.setBaseImponible("1000.00");
            Tot1.setPorcentajeRetener("8.00");
            Tot1.setValorRetenido("12.00");
            Tot1.setCodDocSustento("01");
            Tot1.setNumDocSustento("001002000000642");
            Tot1.setFechaEmisionDocSustento("04/09/2018");

            List<Impuesto> listaImpuesto=new ArrayList<Impuesto>();
            listaImpuesto.add(Tot1);

        f1.setImpuesto(listaImpuesto);

        //////////////////////////////////////////


        //////////////////////////////////////////

        //////////////////////////////////////////
        CampoAdicional Camp1=new CampoAdicional();
        Camp1.setNombre("nombre");
        Camp1.setValor("xxxxx");
        CampoAdicional Camp2=new CampoAdicional();
        Camp2.setNombre("nombre");
        Camp2.setValor("nombre impuesto");
        List<CampoAdicional> Campolista=new ArrayList<CampoAdicional>();
        Campolista.add(Camp1);
        Campolista.add(Camp2);
        f1.setCampoAdicional(Campolista);
        //////////////////////////////////////////



        OutputStreamWriter out = null;
        try {
            JAXBContext context = JAXBContext.newInstance(
                    new Class[]{ComprobanteRetencion.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "comprobanteRetencionv1.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
