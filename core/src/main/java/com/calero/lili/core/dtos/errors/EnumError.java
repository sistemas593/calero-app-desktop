package com.calero.lili.core.dtos.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumError {

    CLIENT_NAME_NOT_FOUND("No existe el nombre del cliente"),
    TIPO_IDENTIFICACION_INCORRECTO("El tipo de identificacion debe ser R/C/P"),
    TIPO_IDENTIFICACION_NUMERO_IDENTIFICACION("El tipo de identificacion y numero de identificacion son datos relacionados"),
    RUC_INCORRECTO("El numero de RUC es incorrecto"),
    CEDULA_INCORRECTA("El numero de Cedula es incorrecto"),
    NUMERO_IDENTIFICACION_YA_EXISTE("El numero de identificacion ya existe"),
    DIRECCION_NOT_FOUND("No existe la direccion del cliente"),
    TIPO("El tipo Cliente/Proveedor es incorrecto"),

    TIPO_CLIENTE_NOT_FOUND("El tipo Cliente/Proveedor no debe ser nulo"),
    ES_TERCERO_ERROR("La columna que refiere a si es un CLIENTE/PROVEEDOR no tiene datos"),

    CODE_NOT_FOUND("No existe codigo para el Producto/Servicio/Gasto"),
    GRUPO_NOT_FOUND("No existe grupo para el Producto/Servicio/Gasto"),
    CODE_IS_PRESENT("El Producto/Servicio/Gasto ya se encuentra registrado"),
    CODIGO_BARRAS_NOT_FOUND("El codigo de barras no existe"),
    NAME_ITEM_NOT_FOUND("No existe nombre para Producto/Servicio/Gasto"),
    ORDENADOR_NOT_FOUND("No existe número de ordenador"),
    NOT_FOUND_IMPUESTO("No existe impuesto para el Producto/Servicio/Gasto"),
    NOT_FOUND_MARCAS("No existe marca para el Producto"),
    MARCAS_IS_PRESENT("La Marca ya se encuentra registrada"),
    NOT_FOUND_CATEGORIA("No existe categoria"),
    NOT_FOUND_NIVEL_CATEGORIA("No existe nivel de categoria"),
    CATEGORIA_IS_PRESENT("La categoria ya se encuentra registrada"),
    NOT_FOUND_MEDIDA("No existe medida"),
    MEDIDA_IS_PRESENT("La medida ya se encuentra registrada"),

    NOT_FOUND_CUENTA("No existe número de cuenta"),
    NOT_FOUND_NOMBRE_CUENTA("No existe nombre de cuenta"),
    CUENTA_IS_PRESENT("La empresa, ya tiene cuentas registradas"),


    TIPO_ASIENTO_NOT_FOUND("El tipo del asiento no se encuentra"),
    NUMERO_ASIENTO_NOT_FOUND("El número asiento no se encuentra"),
    FECHA_ASIENTO_NOT_FOUND("La fecha del asiento no se encuentra"),
    ITEM_ASIENTO_NOT_FOUND("El item del asiento no se encuentra"),
    CODIGO_CUENTA_ASIENTO_NOT_FOUND("El codigo de la cuenta no se encuentra"),
    CONCEPTO_ASIENTO_NOT_FOUND("El concepto  no se encuentra"),
    TIPO_DOCUMENTO_ASIENTO_NOT_FOUND("El tipo documento no se encuentra"),
    NUMERO_DOCUMENTO_ASIENTO_NOT_FOUND("El número documento no se encuentra"),
    FECHA_DOCUMENTO_ASIENTO_NOT_FOUND("La fecha del documento no se encuentra"),
    DETALLE_ASIENTO_NOT_FOUND("El detalle del documento no se encuentra"),
    DEBE_HABER_ASIENTO_NOT_FOUND("El valor del debe o del haber no se encuentra"),
    ASIENTO_IS_PRESENT("Ya existen asientos registrados para esta empresa en los rangos de fechas señalados"),

    ESTADO_CUENTA_IS_PRESENT("El estado de cuenta en el periodo indicado ya se encuetra registado"),
    FECHA_ESTADO_CUENTA_NOT_FOUND("La fecha del estado de cuenta no se encuentra"),
    NUMERO_ESTADO_CUENTA_NOT_FOUND("El número del estado de cuenta no se encuentra"),
    MOVIMIENTO_ESTADO_CUENTA_NOT_FOUND("El movimiento del estado de cuenta no se encuentra"),
    VALOR_ESTADO_CUENTA_NOT_FOUND("El valor del estado de cuenta no se encuentra"),

    NUMERO_IDENTIFICACION_NOT_FOUND("Número de identificación del trabajador no se encuentra"),
    TRABAJADOR_IS_PRESENT("El trabajador ya se encuentra registrado"),
    TRABAJADOR_NOMBRE_NOT_FOUND("Los nombres Y los apellidos del trabajador no se encuentran"),
    TRABAJADOR_TIPO_IDENTIFICACION_NOT_FOUND("El tipo de identificación del trabajador no se encuentra"),
    TRABAJADOR_DIRECCION_NOT_FOUND("La dirección del trabajador no se encuentra"),
    TRABAJADOR_NUMERO_CASA_NOT_FOUND("El número de casa del trabajador no se encuentra"),
    TRABAJADOR_TELEFONO_NOT_FOUND("El teléfono del trabajador no se encuentra"),
    TRABAJADOR_CORREO_NOT_FOUND("El correo del trabajador no se encuentra"),
    TRABAJADOR_CODIGO_SALARIO_NOT_FOUND("El código de salario del  trabajador no se encuentra"),
    TRABAJADOR_CODIGO_ESTAB_NOT_FOUND("El código de establecimiento del  trabajador no se encuentra"),
    TRABAJADOR_APL_CONVENIO_NOT_FOUND("Aplica convenido no se encuentra"),
    TRABAJADOR_TIPO_DISCAPACIDAD_NOT_FOUND("Tipo discapacidad no se encuentra"),
    TRABAJADOR_PORCENTAJE_DISCAPACIDAD_NOT_FOUND("Porcentaje de discapacidad no se encuentra"),
    TRABAJADOR_IDENTIFICACION_DISCAPACIDAD_NOT_FOUND("Identificación de discapacidad no se encuentra"),
    TRABAJADOR_TIPO_ID_DISCAPACIDAD_NOT_FOUND("Tipo identificación de discapacidad no se encuentra"),
    TRABAJADOR_BENEFICIO_PROV_GALAPAGOS_NOT_FOUND("Porcentaje de discapacidad no se encuentra"),
    TRABAJADOR_ENF_CASTROFICA_NOT_FOUND("Enfermedad catastrófica no se encuentra"),
    TRABAJADOR_FECHA_INGRESO_NOT_FOUND("La fecha de ingreso del trabajador no se encuentra"),
    TRABAJADOR_CANTON_NOT_FOUND("El cantón no se encuentra"),
    TRABAJADOR_PROVINCIA_NOT_FOUND("La provincia no se encuentra"),
    TRABAJADOR_PAIS_NOT_FOUND("El país no se encuentra"),
    TRABAJADOR_CODIGO_RESIDENCIA_NOT_FOUND("El codigo de residencia no se encuentra"),
    TRABAJADOR_ESTADO_NOT_FOUND("El estado del trabajador no se encuentra"),
    PAIS_NOT_FOUND("El nombre del país no se encuentra registrado"),
    CANTON_NOT_FOUND("El nombre del canton no se encuentra registrado"),
    PROVINCIA_NOT_FOUND("El nombre de la provincia no se encuentra registrado"),
    PLAN_CUENTA_NOT_FOUND("El plan de cuenta no se encuentra registrado"),
    DEBE_HABER_NO_CUADRAN("El debe y el haber del asiento no estan cuadrados"),
    SUCURSAL_NOT_FOUND("La sucursal no se encuentra"),


    FACTURA_EXISTS("La factura ya se encuentra registrada"),
    FACTURA_FECHA_EMISION_NOT_FOUND("La fecha de emisión no se encuentra"),
    FACTURA_INFORMACION_CLIENTE_NOT_FOUND("Los campos de RUC/Cédula, Tipo Id Cliente, Nombre Cliente, Dirección, Teléfono, Correo Electrónico, no se encuentran"),
    FACTURA_INFORMACION_DETALLE_ADICIONAL("Los campos de Detalle Adicional no se encuentran"),
    FACTURA_RELACIONADO_NOT_FOUND("El campo relacionado no se encuentra"),
    FACTURA_ERROR_FORMATO_COLUMNAS_INFO_DETALLES("Formato de columna incorrecto"),

    PARAMETRO_IDENTIFICACION_NOT_FOUND("La identificación del trabajador no se encuentra"),
    PARAMETRO_ANIO_Y_VALOR_NOT_FOUND("El año o el valor no se encuentran "),
    PARAMETRO_ANIO_Y_CARGA_NOT_FOUND("El año o el numero de cargas no se encuentran "),
    PARAMETRO_VALORES_NOT_CORRECT("Los valores ingresados no tiene el formato correcto"),

    PARAMETRO_TRABAJADOR_NOT_FOUND("Trabajador no encontrado"),
    PARAMETRO_RUBRO_NOT_FOUND("No existe el rubro indicado"),

    PARAMETRO_SALARIO_NOT_FOUND("El valor del salario no se encuentra"),
    PARAMETRO_DECIMO_TERCER_NOT_FOUND("El valor del decimo tercer no se encuentra"),
    PARAMETRO_DECIMO_CUARTO_NOT_FOUND("El valor del decimo cuarto no se encuentra"),
    PARAMETRO_FONDOS_RESERVA_NOT_FOUND("El valor de los fondos de reserva no se encuentra"),
    PARAMETRO_OTROS_INGRESOS_GRAVADOS_NOT_FOUND("El valor de otros ingresos gravados no se encuentra"),
    PARAMETRO_PARTICIPACION_UTILIDADES_NOT_FOUND("El valor de participación de utilidades no se encuentra"),
    PARAMETRO_OTROS_INGRESOS_NOT_FOUND("El valor de otros ingresos en relación de dependencia no se encuentra"),
    PARAMETRO_APORTE_IESS_NOT_FOUND("El valor de aporte al iees no se encuentra"),
    PARAMETRO_IMPUESTO_EMPLEADOR_NOT_FOUND("El valor de impuesto asumido por el empleador no se encuentra"),
    PARAMETRO_CELDA_NOT_FOUND("El valor de la celda no se encuentra"),


    PARAMETRO_SOBRE_SUELDOS_NOT_FOUND("El valor del sobre sueldos no se encuentra"),


    PARAMETRO_PERIODO_NOT_FOUND("El periodo no se encuentra"),
    PARAMETRO_PERIODO_GUARDADO("El periodo ya se encuentra guardado"),


    CAMPO_SUELDOS_Y_SALARIOS("El valor del campo Sueldos y salarios no se encuentra"),
    CAMPO_SOBRESUELDOS_COMISIONES_Y_OTRAS_REMUNERACIONES_GRAVADAS_DE_I_RENTA("El valor del campo Sobresueldos, comisiones y otras remuneraciones gravadas de I.Renta (materia gravada de seguridad social) no se encuentra"),
    CAMPO_OTROS_INGRESOS_GRAVADOS_DE_I_RENTA("El valor del campo Otros ingresos gravados de I.Renta (maeteria NO gravada de seguridad social) no se encuentra"),
    CAMPO_DECIMO_TERCER_SUELDO("El valor del campo Décimo tercer sueldo no se encuentra"),
    CAMPO_DECIMO_CUARTO_SUELDO("El valor del campo Décimo cuarto sueldo no se encuentra"),
    CAMPO_FONDOS_DE_RESERVA("El valor del campo Fondos de Reserva no se encuentra"),
    CAMPO_COMPENSACION_ECONOMICA_SALARIO_DIGNO("El valor del campo Compensación económica Salario Digno no se encuentra"),
    CAMPO_PARTICIPACION_UTILIDADES("El valor del campo Participación utilidades no se encuentra"),
    CAMPO_OTROS_INGRESOS_EN_RELACION_DE_DEPENDENCIA_QUE_NO_CONSTITUYEN_RENTA_GRAVADA("El valor del campo Otros ingresos en relación de dependencia que no constituyen renta gravada, Dshaucui no se encuentra"),
    CAMPO_APORTE_PERSONAL("El valor del campo Aporte personal (únicamente pagado por el empleado) no se encuentra"),
    CAMPO_DEDUCIBLE_VIVIENDA("El valor del campo Deducible vivienda (Anual) no se encuentra"),
    CAMPO_DEDUCIBLE_SALUD("El valor del campo Deducible salud (Anual) no se encuentra"),
    CAMPO_DEDUCIBLE_EDUCACION("El valor del campo Deducible educación  (Anual) no se encuentra"),
    CAMPO_DEDUCIBLE_ALIMENTACION("El valor del campo Deducible alimentación   (Anual) no se encuentra"),
    CAMPO_DEDUCIBLE_VESTIMENTA("El valor del campo Deducible vestimenta   (Anual) no se encuentra"),
    CAMPO_DEDUCIBLE_TURISMO("El valor del campo Deducible turismo (Anual) no se encuentra"),
    CAMPO_REBAJAS_ESPECIALES_DISCAPACITADOS("El valor del campo Rebajas especiales Discapacitados (Anual) no se encuentra"),
    CAMPO_REBAJAS_ESPECIALES_TERCERA_EDAD("El valor del campo Rebajas especiales Tercera Edad (Anual) no se encuentra"),
    CAMPO_IMPUESTO_A_LA_RENTA_ASUMIDO_POR_EL_EMPLEADOR("El valor del campo Impuesto a  la renta asumido por el empleador no se encuentra"),
    CAMPO_INGRESOS_GRAVADOS_OTROS_EMPLEADORES("El valor del campo Ingresos gravados otros empleadores(Anual) no se encuentra"),
    CAMPO_REBAJAS_OTROS_EMPLEADORES_IESS("El valor del campo Rebajas otros empleadores IESS (Anual) no se encuentra"),
    CAMPO_VALOR_RETENIDO("El valor del campo Valor retenido no se encuentra"),
    CAMPO_RETENIDO_Y_ASUMIDO_POR_OTROS_EMPLEADORES("El valor del campo Retenido y asumido por otros empleadores no se encuentra"),
    CAMPO_BASE_IMPONIBLE("El valor del campo Base imponible ( Igual a la suma de todos los formularios 103) no se encuentra"),

    DETALLES_CAMPOS_NO_FOUND("No existen detalles de cabecera a guardar"),

    CODIGO_PRINCIPAL_NOT_VALID_CARACTER("El codigo principal contiene caracteres inválidos"),
    CODIGO_BARRAS_NOT_VALID_CARACTER("El codigo de barras contiene caracteres inválidos"),
    NOMBRE_NOT_VALID_CARACTER("El nombre contiene caracteres inválidos"),
    DETALLE_NOT_VALID_CARACTER("Un campo en los detalles contiene caracteres inválidos "),

    PROVINCIA_NOT_EXIST("El codigo de la provincia proporcionado no existe"),
    CANTON_NOT_EXIST("El codigo del canton proporcionado no existe"),
    PARROQUIA_NOT_EXIST("El codigo de la parroquia proporcionado no existe"),

    ;
    private String description;
}