package com.mastermindcms.modules.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageNavigation {

    private Object number;

    private long value;

    private long numberOfElements;

}
