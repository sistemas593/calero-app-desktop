package com.calero.lili.core.dtos;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PaginatedDtoPage<T> {
    private Page<T> content;

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
