package com.examples.llm;

import java.nio.ByteBuffer;
import javax.sound.sampled.*;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.exception.NoApiKeyException;

import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/audio")
public class AudioExample {
    private final DashScopeChatModel chatModel;
    private final TargetDataLine asr;
    private final SourceDataLine tts;

    @Value("${spring.ai.dashscope.api-key}")
    private String API_KEY;

    private static final String ASR_MODEL = "paraformer-realtime-v2";
    private static final String TTS_MODEL = "cosyvoice-v2";
    private static final String VOICE = "longxiaochun_v2";

    public AudioExample(DashScopeChatModel chatModel) throws LineUnavailableException {
        this.chatModel = chatModel;

        AudioFormat asrFormat = new AudioFormat(16000, 16, 1, true, false);
        asr = AudioSystem.getTargetDataLine(asrFormat);
        asr.open(asrFormat);
        asr.start();

        AudioFormat ttsFormat = new AudioFormat(48000, 16, 1, true, false);
        tts = AudioSystem.getSourceDataLine(ttsFormat);
        tts.open(ttsFormat);
        tts.start();
    }

    @GetMapping("/asr/stream")
    public void asrStream() {}

    @GetMapping("/tts/call")
    public void ttsCall(@RequestParam(defaultValue = "给我讲一个笑话") String message) throws NoApiKeyException {
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(API_KEY)
                .model(TTS_MODEL)
                .voice(VOICE)
                .format(SpeechSynthesisAudioFormat.PCM_48000HZ_MONO_16BIT)
                .build();
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(param, null);
        speechSynthesizer.callAsFlowable(message).blockingForEach(result -> {
            ByteBuffer data = result.getAudioFrame();
            if (data != null) {
                tts.write(data.array(), 0, data.array().length);
            }
        });
    }

    @GetMapping("/tts/stream")
    public void ttsStream(@RequestParam(defaultValue = "给我讲一个笑话") String message) throws NoApiKeyException {
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(API_KEY)
                .model(TTS_MODEL)
                .voice(VOICE)
                .format(SpeechSynthesisAudioFormat.PCM_48000HZ_MONO_16BIT)
                .build();
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(param, null);

        Prompt prompt = new Prompt(
                message,
                DashScopeChatOptions.builder()
                        .withModel(DashScopeApi.ChatModel.QWEN_MAX.getModel())
                        .withEnableSearch(true)
                        .build());
        Flux<String> chatFlux = this.chatModel.stream(prompt).map(response -> {
            String text = response.getResult().getOutput().getText();
            System.out.print(text);
            return text;
        });

        speechSynthesizer
                .streamingCallAsFlowable(Flowable.fromPublisher(chatFlux))
                .blockingForEach(result -> {
                    ByteBuffer data = result.getAudioFrame();
                    if (data != null) {
                        tts.write(data.array(), 0, data.array().length);
                    }
                });
    }
}
