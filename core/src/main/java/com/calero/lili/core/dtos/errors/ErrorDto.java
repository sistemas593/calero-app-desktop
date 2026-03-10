package com.calero.lili.core.dtos.errors;

import com.calero.lili.core.utils.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Setter

public class ErrorDto {

    private String timestamp;
    private String resource;
    private List<String> messages;


    public String getTimestamp() {
        return DateUtils.toString(LocalDate.now());
    }


}
