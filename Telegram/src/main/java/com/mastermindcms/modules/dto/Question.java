package com.mastermindcms.modules.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "userId" }, callSuper = false)
public class Question {

    private Long userId;

    private Integer messageId;

    private Long chatId;

    private String command;

}
