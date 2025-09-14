package com.mastermindcms.modules.beans;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class GroupFilter {

    @Id
    private String id;

    private String filterId;

    private String count;

    private Object value;

    private Double step;

    private Double min;

    private Double max;

}
