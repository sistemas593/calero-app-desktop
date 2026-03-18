package com.calero.lili.core.comprobantes.objetosXml.notaCredito;

import com.calero.lili.core.comprobantes.objetosXml.factura.CampoAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.DetAdicional;
import com.calero.lili.core.comprobantes.objetosXml.factura.Impuesto;
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
@XmlType(propOrder = {"infoTributaria", "infoNotaCredito", "detalle", "campoAdicional"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotaCredito {
    private String id;
    private String version;
    private InfoTributaria infoTributaria;
    private InfoNotaCredito infoNotaCredito;
    private List<Detalle> detalle;
    private List<CampoAdicional> campoAdicional;

    @XmlElementWrapper(name = "detalles") // envoltorio
    public List<Detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Detalle> detalle) {
        this.detalle = detalle;
    }

    @XmlElementWrapper(name = "infoAdicional") // envoltorio
    public List<CampoAdicional> getCampoAdicional() {
        return campoAdicional;
    }

    public void setCampoAdicional(List<CampoAdicional> campoAdicional) {
        this.campoAdicional = campoAdicional;
    }

    public InfoNotaCredito getInfoNotaCredito() {
        return infoNotaCredito;
    }

    public void setInfoNotaCredito(InfoNotaCredito infoNotaCredito) {
        this.infoNotaCredito = infoNotaCredito;
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
        NotaCredito f1= new NotaCredito();
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
        InfoNotaCredito If1=new InfoNotaCredito();
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
        If1.setValorModificacion("201.60");
        If1.setMoneda("DOLAR");
        If1.setMotivo("duplicidad en factura ");

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
       /* List<TotalImpuesto> listaTotalImpuesto=new ArrayList<TotalImpuesto>();
        listaTotalImpuesto.add(Tot1);
        listaTotalImpuesto.add(Tot2);
        If1.setTotalImpuesto(listaTotalImpuesto);
        f1.setInfoNotaCredito(If1);

        If1.setTotalImpuesto(listaTotalImpuesto);*/

        //////////////////////////////////////////

        //////////////////////////////////////////
        Detalle Det1=new Detalle();
        Det1.setCodigoInterno("028");
        Det1.setCodigoAdicional("1234D56789");
        Det1.setDescripcion("CAMIONETA 4X4 DIESEL 3.7");
        Det1.setCantidad("10.00");
        Det1.setPrecioUnitario("30000.00");
        Det1.setDescuento("5000.00");
        Det1.setPrecioTotalSinImpuesto("295000.00");

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
        Det1.setDetAdicional(listaDetalleAdicional);

        //////////////////////////////////////////
        Impuesto impu1= new Impuesto();
        impu1.setCodigo("3");
        impu1.setCodigoPorcentaje("3072");
        impu1.setTarifa("5");
        impu1.setBaseImponible("295000.00");
        impu1.setValor("14750.00");

        List<Impuesto> listaImpuesto=new ArrayList<Impuesto>();
        listaImpuesto.add(impu1);
        Det1.setImpuesto(listaImpuesto);


        List<Detalle> listaDetalle=new ArrayList<Detalle>();
        listaDetalle.add(Det1);
        f1.setDetalle(listaDetalle);
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
                    new Class[]{NotaCredito.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "notaCredito.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
