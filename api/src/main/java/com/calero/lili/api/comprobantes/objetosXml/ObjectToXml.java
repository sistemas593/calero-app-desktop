package com.calero.lili.api.comprobantes.objetosXml;

public class ObjectToXml {
    public static void main(String args[]){

    /*    //////////////////////////////////////////
        Factura f1= new Factura();
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
        InfoFactura If1=new InfoFactura();
        If1.setFechaEmision("01/12/2013");
        If1.setDirEstablecimiento("Pamela Egas");
        If1.setContribuyenteEspecial("4521");
        If1.setObligadoContabilidad("NO");
        If1.setComercioExterior("EXPORTADOR");
        If1.setIncoTermFactura("FOB");
        If1.setLugarIncoTerm("ECUADOR");
        If1.setPaisOrigen("593");
        If1.setPuertoEmbarque("QUITO");
        If1.setPuertoDestino("USA");
        If1.setPaisDestino("110");
        If1.setPaisAdquisicion("593");
        If1.setTipoIdentificacionComprador("04");
        If1.setGuiaRemision("001-001-000000001");
        If1.setRazonSocialComprador("PRUEBAS SERVICIO DERENTAS INTERNAS");
        If1.setIdentificacionComprador("1717740441001");
        If1.setTotalSinImpuestos("295000.00");
        If1.setTotalDescuento("5000.00");
        If1.setPropina("100.98");
        If1.setFleteInternacional("0.00");
        If1.setSeguroInternacional("0.00");
        If1.setGastosAduaneros("0.00");
        If1.setGastosTransporteOtros("0.00");
        If1.setImporteTotal("341020.98");
        If1.setMoneda("DOLAR");

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
        List<TotalImpuesto> listaTotalImpuesto=new ArrayList<TotalImpuesto>();
        listaTotalImpuesto.add(Tot1);
        listaTotalImpuesto.add(Tot2);
        If1.setTotalImpuesto(listaTotalImpuesto);
        f1.setInfoFactura(If1);

        Pago Pago1=new Pago();
        Pago1.setFormaPago("20");
        Pago1.setTotal("20");

        Pago Pago2=new Pago();
        Pago2.setFormaPago("20");
        Pago2.setTotal("20");

        List<Pago> listaPago=new ArrayList<Pago>();
        listaPago.add(Pago1);
        listaPago.add(Pago2);
        If1.setTotalImpuesto(listaTotalImpuesto);
        If1.setPago(listaPago);

        //////////////////////////////////////////

        //////////////////////////////////////////
        Detalle Det1=new Detalle();
        Det1.setCodigoPrincipal("125BJC-01");
        Det1.setCodigoAuxiliar("1234D56789");
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
                    new Class[]{Factura.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "Factura.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
            System.out.println("XML Generado con exito a partir de objeto: Factura.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }
}
