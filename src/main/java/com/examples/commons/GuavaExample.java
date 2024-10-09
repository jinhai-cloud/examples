package com.examples.commons;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.google.common.base.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GuavaExample {

    @Test
    void string() {
        String s1 = Joiner.on("-").join("hello", "world");
        log.info(s1);

        String s2 = Joiner.on("&").withKeyValueSeparator("=").join(Map.of("k1", "v1", "k2", "v2"));
        log.info(s2);

        // 这里也可以用 JSON.toJSONString
        StringJoiner sj = new StringJoiner(",", "[", "]");
        String s3 = sj.add("hi").add("hei").add("hello").toString();
        log.info(s3);

        Splitter.on(",").splitToList("hello,world");
        Splitter.on(",").omitEmptyStrings().trimResults().splitToList("a, b,,c ,d");
        Splitter.on("&").withKeyValueSeparator("=").split("k1=v1&k2=v2");

        // 统一用 %s 代替
        Strings.lenientFormat("%s, i am %s years old", "Hi", 10);
    }

    /**
     * 单例，或对象懒加载
     */
    @Test
    void supplier() {
        Suppliers.ofInstance("Hello");
        Suppliers.memoize(() -> "Hello");
        Suppliers.memoizeWithExpiration(() -> "Hello", 300, TimeUnit.SECONDS);
    }

    @Test
    void check() {
        String input = "hello";

        // 方法前置入参检查
        Preconditions.checkArgument(input.length() > 3, "input %s 长度小于%s", 3);
        Preconditions.checkNotNull(input, "input %s is null", input);

        // 方法内对象或变量检查，Assert
        Verify.verify(input.length() > 3, "input %s 长度小于%s", 3);
        Verify.verifyNotNull(input, "input %s is null", input);
    }
}
