package com.examples.llm;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/llm")
public class LLMExample {

    private final OpenAiChatModel chatModel;
    private final OpenAiImageModel imageModel;
    private final OpenAiAudioTranscriptionModel asrModel;
    private final OpenAiAudioSpeechModel ttsModel;

    public LLMExample(
            OpenAiChatModel chatModel,
            OpenAiImageModel imageModel,
            OpenAiAudioTranscriptionModel asrModel,
            OpenAiAudioSpeechModel ttsModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
        this.asrModel = asrModel;
        this.ttsModel = ttsModel;
    }

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam(defaultValue = "给我讲一个笑话") String message) {
        Prompt prompt = new Prompt(
                message,
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O)
                        .temperature(0.8)
                        .build());
        return this.chatModel.stream(prompt)
                .map(response -> (response.getResult() == null
                                || response.getResult().getOutput() == null
                                || response.getResult().getOutput().getText() == null)
                        ? ""
                        : response.getResult().getOutput().getText())
                .filter(StringUtils::isNotEmpty);
    }

    @GetMapping("/image")
    public String image(@RequestParam(defaultValue = "给我画一只猫") String message) {
        ImagePrompt imagePrompt = new ImagePrompt(
                message,
                OpenAiImageOptions.builder()
                        .model(OpenAiImageApi.DEFAULT_IMAGE_MODEL)
                        .quality("hd")
                        .N(1)
                        .height(1024)
                        .width(1024)
                        .build());

        String html = """
                <img src="data:image/png;base64,%s" alt="%s" />
                """;
        return String.format(
                html, imageModel.call(imagePrompt).getResult().getOutput().getB64Json(), message);
    }

    @GetMapping("/asr/call")
    public String asr() {
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .model(OpenAiAudioApi.WhisperModel.WHISPER_1.getValue())
                .prompt("帮我转成简体中文")
                .temperature(0f)
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .build();

        String path = System.getProperty("user.home") + "/llm/asr.mp3";
        AudioTranscriptionPrompt audioPrompt =
                new AudioTranscriptionPrompt(new FileSystemResource(path), transcriptionOptions);
        return asrModel.call(audioPrompt).getResult().getOutput();
    }

    @GetMapping("/tts/call")
    public String ttsCall(@RequestParam(defaultValue = "兄弟们，今天又是躺平的一天") String message) {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(message, speechOptions);
        byte[] output = this.ttsModel.call(speechPrompt).getResult().getOutput();

        String html = """
                <audio controls src="data:audio/mp3;base64,%s" />
                """;
        return String.format(html, Base64.getEncoder().encodeToString(output));
    }

    @GetMapping("/tts/stream")
    public Flux<SpeechResponse> ttsStream(@RequestParam(defaultValue = "兄弟们，今天又是躺平的一天") String message) {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(message, speechOptions);
        return this.ttsModel.stream(speechPrompt);
    }
}
