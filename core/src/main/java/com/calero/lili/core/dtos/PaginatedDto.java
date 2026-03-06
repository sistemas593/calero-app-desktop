package com.calero.lili.core.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedDto<T> {
    private List<T> content;
    private Paginator paginator;
}
