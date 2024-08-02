package com.examples.commons;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.StringJoiner;

@Slf4j
public class GuavaExample {

    @Test
    void supplier() {

    }

    @Test
    void string() {
        String s1 = Joiner.on("-").join("hello", "world");
        log.info(s1);

        String s2 = Joiner.on("&").withKeyValueSeparator("=")
                .join(Map.of("k1", "v1", "k2", "v2"));
        log.info(s2);

        // 这里也可以用 JSON.toJSONString
        StringJoiner sj = new StringJoiner(",", "[", "]");
        String s3 = sj.add("hi").add("hei").add("hello").toString();
        log.info(s3);

        Splitter.on(",").splitToList("hello,world");
        Splitter.on(",").omitEmptyStrings().trimResults().splitToList("a, b,,c ,d");
        Splitter.on("&").withKeyValueSeparator("=").split("k1=v1&k2=v2");
    }
}
