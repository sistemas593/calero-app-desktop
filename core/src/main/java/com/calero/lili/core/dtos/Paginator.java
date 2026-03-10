package com.calero.lili.core.dtos;

import lombok.Data;

@Data
public class Paginator {
    private Long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;

    private Boolean first;
    private Boolean last;
    private int pageNumber;
    private int pageSize;

    private Boolean empty;
    private int number;
}
