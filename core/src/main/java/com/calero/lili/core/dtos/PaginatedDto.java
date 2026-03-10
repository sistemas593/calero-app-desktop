package com.calero.lili.core.dtos;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedDto<T> {
    private List<T> content;

    private Paginator paginator;

//    private Long totalElements;
//    private int totalPages;
//    private int numberOfElements;
//    private int size;
//
//    private Boolean first;
//    private Boolean last;
//    private int pageNumber;
//    private int pageSize;
//
//    private Boolean empty;
}
