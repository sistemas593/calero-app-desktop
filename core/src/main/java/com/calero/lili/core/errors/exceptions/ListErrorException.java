package com.calero.lili.core.errors.exceptions;

import lombok.Getter;

import java.util.List;
@Getter
public class ListErrorException extends RuntimeException {

    private List<String> errors;
    public ListErrorException(List<String> errors) {
        this.errors=errors;
    }

}
