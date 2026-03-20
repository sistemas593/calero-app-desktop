package com.calero.lili.core.modAdminlistaNegra;

import com.calero.lili.core.modAdminlistaNegra.dto.MailBlackResponseDto;
import com.calero.lili.core.modAdminlistaNegra.dto.MailsBlackDto;
import com.calero.lili.core.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AdMailsListaNegraBuilder {

    public List<AdMailListaNegraEntity> builderList(List<MailsBlackDto> model) {
        return model.stream()
                .map(this::builder)
                .toList();
    }

    private AdMailListaNegraEntity builder(MailsBlackDto mailsBlackDto) {
        return AdMailListaNegraEntity.builder()
                .email(mailsBlackDto.getRecipient())
                .fecha(LocalDate.now())
                .build();
    }

    public MailBlackResponseDto builderResponse(AdMailListaNegraEntity model) {
        return MailBlackResponseDto.builder()
                .email(model.getEmail())
                .motivo(model.getMotivo())
                .fecha(DateUtils.toString(model.getFecha()))
                .build();
    }

}
