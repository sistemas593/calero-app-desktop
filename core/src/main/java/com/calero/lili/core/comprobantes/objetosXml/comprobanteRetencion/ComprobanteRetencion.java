package com.calero.lili.core.comprobantes.objetosXml.comprobanteRetencion;


import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.core.comprobantes.objetosXml.factura.TotalImpuesto;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement // DEFINE RAIZ DEL DOC XML
@XmlType(propOrder = {"infoTributaria", "infoCompRetencion", "docSustento", "campoAdicional"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ComprobanteRetencion {
    private String id;
    private String version;
    private InfoTributaria infoTributaria;
    private InfoCompRetencion infoCompRetencion;
    private List<DocSustento> docSustento;
    private List<CampoAdicional> campoAdicional;

    @XmlElementWrapper(name = "docsSustento") // envoltorio
    public List<DocSustento> getDocSustento() {
        return docSustento;
    }

    public void setDocSustento(List<DocSustento> docSustento) {
        this.docSustento = docSustento;
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
        If1.setContribuyenteEspecial("4521");
        If1.setObligadoContabilidad("NO");
        If1.setTipoIdentificacionSujetoRetenido("04");
        If1.setParteRel("NO");
        If1.setRazonSocialSujetoRetenido("CALERO ANDRADE RICARDO JAVIER ");
        If1.setIdentificacionSujetoRetenido("1714406236001");

        //////////////////////////////////////////
        TotalImpuesto Tot1=new TotalImpuesto();
        Tot1.setCodigo("1");
        Tot1.setCodigoPorcentaje("3072");
        Tot1.setBaseImponible("1000.00");
        Tot1.setTarifa("5.00");
        Tot1.setValor("10");

        List<TotalImpuesto> listaTotalImpuesto=new ArrayList<TotalImpuesto>();
        listaTotalImpuesto.add(Tot1);
        //If1.se(listaTotalImpuesto);
        f1.setInfoCompRetencion(If1);


        //////////////////////////////////////////

        //////////////////////////////////////////
        DocSustento Det1=new DocSustento();
        Det1.setCodSustento("01");
        Det1.setCodDocSustento("01");
        Det1.setNumDocSustento("001002000001920");
        Det1.setFechaEmisionDocSustento("13/07/2023");
        Det1.setFechaRegistroContable("14/07/2023");
        Det1.setNumAutDocSustento("1307202301171440623600120010020000019201801450212");
        Det1.setPagoLocExt("01");
        Det1.setPaisEfecPago("593");
        Det1.setTotalComprobantesReembolso("0.00");
        Det1.setTotalBaseImponibleReembolso("0.00");
        Det1.setTotalImpuestoReembolso("0.00");
        Det1.setTotalSinImpuestos("80.00");
        Det1.setImporteTotal("89.60");

        //////////////////////////////////////////

        //////////////////////////////////////////
            ImpuestoDocSustento impu1= new ImpuestoDocSustento();
            impu1.setCodImpuestoDocSustento("2");
            impu1.setCodigoPorcentaje("3072");
            impu1.setBaseImponible("295000.00");
            impu1.setTarifa("5");
            impu1.setValorImpuesto("9.60");

            List<ImpuestoDocSustento> listaImpuesto=new ArrayList<ImpuestoDocSustento>();
            listaImpuesto.add(impu1);
            Det1.setImpuestoDocSustento(listaImpuesto);

            Retencion rete1= new Retencion();
            rete1.setCodigo("1");
            rete1.setCodigoRetencion("343");
            rete1.setBaseImponible("80.00");
            rete1.setPorcentajeRetener("1.00");
            rete1.setValorRetenido("0.80");

            List<Retencion> listaRetencion=new ArrayList<Retencion>();
            listaRetencion.add(rete1);
            Det1.setRetencion(listaRetencion);

        List<DocSustento> listaSustentos=new ArrayList<DocSustento>();
        listaSustentos.add(Det1);
        f1.setDocSustento(listaSustentos);
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
                    "comprobanteRetencion.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
