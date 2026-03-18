package com.calero.lili.core.modVentas.dto;

import com.calero.lili.core.dtos.Paginator;
import com.calero.lili.core.modVentas.projection.TotalesProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetListDtoTotalizado<T> {

    private List<T> content;

    private Paginator paginated;

    private Totales totales;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Totales {
        private BigDecimal totalDescuento;
//        private BigDecimal total;
        private List<TotalesProjection> valoresTotales;
    }

}