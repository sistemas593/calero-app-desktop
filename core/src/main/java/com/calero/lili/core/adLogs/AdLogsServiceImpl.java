package com.calero.lili.core.adLogs;

import com.calero.lili.core.adLogs.builder.AdLogsBuilder;
import com.calero.lili.core.adLogs.dto.AdLogsRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdLogsServiceImpl {

    private final AdLogsBuilder adLogsBuilder;
    private final AdLogsRepository adLogsRepository;

    public void saveLog(AdLogsRequestDto model, String mensajes) {
        adLogsRepository.save(adLogsBuilder.builderLog(model, mensajes));
    }

}
