package com.calero.lili.core.modComprasItems.builder;

import com.calero.lili.core.modComprasItems.GeItemsPreciosEntity;
import com.calero.lili.core.modComprasItems.dto.GeItemRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class GeItemsPreciosBuilder {

    public List<GeItemsPreciosEntity> builderListPrecios(List<GeItemRequestDto.Precios> list) {
        return list.stream()
                .map(this::builderPrecios)
                .toList();
    }

    private GeItemsPreciosEntity builderPrecios(GeItemRequestDto.Precios model) {
        return GeItemsPreciosEntity.builder()
                .idItemsPrecio(UUID.randomUUID())
                .precio1(model.getPrecio1())
                .precio2(model.getPrecio2())
                .precio3(model.getPrecio3())
                .precio4(model.getPrecio4())
                .precio5(model.getPrecio5())
                .build();
    }
}
