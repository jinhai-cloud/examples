package com.examples.commons;

import java.io.IOException;

import com.alibaba.csp.sentinel.*;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;
import com.examples.exceptions.HttpException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SentinelOkHttpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Entry entry = null;
        try {
            Request request = chain.request();
            String name = extract(request, chain.connection());
            entry = SphU.entry(name, ResourceTypeConstants.COMMON_WEB, EntryType.OUT);
            return chain.proceed(request);
        } catch (BlockException e) {
            return handle(chain.request(), chain.connection(), e);
        } catch (HttpException | IOException ex) {
            Tracer.traceEntry(ex, entry);
            throw ex;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    private String extract(Request request, Connection connection) {
        return request.url().host();
    }

    private Response handle(Request request, Connection connection, BlockException e) {
        // Just wrap and throw the exception.
        throw new SentinelRpcException(e);
    }
}
