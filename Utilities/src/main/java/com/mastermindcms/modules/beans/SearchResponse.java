package com.mastermindcms.modules.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Deque;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResponse {

    Deque<PageNavigation> pages;

    Page<?> data;

}
