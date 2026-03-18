package com.calero.lili.core.utils.validaciones;

public class ValidarDatos {
    public Boolean validaSerie(String serie){
        if(serie.length()!=6){
            return false;
        }

        return true;
    }
}
