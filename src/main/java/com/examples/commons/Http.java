package com.examples.commons;

import com.examples.exceptions.HttpException;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
public class Http {
    private static final OkHttpClient client;
    private static final String DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8";

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(1000))
                .readTimeout(Duration.ofMillis(1000))
                .writeTimeout(Duration.ofMillis(1000))
                .build();
    }

    public static String get(String url) {
        Request request = new Request.Builder().url(url).build();
        return doRequest(request);
    }

    public static String get(String url, Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        headers.forEach(builder::add);

        Request request = new Request.Builder().url(url).headers(builder.build()).build();
        return doRequest(request);
    }

    public static String post(String url, String json) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, MediaType.parse(DEFAULT_MEDIA_TYPE)))
                .build();
        return doRequest(request);
    }

    public static String post(String url, String json, Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        headers.forEach(builder::add);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, MediaType.parse(DEFAULT_MEDIA_TYPE)))
                .headers(builder.build())
                .build();
        return doRequest(request);
    }

    public static String post(String url, Map<String, String> formBody) {
        FormBody.Builder builder = new FormBody.Builder();
        formBody.forEach(builder::add);

        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return doRequest(request);
    }

    private static String doRequest(Request request) {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                return body != null ? body.string() : null;
            } else {
                throw new HttpException(Placeholder.format("Response code is incorrect: {}, {}, {}", request.url(), response.code(), response.message()));
            }
        } catch (IOException e) {
            Throwables.throwIfUnchecked(e);
            throw new HttpException(Placeholder.format("Request {} failed", request.url()), e);
        }
    }
}
