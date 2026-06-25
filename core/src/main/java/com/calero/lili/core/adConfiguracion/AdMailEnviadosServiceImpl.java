package com.calero.lili.core.adConfiguracion;

import com.calero.lili.core.adConfiguracion.builder.AdMailEnviadosBuilder;
import com.calero.lili.core.adConfiguracion.dto.AdMailEnviadosResponseDto;
import com.calero.lili.core.adConfiguracion.dto.FilterMailEnviadosDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdMailEnviadosServiceImpl {


    private final AdMailsEnviadosRepository adMailsEnviadosRepository;
    private final AdMailEnviadosBuilder adMailEnviadosBuilder;


    public List<AdMailEnviadosResponseDto> findAll(FilterMailEnviadosDto filter) {

        return adMailsEnviadosRepository.findAll(filter.getClave1(), filter.getCodigoDocumento(),
                        filter.getSerie(), filter.getSecuencial(), filter.getCorreo(),
                        filter.getFechaInicial(), filter.getFechaFinal())
                .stream()
                .map(adMailEnviadosBuilder::builderResponse)
                .toList();
    }


}
