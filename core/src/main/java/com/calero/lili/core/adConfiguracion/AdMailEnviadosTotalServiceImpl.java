package com.calero.lili.core.adConfiguracion;

import com.calero.lili.core.adConfiguracion.builder.AdMailBuilderTotalBuilder;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosTotalResponseDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosTotalesDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdMailEnviadosTotalServiceImpl {


    private final AdMailsEnviadosTotalRepository adMailsEnviadosTotalRepository;
    private final AdMailBuilderTotalBuilder adMailBuilderTotalBuilder;

    public List<AdMailEnviadosTotalResponseDto> findAll(FilterMailEnviadosTotalesDto filter) {

        return adMailsEnviadosTotalRepository.findAll(filter.getClave1(), filter.getPeriodo())
                .stream()
                .map(adMailBuilderTotalBuilder::builderResponse)
                .toList();
    }


}
