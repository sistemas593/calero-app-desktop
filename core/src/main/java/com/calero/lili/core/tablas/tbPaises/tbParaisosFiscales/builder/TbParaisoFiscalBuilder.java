package com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.builder;

import com.calero.lili.core.tablas.tbPaises.TbPaisEntity;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.TbParaisoFiscalEntity;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalRequestDto;
import com.calero.lili.core.tablas.tbPaises.tbParaisosFiscales.dto.TbParaisoFiscalResponseDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TbParaisoFiscalBuilder {


    public TbParaisoFiscalEntity builder(TbParaisoFiscalRequestDto model) {
        return TbParaisoFiscalEntity.builder()
                .codigo(model.getCodigo())
                .paraisoFiscal(model.getParaisoFiscal())
                .pais(builderPais(model.getCodigoPais()))
                .build();
    }

    public TbParaisoFiscalEntity builderUpdate(TbParaisoFiscalEntity item, TbParaisoFiscalRequestDto model) {
        return TbParaisoFiscalEntity.builder()
                .codigo(item.getCodigo())
                .paraisoFiscal(model.getParaisoFiscal())
                .pais(builderPais(model.getCodigoPais()))
                .build();
    }


    public TbParaisoFiscalResponseDto builderResponse(TbParaisoFiscalEntity model) {
        return TbParaisoFiscalResponseDto.builder()
                .codigo(model.getCodigo())
                .paraisoFiscal(model.getParaisoFiscal())
                .pais(builderPaisResponse(model.getPais()))
                .build();
    }

    private TbParaisoFiscalResponseDto.Pais builderPaisResponse(TbPaisEntity pais) {
        if (Objects.isNull(pais)) return null;
        return TbParaisoFiscalResponseDto.Pais.builder()
                .codigoPais(pais.getCodigoPais())
                .pais(pais.getPais())
                .build();
    }


    private TbPaisEntity builderPais(String codigoPais) {
        if (Objects.isNull(codigoPais)) return null;
        return TbPaisEntity.builder()
                .codigoPais(codigoPais)
                .build();
    }

}
