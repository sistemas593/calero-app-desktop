package com.calero.lili.core.modComprasItems.builder;

import com.calero.lili.core.modComprasItems.GeItemsPreciosEntity;
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class GeItemsPreciosBuilder {

    public List<GeItemsPreciosEntity> builderListPrecios(List<GeItemRequestDto.Precios> list, Long idData, Long idEmpresa) {
        if (Objects.isNull(list)) return null;
        return list.stream()
                .map(precios -> builderPrecios(precios, idData, idEmpresa))
                .toList();
    }

    private GeItemsPreciosEntity builderPrecios(GeItemRequestDto.Precios model, Long idData, Long idEmpresa) {
        return GeItemsPreciosEntity.builder()
                .idItemsPrecio(UUID.randomUUID())
                .idData(idData)
                .idEmpresa(idEmpresa)
                .precio1(Objects.nonNull(model.getPrecio1()) ? model.getPrecio1() : BigDecimal.ZERO)
                .precio2(Objects.nonNull(model.getPrecio2()) ? model.getPrecio2() : BigDecimal.ZERO)
                .precio3(Objects.nonNull(model.getPrecio3()) ? model.getPrecio3() : BigDecimal.ZERO)
                .precio4(Objects.nonNull(model.getPrecio4()) ? model.getPrecio4() : BigDecimal.ZERO)
                .precio5(Objects.nonNull(model.getPrecio5()) ? model.getPrecio5() : BigDecimal.ZERO)
                .build();
    }
}
