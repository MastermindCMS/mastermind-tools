package com.mastermindcms.modules.telegram.bots;

import com.mastermindcms.ai.gpt.services.ChatGPTService;
import com.mastermindcms.ai.gpt.services.DalleService;
import com.mastermindcms.modules.config.TelegramConfiguration;
import com.mastermindcms.modules.dto.Question;
import com.mastermindcms.modules.services.AnswerServiceImpl;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.Image;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mastermindcms.modules.constants.Commands.COMMAND_ABOUT;
import static com.mastermindcms.modules.constants.Commands.COMMAND_ASK_GPT;
import static com.mastermindcms.modules.constants.Commands.COMMAND_HI_GPT;
import static com.mastermindcms.modules.constants.Commands.COMMAND_NEW_PIC;
import static com.mastermindcms.modules.constants.Commands.COMMAND_START;
import static com.mastermindcms.modules.constants.MessageTexts.ABOUT;
import static com.mastermindcms.modules.constants.MessageTexts.ERROR_MSG;
import static com.mastermindcms.modules.constants.MessageTexts.INTRODUCTION;
import static com.mastermindcms.modules.constants.MessageTexts.WAITING_MSG;

@Slf4j
@Component
public class AiTelegramBot extends TelegramLongPollingBot {

    @Autowired
    private TelegramConfiguration telegramConfiguration;

    @Autowired
    private ChatGPTService chatService;

    @Autowired
    private DalleService dalleService;

    @Autowired
    private AnswerServiceImpl answerService;

    private final Map<Long, List<Question>> questions = new HashMap<>();
    private final Map<Integer, Message> answers = new HashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {
        if(Objects.nonNull(this.getBotToken()) && !this.getBotToken().isEmpty()){
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(this);
            } catch (TelegramApiException e) {
                log.error("Bot initialize:", e);
            }

            List<BotCommand> listOfCommands = new ArrayList<>();
            listOfCommands.add(new BotCommand(COMMAND_START, "главное меню"));
            listOfCommands.add(new BotCommand(COMMAND_HI_GPT, "начать общение с ChatGPT 3.5"));
            listOfCommands.add(new BotCommand(COMMAND_ASK_GPT, "спросить ChatGPT 3"));
            listOfCommands.add(new BotCommand(COMMAND_NEW_PIC, "нарисовать картинку по описанию. Используется DALL-E AI"));
            listOfCommands.add(new BotCommand(COMMAND_ABOUT, "Об авторе"));

            try {
                this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
            } catch (TelegramApiException e) {
                log.error("Error setting bot's command list: " + e.getMessage());
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(Objects.nonNull(this.getBotToken()) && !this.getBotToken().isEmpty()) {
            executorService.execute(() -> {
                if (update.hasMessage() && update.getMessage().hasText()) {
                    Long chatId = update.getMessage().getChatId();
                    Long userId = update.getMessage().getFrom().getId();
                    Integer messageId = update.getMessage().getMessageId();
                    String receivedText = update.getMessage().getText()
                            .replace("@" + telegramConfiguration.getBotName(), "");
                    String command = getCommand(receivedText.toLowerCase());
                    Object message;
                    List<Question> chatQuestions;
                    Question question = Question.builder()
                            .userId(userId)
                            .messageId(messageId)
                            .chatId(chatId)
                            .build();
                    try {
                        switch (receivedText.toLowerCase()) {
                            case COMMAND_START:
                                message = answerService.createSimpleMsg(update, INTRODUCTION);
                                chatQuestions = Objects.nonNull(questions.get(chatId)) ? questions.get(chatId) : new ArrayList<>();
                                chatQuestions.remove(question);
                                questions.put(chatId, chatQuestions);
                                break;
                            case COMMAND_ABOUT:
                                message = answerService.createSimpleMsg(update, ABOUT);
                                chatQuestions = Objects.nonNull(questions.get(chatId)) ? questions.get(chatId) : new ArrayList<>();
                                chatQuestions.remove(question);
                                questions.put(chatId, chatQuestions);
                                break;
                            case COMMAND_HI_GPT:
                                message = answerService.chatGreeting(update);
                                question.setCommand(COMMAND_HI_GPT);
                                chatQuestions = questions.get(chatId);
                                chatQuestions.add(question);
                                questions.put(chatId, chatQuestions);
                                break;
                            case COMMAND_ASK_GPT:
                                message = answerService.completionGreeting(update);
                                question.setCommand(COMMAND_ASK_GPT);
                                chatQuestions = questions.get(chatId);
                                chatQuestions.add(question);
                                questions.put(chatId, chatQuestions);
                                break;
                            case COMMAND_NEW_PIC:
                                message = answerService.picGreeting(update);
                                question.setCommand(COMMAND_NEW_PIC);
                                chatQuestions = questions.get(chatId);
                                chatQuestions.add(question);
                                questions.put(chatId, chatQuestions);
                                break;
                            default:
                                if (questions.containsKey(chatId)) {
                                    chatQuestions = questions.get(chatId);
                                    Question askedQuestion;
                                    if (command != null) {
                                        askedQuestion = chatQuestions.stream()
                                                .filter(m -> m.getUserId().equals(userId) && m.getCommand().equals(command))
                                                .findFirst().orElseGet(() -> Question.builder()
                                                        .command(command)
                                                        .userId(userId)
                                                        .messageId(messageId)
                                                        .chatId(chatId)
                                                        .build());
                                    } else {
                                        askedQuestion = chatQuestions.stream()
                                                .filter(m -> m.getUserId().equals(userId))
                                                .findFirst().orElseGet(() -> Question.builder()
                                                        .command(COMMAND_ASK_GPT)
                                                        .userId(userId)
                                                        .messageId(messageId)
                                                        .chatId(chatId)
                                                        .build());
                                    }
                                    chatQuestions.remove(question);
                                    questions.put(chatId, chatQuestions);
                                    message = chooseService(askedQuestion, update);
                                } else {
                                    message = answerService.createSimpleMsg(update, INTRODUCTION);
                                }
                        }

                        if (answers.containsKey(messageId)) {
                            Integer msgAnswerId = answers.get(messageId).getMessageId();
                            DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), msgAnswerId);
                            answers.remove(messageId);
                            execute(deleteMessage);
                        }
                        if (message instanceof SendMessage) {
                            execute((SendMessage) message);
                        } else if (message instanceof SendPhoto) {
                            execute((SendPhoto) message);
                        } else {
                            if (telegramConfiguration.getReactiveUpdate()) {
                                Message msg = sendWaitingMsg(chatId);
                                if (Objects.nonNull(message)) {
                                    StringBuilder sb = new StringBuilder();
                                    EditMessageText editMessageText = new EditMessageText();
                                    editMessageText.setChatId(chatId);
                                    editMessageText.setMessageId(msg.getMessageId());
                                    editMessageText.setText("");
                                    ((Flowable<?>) message).subscribe((res) -> {
                                        ChatCompletionChunk chunk = (ChatCompletionChunk) res;
                                        for (ChatCompletionChoice choice : chunk.getChoices()) {
                                            if (Objects.nonNull(choice.getMessage().getContent())) {
                                                sb.append(choice.getMessage().getContent());
                                            }
                                            if (Objects.nonNull(choice.getFinishReason()) &&
                                                    choice.getFinishReason().equals("stop") &&
                                                    !editMessageText.getText().equals(sb.toString().trim())) {
                                                editMessageText.setText(sb.toString());
                                                execute(editMessageText);
                                            }
                                        }
                                        if (!sb.toString().isBlank() && sb.toString().length() % 20 == 0 &&
                                                !editMessageText.getText().equals(sb.toString().trim())) {
                                            editMessageText.setText(sb.toString());
                                            TimeUnit.MILLISECONDS.sleep(500);
                                            execute(editMessageText);
                                        }
                                    });
                                }

                            }
                        }

                    } catch (TelegramApiException e) {
                        log.error("Problem with sending a message", e);
                    }
                }
            });
        }
    }

    private Message sendWaitingMsg(Long chatId) throws TelegramApiException {
        Message execute = execute(answerService.createSimpleMsg(chatId.toString(), WAITING_MSG));
        answers.put(execute.getMessageId(), execute);
        return execute;
    }

    private Object chooseService(Question question, Update update) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage message = new ChatMessage();
            message.setContent(update.getMessage().getText());
            message.setRole(ChatMessageRole.USER.value());
            messages.add(message);
            switch (question.getCommand()) {
                case COMMAND_HI_GPT:
                case COMMAND_ASK_GPT:
                    if(telegramConfiguration.getReactiveUpdate()){
                        return chatService.streamAsk(messages, update.getMessage().getChat().getUserName());
                    } else {
                        ChatCompletionResult result = chatService.ask(messages, update.getMessage().getChat().getUserName());
                        return answerService.gptChatCompletion(result.getChoices(),update.getMessage().getChatId());
                    }
                case COMMAND_NEW_PIC:
                    Image image = dalleService.generateImage(update.getMessage().getText(), update.getMessage().getChat().getUserName());
                    return answerService.dallePicURL(image, update.getMessage().getChatId());

            }
        } catch (Exception e) {
            log.error("We have some problems:", e);
            return answerService.getErrorText(String.format(ERROR_MSG, e.getMessage()), update.getMessage().getChatId());
        }
        return null;
    }

    private String getCommand(String text) {
        if(text.contains(COMMAND_START)){
            return COMMAND_START;
        } else if(text.contains(COMMAND_ABOUT)){
            return COMMAND_ABOUT;
        } else if(text.contains(COMMAND_ASK_GPT)){
            return COMMAND_ASK_GPT;
        } else if(text.contains(COMMAND_HI_GPT)){
            return COMMAND_HI_GPT;
        } else if(text.contains(COMMAND_NEW_PIC)){
            return COMMAND_NEW_PIC;
        } else {
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        return telegramConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramConfiguration.getToken();
    }

}
