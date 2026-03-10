package com.calero.lili.core.builder;

import com.calero.lili.core.dtos.ResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ResponseApiBuilder {

    public ResponseDto builderResponse(String response) {
        return ResponseDto.builder()
                .id(response)
                .build();
    }

}
