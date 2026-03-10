package com.calero.lili.api.modImpuestosAnexos;

public class ObjectToXml {

    public static void main(String args[]) {

       /* //////////////////////////////////////////
        Iva f1= new Iva();
        f1.setAnio(2025);
        f1.setMes(11);
        //////////////////////////////////////////
        DetalleCompras If1=new DetalleCompras();
        If1.setFechaEmision("01/12/2013");
        If1.setCodSustento("01");

        List<DetalleCompras> detalleCompras = new ArrayList<>();
        detalleCompras.add(If1);
        f1.setDetalleCompras(detalleCompras);

        Pago Pago1=new Pago();
        Pago1.setFormaPago("20");
        Pago1.setTotal("20");

        Pago Pago2=new Pago();
        Pago2.setFormaPago("20");
        Pago2.setTotal("20");

        List<Pago> listaPago=new ArrayList<Pago>();
        listaPago.add(Pago1);
        listaPago.add(Pago2);
        If1.setPago(listaPago);

        //////////////////////////////////////////


        OutputStreamWriter out = null;
        try {
            JAXBContext context = JAXBContext.newInstance(
                    new Class[]{Iva.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.setProperty("jaxb.formatted.output",
                    Boolean.valueOf(true));
            out = new OutputStreamWriter(new FileOutputStream(
                    "Archivo.xml"), "UTF-8");
            marshaller.marshal(f1, out);
            out.close();
            System.out.println("XML Generado con exito a partir de objeto: xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }
}
