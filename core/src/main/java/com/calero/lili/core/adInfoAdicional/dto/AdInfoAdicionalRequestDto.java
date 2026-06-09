package com.calero.lili.core.adInfoAdicional.dto;


import com.calero.lili.core.adInfoAdicional.AdInformacionAdicional;
import com.calero.lili.core.enums.TipoDocumentoSerie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdInfoAdicionalRequestDto {

    private TipoDocumentoSerie documento;
    private List<AdInformacionAdicional> informacionAdicional;
}
