package com.calero.lili.core.apiSitac.dtos.adMailsListaNegra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AdMailsListaNegraListCreationRequestDto {

    private List<Email> emails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Email {
        private String email;
    }

}
