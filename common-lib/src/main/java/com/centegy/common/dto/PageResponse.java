package com.centegy.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int totalPages;
    private int page;
    private int pageSize;
    private Long totalElements;
    private int numberOfElements;
    private boolean firstPage;
    private boolean lastPage;
    private boolean previous;
    private boolean next;

}
