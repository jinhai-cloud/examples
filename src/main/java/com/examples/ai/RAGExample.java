package com.examples.ai;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RAGExample {

    @Test
    void rag() {
        String baseUrl = "https://api.chatanywhere.tech";
        String apiKey = "sk-3DRp9oFqDQEdkxPLuHRzJoNEx9IPSqWbZWRGMvHdG4cVmY91";

        String path = "";
        String userInput = "";

        // 配置大模型参数
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O_MINI)
                .withTemperature(0.7)
                .build();
        ChatClient chatClient =
                ChatClient.builder(new OpenAiChatModel(openAiApi, options)).build();

        // 配置VectorStore
        VectorStore vectorStore = new SimpleVectorStore(new OpenAiEmbeddingModel(openAiApi));

        // 读取知识库，embedding并保存到VectorStore
        FileSystemResource resource = new FileSystemResource(path);
        List<Document> documents = new TokenTextSplitter().apply(new TikaDocumentReader(resource).read());
        vectorStore.add(documents);

        // QuestionAnswerAdvisor: 根据similaritySearch获取符合的documents，并作为question_answer_context传入大模型
        // QuestionAnswerAdvisor类里可以自定义SearchRequest，配置topK或filterExpression
        // VectorStoreChatMemoryAdvisor: 通过向量数据库来永久记忆chat过程中上下文信息
        // MessageChatMemoryAdvisor: 记忆chat过程中上下文信息
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);
        VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor = new VectorStoreChatMemoryAdvisor(vectorStore);
        MessageChatMemoryAdvisor chatMemoryAdvisor = new MessageChatMemoryAdvisor(new InMemoryChatMemory());

        String result = chatClient
                .prompt()
                .user(userInput)
                .advisors(questionAnswerAdvisor, chatMemoryAdvisor)
                .call()
                .content();
        log.info(result);
    }
}
