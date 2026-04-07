package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales;

import com.calero.lili.core.dtos.FilterDto;
import com.calero.lili.core.errors.exceptions.GeneralException;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.builder.TbParaisoFiscalBuilder;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalRequestDto;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TbParaisoFiscalServiceImpl {

    private final TbParaisoFiscalRepository tbParaisoFiscalRepository;
    private final TbParaisoFiscalBuilder tbParaisoFiscalBuilder;

    public TbParaisoFiscalResponseDto create(String username, TbParaisoFiscalRequestDto model) {
        TbParaisoFiscalEntity paraisoFiscal = tbParaisoFiscalBuilder.builder(model);
        paraisoFiscal.setCreatedDate(LocalDateTime.now());
        paraisoFiscal.setCreatedBy(username);
        return tbParaisoFiscalBuilder.builderResponse(tbParaisoFiscalRepository.save(paraisoFiscal));
    }

    public TbParaisoFiscalResponseDto update(String username, String codigo, TbParaisoFiscalRequestDto model) {

        TbParaisoFiscalEntity item = tbParaisoFiscalRepository.findByCodigo(codigo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe paraiso fiscal con el código {0}", codigo)));

        TbParaisoFiscalEntity paraisoFiscal = tbParaisoFiscalBuilder.builderUpdate(item, model);
        paraisoFiscal.setModifiedDate(LocalDateTime.now());
        paraisoFiscal.setModifiedBy(username);
        return tbParaisoFiscalBuilder.builderResponse(tbParaisoFiscalRepository.save(paraisoFiscal));
    }


    public void delete(String username, String codigo) {
        TbParaisoFiscalEntity item = tbParaisoFiscalRepository.findByCodigo(codigo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe paraiso fiscal con el código {0}", codigo)));

        item.setDelete(Boolean.TRUE);
        item.setDeletedBy(username);
        item.setDeletedDate(LocalDateTime.now());

        tbParaisoFiscalRepository.save(item);
    }


    public TbParaisoFiscalResponseDto findById(String codigo) {
        return tbParaisoFiscalBuilder.builderResponse(tbParaisoFiscalRepository.findByCodigo(codigo)
                .orElseThrow(() -> new GeneralException(MessageFormat.format("No existe paraiso fiscal con el código {0}", codigo))));
    }

    public List<TbParaisoFiscalResponseDto> findAll(FilterDto filters) {
        return tbParaisoFiscalRepository.findAll(filters.getFilter(), (filters.getFilter() != null) ? filters.getFilter() : "")
                .stream()
                .map(tbParaisoFiscalBuilder::builderResponse)
                .toList();
    }

}
