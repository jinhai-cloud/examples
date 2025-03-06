package com.examples.llm;

import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.common.ResultCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/audio")
public class AudioExample {
    private final DashScopeChatModel chatModel;
    private final DashScopeSpeechSynthesisModel speechSynthesisModel;
    private final SourceDataLine tts;

    @Value("${spring.ai.dashscope.api-key}")
    private String API_KEY;

    private static final String TTS_MODEL = "cosyvoice-v2";
    private static final String VOICE = "longxiaochun_v2";

    public AudioExample(DashScopeChatModel chatModel, DashScopeSpeechSynthesisModel speechSynthesisModel)
            throws LineUnavailableException {
        this.chatModel = chatModel;
        this.speechSynthesisModel = speechSynthesisModel;

        AudioFormat format = new AudioFormat(48000, 16, 1, true, false);
        tts = AudioSystem.getSourceDataLine(format);
        tts.open(format);
        tts.start();
    }

    @GetMapping("/tts/call")
    public void ttsCall(@RequestParam(defaultValue = "给我讲一个笑话") String message) {
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(API_KEY)
                .model(TTS_MODEL)
                .voice(VOICE)
                .format(SpeechSynthesisAudioFormat.PCM_48000HZ_MONO_16BIT)
                .build();
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(param, null);
        ByteBuffer data = speechSynthesizer.call(message);
        tts.write(data.array(), 0, data.array().length);
    }

    @GetMapping("/tts/stream")
    public void ttsStream(@RequestParam(defaultValue = "给我讲一个笑话") String message) {
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                .apiKey(API_KEY)
                .model(TTS_MODEL)
                .voice(VOICE)
                .format(SpeechSynthesisAudioFormat.PCM_48000HZ_MONO_16BIT)
                .build();
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(param, new ResultCallback<>() {
            @Override
            public void onEvent(SpeechSynthesisResult message) {
                ByteBuffer data = message.getAudioFrame();
                if (data != null) {
                    tts.write(data.array(), 0, data.array().length);
                }
            }

            @Override
            public void onComplete() {}

            @Override
            public void onError(Exception e) {}
        });

        Prompt prompt = new Prompt(
                message,
                DashScopeChatOptions.builder()
                        .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getModel())
                        .withEnableSearch(true)
                        .build());
        this.chatModel.stream(prompt)
                .doFinally(signal -> speechSynthesizer.streamingComplete())
                .subscribe(res -> {
                    String text = res.getResult().getOutput().getText();
                    if (StringUtils.isNotEmpty(text)) {
                        log.info("Out: {}", text);
                        speechSynthesizer.streamingCall(text);
                    }
                });
    }
}
