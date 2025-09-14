package com.mastermindcms.modules.beans;

import com.mastermindcms.modules.enums.FiltersStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRequest {

    private String id;

    private Map<String,Object> query;

    private String queryAsString;

    private String language;

    private String searchTerm;

    private String type;

    private long page;

    private List<String> ignoreRegexWrap;

    private FiltersStrategy filteringStrategy;

    private long offset;

    private long limit;

    private long visiblePages;

    private Map<String,Object> sort;

    private String sortDirection;

    private String sortName;

    private LocalDate from;

    private LocalDate to;

    @Builder.Default
    private boolean aiSearchActive = false;

    @Builder.Default
    private boolean optimal = false;

    @Builder.Default
    private boolean lazyLoad = false;

}
