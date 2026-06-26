package com.llm.chats;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/v1/chats")
    public Object chat(@RequestBody @Valid UserInput userInput) {

        log.info("userInput : {} ", userInput);
        var requestSpec = chatClient.prompt()
                .user(userInput.prompt());

        log.info("requestSpec : {}", requestSpec);

        var responseSpec = requestSpec.call();
        log.info("responseSpec : {}", responseSpec);

        return responseSpec.content();
    }

    @PostMapping("/v1/chats/stream")
    public Flux<String> chatWithStream(@RequestBody @Valid UserInput userInput) {
        return chatClient.prompt()
                .user(userInput.prompt())
                .stream()
                .content()
                .doOnNext(s -> log.info("s : {}", s))
                .doOnComplete(() -> log.info("Data complete"));
    }
}
