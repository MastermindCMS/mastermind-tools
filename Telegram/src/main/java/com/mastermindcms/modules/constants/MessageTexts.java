package com.mastermindcms.modules.constants;

import static com.mastermindcms.modules.constants.Commands.*;

public class MessageTexts {
    public static final String INTRODUCTION =
            COMMAND_HI_GPT + " - начать чат с ChatGPT 3.5.%n" +
            COMMAND_ASK_GPT + " - спросить ChatGPT 3.%n" +
            COMMAND_NEW_PIC + " - создать картинку по описанию. Используется DALL-E AI.%n%n" +
            COMMAND_START + " - перейти в главное меню.";
    public static final String START_CONVERSATION = "Привет, %s!%nЯ ChatGPT 3 что ты хотел бы у меня спросить?";
    public static final String START_CHATTING = "Привет, %s!%nЯ ChatGPT 3.5 что ты хотел бы у меня спросить?";
    public static final String START_DRAWING = "Привет, %s!%nЧто ты хочешь нарисовать?";
    public static final String ERROR_MSG = "Опа.. Есть некоторые проблемы с чатом:%n%s";
    public static final String WAITING_MSG = "Хм...";
    public static final String ABOUT =
            "Author - Alexander Yanchin%n" +
            "Contacts:%n" +
            "https://mastermindcms.co%n" +
            "https://www.linkedin.com/in/aleksandr-yanchin/%n";
}
