package com.calero.lili.core.modLocalidades.modParroquias.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FilterRequestDto {

    private String codigoCanton;
    private String filter;
}
