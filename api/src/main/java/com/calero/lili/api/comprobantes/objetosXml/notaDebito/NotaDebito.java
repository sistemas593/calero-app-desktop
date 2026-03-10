package com.calero.lili.api.comprobantes.objetosXml.notaDebito;

import com.calero.lili.api.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.api.comprobantes.objetosXml.factura.InfoTributaria;
import com.calero.lili.api.comprobantes.objetosXml.factura.Pago;
import com.calero.lili.api.comprobantes.objetosXml.factura.TotalImpuesto;
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
@XmlType(propOrder = {"infoTributaria", "infoNotaDebito", "motivo", "campoAdicional"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotaDebito {
    private String id;
    private String version;
    private InfoTributaria infoTributaria;
    private InfoNotaDebito infoNotaDebito;
    private List<Motivo> motivo;
    private List<CampoAdicional> campoAdicional;

    @XmlElementWrapper(name = "motivos") // envoltorio
    public List<Motivo> getMotivo() {
        return motivo;
    }
    public void setMotivo(List<Motivo> motivo) {
        this.motivo = motivo;
    }


    @XmlElementWrapper(name = "infoAdicional") // envoltorio
    public List<CampoAdicional> getCampoAdicional() {
        return campoAdicional;
    }

    public void setCampoAdicional(List<CampoAdicional> campoAdicional) {
        this.campoAdicional = campoAdicional;
    }

    public InfoNotaDebito getInfoNotaDebito() {
        return infoNotaDebito;
    }

    public void setInfoNotaDebito(InfoNotaDebito infoNotaDebito) {
        this.infoNotaDebito = infoNotaDebito;
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
        NotaDebito f1= new NotaDebito();
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
        InfoNotaDebito If1=new InfoNotaDebito();
        If1.setFechaEmision("01/12/2013");
        If1.setDirEstablecimiento("Pamela Egas");
        If1.setContribuyenteEspecial("4521");
        If1.setObligadoContabilidad("NO");
        If1.setCodDocModificado("01");
        If1.setNumDocModificado("001-002-000001854");
        If1.setFechaEmisionDocSustento("12/04/2023");
        If1.setTipoIdentificacionComprador("04");
        If1.setRazonSocialComprador("PRUEBAS SERVICIO DERENTAS INTERNAS");
        If1.setIdentificacionComprador("1717740441001");
        If1.setTotalSinImpuestos("295000.00");
        If1.setValorTotal("0.04");


        //////////////////////////////////////////
        TotalImpuesto Tot1=new TotalImpuesto();
        TotalImpuesto Tot2=new TotalImpuesto();
        Tot1.setCodigo("1");
        Tot1.setCodigoPorcentaje("3072");
        Tot1.setBaseImponible("1000.00");
        Tot1.setTarifa("5.00");
        Tot1.setValor("10");
        Tot2.setCodigo("2");
        Tot2.setCodigoPorcentaje("2");
        Tot2.setBaseImponible("2000.00");
        Tot2.setTarifa("12.00");
        Tot2.setValor("20");
      /*  List<TotalImpuesto> listaTotalImpuesto=new ArrayList<TotalImpuesto>();
        listaTotalImpuesto.add(Tot1);
        listaTotalImpuesto.add(Tot2);
        If1.setTotalImpuesto(listaTotalImpuesto);*/
        f1.setInfoNotaDebito(If1);

        Pago Pago1=new Pago();
        Pago1.setFormaPago("20");
        Pago1.setTotal("20");

        Pago Pago2=new Pago();
        Pago2.setFormaPago("20");
        Pago2.setTotal("20");

        List<Pago> listaPago=new ArrayList<Pago>();
        listaPago.add(Pago1);
        listaPago.add(Pago2);
      //  If1.setTotalImpuesto(listaTotalImpuesto);


        //////////////////////////////////////////

        //////////////////////////////////////////
        Motivo Mot1=new Motivo();
        Mot1.setRazon("Intereses de mora");
        Mot1.setValor("0.04 ");

        List<Motivo> listaDetalle=new ArrayList<Motivo>();
        listaDetalle.add(Mot1);
        f1.setMotivo(listaDetalle);
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
                    new Class[]{NotaDebito.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "notaDebito.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}