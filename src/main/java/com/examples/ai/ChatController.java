package com.examples.ai;

import com.examples.commons.JSON;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.chatMemory = new InMemoryChatMemory();
    }

    @GetMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(String prompt, String sessionId) {
        MessageChatMemoryAdvisor advisor = new MessageChatMemoryAdvisor(chatMemory, sessionId, 10);

        return this.chatClient.prompt()
                .user(prompt)
                .advisors(advisor)
                .stream()
                .chatResponse()
                .map(chatResponse -> ServerSentEvent.<String>builder()
                        .id(sessionId)
                        .event("message")
                        .data(JSON.toJSONString(chatResponse.getResult()))
                        .build());
    }
}
