package com.calero.lili.core.errors.exceptions;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<String> errors;

}