package com.dp.gantt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PageResponse<T> {
    private long totalItems;
    private Integer totalPages;
    private List<T> pageData;
}
