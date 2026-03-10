package com.calero.lili.api.comprobantes.objetosXml.guiaRemision;

import com.calero.lili.api.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.api.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.api.comprobantes.objetosXml.factura.Impuesto;
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
@XmlType(propOrder = {"infoTributaria", "infoGuiaRemision", "destinatario", "campoAdicional"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuiaRemision {
    private String id;
    private String version;
    private InfoTributaria infoTributaria;
    private InfoGuiaRemision infoGuiaRemision;
    private List<Destinatario> destinatario;
    private List<CampoAdicional> campoAdicional;

    @XmlElementWrapper(name = "destinatarios") // envoltorio
    public List<Destinatario> getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(List<Destinatario> destinatario) {
        this.destinatario = destinatario;
    }

    @XmlElementWrapper(name = "infoAdicional") // envoltorio
    public List<CampoAdicional> getCampoAdicional() {
        return campoAdicional;
    }

    public void setCampoAdicional(List<CampoAdicional> campoAdicional) {
        this.campoAdicional = campoAdicional;
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

    public InfoGuiaRemision getInfoGuiaRemision() {
        return infoGuiaRemision;
    }

    public void setInfoGuiaRemision(InfoGuiaRemision infoGuiaRemision) {
        this.infoGuiaRemision = infoGuiaRemision;
    }

    public static void main(String args[]){

        //////////////////////////////////////////
        GuiaRemision f1= new GuiaRemision();
        //////////////////////////////////////////

        //////////////////////////////////////////
        f1.setId("comprobante");
        f1.setVersion("1.0");
        InfoTributaria It=new InfoTributaria();
        It.setAmbiente("1");
        It.setClaveAcceso("2110201101179214673900110020010000000011234567813");
        It.setCodDoc("01");
        It.setDirMatriz("Pinta y Amazonas");
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
        InfoGuiaRemision If1=new InfoGuiaRemision();

        If1.setDirEstablecimiento("Pamela Egas");
        If1.setDirPartida("La Pinta E4-432 y Av. Amazonas");
        If1.setRazonSocialTransportista("JAVIER CALERO");
        If1.setTipoIdentificacionTransportista("04");
        If1.setRucTransportista("1212121212001");
        If1.setObligadoContabilidad("NO");
        If1.setFechaIniTransporte("28/09/2018");
        If1.setFechaFinTransporte("28/09/2018");
        If1.setPlaca("NA");

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

        //List<TotalImpuesto> listaTotalImpuesto=new ArrayList<TotalImpuesto>();
        //listaTotalImpuesto.add(Tot1);
        //listaTotalImpuesto.add(Tot2);
        //If1.setTotalImpuesto(listaTotalImpuesto);

        f1.setInfoGuiaRemision(If1);

        Pago Pago1=new Pago();
        Pago1.setFormaPago("20");
        Pago1.setTotal("20");

        Pago Pago2=new Pago();
        Pago2.setFormaPago("20");
        Pago2.setTotal("20");

        List<Pago> listaPago=new ArrayList<Pago>();
        listaPago.add(Pago1);
        listaPago.add(Pago2);
        //If1.setTotalImpuesto(listaTotalImpuesto);
        //If1.setPago(listaPago);

        //////////////////////////////////////////

        //////////////////////////////////////////
        Destinatario Det1=new Destinatario();
        //Det1.setCodigoPrincipal("125BJC-01");
        Det1.setIdentificacionDestinatario("1717740441001");
        Det1.setRazonSocialDestinatario("PAMELA &amp; EGAS PICO &lt; &gt; ");
        Det1.setDirDestinatario("La Pinta E4 - 432 y Av. Amazonas");
        Det1.setMotivoTraslado("Venta");
        Det1.setRuta("NORTE");

        //////////////////////////////////////////
        Detalle detalle1= new Detalle();
        detalle1.setCodigoInterno("001");
        detalle1.setDescripcion("Licencia Software SITAC");
        detalle1.setCantidad("1.00");

        List<Detalle> listaDetalle=new ArrayList<Detalle>();
        listaDetalle.add(detalle1);
        Det1.setDetalle(listaDetalle);

        //////////////////////////////////////////
        DetAdicional dadic1= new DetAdicional();
        dadic1.setNombre("Marca Chevrolet");
        dadic1.setValor("Chevrolet");
        DetAdicional dadic2= new DetAdicional();
        dadic2.setNombre("Modelo");
        dadic2.setValor("2012");
        DetAdicional dadic3= new DetAdicional();
        dadic3.setNombre("Chasis");
        dadic3.setValor("8LDETA03V20003289");

        List<DetAdicional> listaDetalleAdicional=new ArrayList<DetAdicional>();
        listaDetalleAdicional.add(dadic1);
        listaDetalleAdicional.add(dadic2);
        listaDetalleAdicional.add(dadic3);
//        Det1.setDetAdicional(listaDetalleAdicional);

        //////////////////////////////////////////
        Impuesto impu1= new Impuesto();
        impu1.setCodigo("3");
        impu1.setCodigoPorcentaje("3072");
        impu1.setTarifa("5");
        impu1.setBaseImponible("295000.00");
        impu1.setValor("14750.00");

        //List<Impuesto> listaImpuesto=new ArrayList<Impuesto>();
        //listaImpuesto.add(impu1);
        //Det1.setImpuesto(listaImpuesto);


        List<Destinatario> listaDestinatario=new ArrayList<Destinatario>();
        listaDestinatario.add(Det1);
        f1.setDestinatario(listaDestinatario);


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
                    new Class[]{GuiaRemision.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "GuiaRemision.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
