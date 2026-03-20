package com.calero.lili.core.modAdminlistaNegra;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final EmailProcesarRechazados emailProcesarListaNegra;
    //@Scheduled(fixedRate = 6000000) // 1000 1 segundos // 600000 10 minutos
    @Scheduled(cron = "0 0 12,20 * * *", zone = "America/Guayaquil")
    public void performTask() {
        log.info("Procesando correos rechazados");
        emailProcesarListaNegra.procesarRechazados();
    }
}