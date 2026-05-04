package com.calero.lili.core.utils.validaciones;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

@Component
public class ValidarIdentificacion {
    public void validarCedula(String numero) throws Exception {
            String respuesta=validarLongitudSoloNumeros(numero, 10);
            if (!respuesta.isEmpty()){
                throw new Exception(respuesta);
            }
            respuesta=validarCodigoProvincia(numero.substring(0, 2));
            if (!respuesta.isEmpty()){
                throw new Exception(respuesta);
            }
//            respuesta=validarTercerDigito(String.valueOf(numero.charAt(2)), TipoIdentificacion.getTipoCedula());
//            if (!respuesta.isEmpty()){
//                throw new Exception(respuesta);
//            }
            respuesta=algoritmoModulo10(numero, Integer.parseInt(String.valueOf(numero.charAt(9))));
            if (!respuesta.isEmpty()){
                throw new Exception(respuesta);
            }
    }

    public void validarRuc(String numero) throws Exception {
            String respuesta="";
            respuesta=validarLongitudSoloNumeros(numero, 13);
            if (!respuesta.isEmpty()){
                throw new Exception(respuesta);
            }
        respuesta=validarCodigoProvincia(numero.substring(0, 2));
        if (!respuesta.isEmpty()){
            throw new Exception(respuesta);
        }
//        respuesta=validarTercerDigito(String.valueOf(numero.charAt(2)), TipoIdentificacion.getTipoRucNatural());
//        if (!respuesta.isEmpty()){
//            throw new Exception(respuesta);
//        }
        respuesta=validarCodigoEstablecimiento(numero.substring(10, 13));
        if (!respuesta.isEmpty()){
            throw new Exception(respuesta);
        }
//        respuesta=algoritmoModulo10(numero.substring(0, 9), Integer.parseInt(String.valueOf(numero.charAt(9))));
//        if (!respuesta.isEmpty()){
//            throw new Exception(respuesta);
//        }
    }

//    public boolean validarRucSociedadPrivada(String numero) throws Exception {
//
//        // validaciones
//        try {
//            validarLongitudSoloNumeros(numero, 13);
//            validarCodigoProvincia(numero.substring(0, 2));
//            validarTercerDigito(String.valueOf(numero.charAt(2)), TipoIdentificacion.getRucPrivada());
//            validarCodigoEstablecimiento(numero.substring(10, 13));
//            algoritmoModulo11(numero.substring(0, 9), Integer.parseInt(String.valueOf(numero.charAt(9))), TipoIdentificacion.getRucPrivada());
//        } catch (Exception e) {
//            return false;
//        }
//
//        return true;
//    }

    protected String validarLongitudSoloNumeros(String numero, int caracteres) {
        String respuesta="";
        if (StringUtils.isEmpty(numero)) {
            //throw new Exception("Valor no puede estar vacio");
            respuesta="Valor no puede estar vacio";
        }

        if (!NumberUtils.isDigits(numero)) {
            //throw new Exception("Valor ingresado solo puede tener dígitos");
            respuesta="Valor ingresado solo puede tener dígitos";
        }

        if (numero.length() != caracteres) {
            //throw new Exception("Valor ingresado debe tener " + caracteres + " caracteres");
            respuesta="Valor ingresado debe tener " + caracteres + " caracteres";
        }

        return respuesta;
    }

    protected String validarCodigoProvincia(String numero) {
        String respuesta="";

        int valor = Integer.parseInt(numero);

        if ((valor < 1 || valor > 24) && !numero.startsWith("30")) {
            //throw new Exception("Codigo de Provincia (dos primeros dígitos) no deben ser mayor a 24 ni menores a 0");
            respuesta="Codigo de Provincia (dos primeros dígitos) no deben ser mayor a 24 ni menores a 1";
        }
        return respuesta;
    }

    protected String validarTercerDigito(String numero, Integer tipo) throws Exception {
        String respuesta ="";
        switch (tipo) {
            case 1:
            case 2:

                if (Integer.parseInt(numero) < 0 || Integer.parseInt(numero) > 5) {
                    //throw new Exception("Tercer dígito debe ser mayor o igual a 0 y menor a 6 para cédulas y RUC de persona natural ... permitidos de 0 a 5");
                    respuesta ="Tercer dígito debe ser mayor o igual a 0 y menor a 6 para cédulas y RUC de persona natural ... permitidos de 0 a 5";
                }
                break;
            case 3:
                if (Integer.parseInt(numero) != 9) {
                    //throw new Exception("Tercer dígito debe ser igual a 9 para sociedades privadas");
                    respuesta="Tercer dígito debe ser igual a 9 para sociedades privadas";
                }
                break;

            case 4:
                if (Integer.parseInt(numero) != 6) {
                    //throw new Exception("Tercer dígito debe ser igual a 6 para sociedades públicas");
                    respuesta="Tercer dígito debe ser igual a 6 para sociedades públicas";
                }
                break;
            default:
                //throw new Exception("TipoIngreso de Identificacion no existe.");
                respuesta="Error 3er digito.";
        }

        return respuesta;
    }

    protected String algoritmoModulo10(String digitosIniciales, int digitoVerificador) throws Exception {
        String respuesta="";
        Integer[] arrayCoeficientes = new Integer[]{2, 1, 2, 1, 2, 1, 2, 1, 2};

        Integer[] digitosInicialesTMP = new Integer[digitosIniciales.length()];
        int indice = 0;
        for (char valorPosicion : digitosIniciales.toCharArray()) {
            digitosInicialesTMP[indice] = NumberUtils.createInteger(String.valueOf(valorPosicion));
            indice++;
        }

        int total = 0;
        int key = 0;

        for (Integer valorPosicion : digitosInicialesTMP) {
            if (key < arrayCoeficientes.length) {
                valorPosicion = (digitosInicialesTMP[key] * arrayCoeficientes[key]);

                if (valorPosicion >= 10) {
                    char[] valorPosicionSplit = String.valueOf(valorPosicion).toCharArray();
                    valorPosicion = (Integer.parseInt(String.valueOf(valorPosicionSplit[0]))) + (Integer.parseInt(String.valueOf(valorPosicionSplit[1])));

                }
                total = total + valorPosicion;
            }

            key++;
        }
        int residuo = total % 10;
        int resultado;

        if (residuo == 0) {
            resultado = 0;
        } else {
            resultado = 10 - residuo;
        }

        if (resultado != digitoVerificador) {
            //throw new Exception("Dígitos iniciales no validan contra Dígito Idenficador");
            respuesta="Dígitos iniciales no validan contra Dígito Idenficador";
        }

        return respuesta;
    }

    protected String validarCodigoEstablecimiento(String numero) {
        String respuesta="";
        if (!numero.equals("001")) {
            //throw new Exception("Código de establecimiento no puede ser 0");
            respuesta="Código de establecimiento incorrecto debe ser 001";
        }
        return respuesta;
    }

//    protected boolean algoritmoModulo11(String digitosIniciales, int digitoVerificador, Integer tipo) throws Exception {
//        List<Integer> arrayCoeficientes = null;
//
//        switch (tipo) {
//
//            case 3:
//                arrayCoeficientes = Arrays.asList(4, 3, 2, 7, 6, 5, 4, 3, 2);
//                break;
//            case 4:
//                arrayCoeficientes = Arrays.asList(3, 2, 7, 6, 5, 4, 3, 2);
//                break;
//            default:
//                throw new Exception("TipoIngreso de Identificacion no existe.");
//        }
//
//        List<Integer> digitosInicialesTMP = IntStream.range(0, digitosIniciales.length()).mapToObj(
//                        i -> NumberUtils.createInteger(String.valueOf(digitosIniciales.charAt(i)))).
//                collect(Collectors.toCollection(() -> new ArrayList<>(digitosIniciales.length())));
//
//
//        AtomicInteger consolidadodMultiplicacionIndiceConeficiente = new AtomicInteger();
//        List<Integer> finalArrayCoeficientes = arrayCoeficientes;
//        IntStream.range(0, arrayCoeficientes.size()).map(x -> (digitosInicialesTMP.get(x) * finalArrayCoeficientes.get(x))).
//                forEach(consolidadodMultiplicacionIndiceConeficiente::addAndGet);
//
//
//        int residuo = consolidadodMultiplicacionIndiceConeficiente.get() % 11;
//        int resultado;
//
//        if (residuo == 0) {
//            resultado = 0;
//        } else {
//            resultado = (11 - residuo);
//        }
//
//        if (resultado != digitoVerificador) {
//            throw new Exception("Dígitos iniciales no validan contra Dígito Idenficador");
//        }
//
//        return true;
//    }

}
