package com.calero.lili.core.comprobantes.message;


public abstract class MensajeComprobante {


    public MensajeComprobante() {
    }

    public static final String NOT_ERROR = "";
    public static final String ERR_CODIFICACION_BASE64 = "Error al decodificar la cadena Base64";
    public static final String ERR_NO_AUTORIZADO = "No es un archivo xml autorizado";
    public static final String ERR_CLAVE_ACCESO_INCORRECTA = "Clave de acceso incorrecta";
    public static final String ERR_DOCUMENTO_EXISTE = "El documento ya existe";
    public static final String ERR_LEER_DOCUMENTO_INTERNO = "No se pudo leer el comprobante interno";

    public static final String ERR_GENERAL_DOCUMENTO = "Error al guardar documento";
    public static final String ERR_NOT_FACTURA = "El documento no corresponde a una factura";

    public static final String ERR_NO_CORRESPONDE_EMPRESA = "La empresa del documento no corresponde con la empresa seleccionada";

    public static final String ERR_NO_EXISTE_EMPRESA = "La empresa seleccionada no existe";

    public static final String ERR_ES_UNA_LIQUIDACION = "El documento hace referencia a una liquidación";
    public static final String ERR_NO_ES_RETENCION = "El documento no corresponde a un retención";

    public static final String ERR_EL_DOCUMENTO_NO_CORRESPONDE = "El documento ingresado no corresponde a los documentos permitidos en esta sección";


}
