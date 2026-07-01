package com.usco.convocatoria.app.reports.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvocationCategoryResponse {
    private String category;
    private Integer count;
    private Integer countDraft;
    private Integer countPublished;
    private Integer countClosed;


}
