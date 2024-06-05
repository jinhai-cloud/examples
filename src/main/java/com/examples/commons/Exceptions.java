package com.examples.commons;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.commons.lang3.exception.UncheckedInterruptedException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class Exceptions {

    public void sample() throws IOException {
        try {
            throwIOException();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        try {
            throwTimeoutException();
        } catch (TimeoutException e) {
            throw new UncheckedTimeoutException(e);
        }

        try {
            throwInterruptedException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UncheckedInterruptedException(e);
        }

        try {
            throwIOException();
            throwSQLException();
        } catch (Exception e) {
            // 当需要获取关键数据时，可以打log
            // 有时异常是框架内抛出的，框架外捕获后无法看到具体的方法调用栈，
            // 可以通过打印log或者再抛出新异常，从而获取方法调用栈
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }

        try {
            throwIOException();
            throwSQLException();
        } catch (Exception e) {
            Throwables.throwIfInstanceOf(e, IOException.class);
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }

        try {
            throwIOException();
            throwSQLException();
        } catch (Exception e) {
            // 当e是嵌套的异常类，可以通过getCause获取原始异常，再throw，而不是再封装成新异常抛出
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            Throwables.throwIfUnchecked(cause);
            throw new RuntimeException(cause);
        }
    }

    private void throwIOException() throws IOException {
        throw new IOException();
    }

    private void throwTimeoutException() throws TimeoutException {
        throw new TimeoutException();
    }

    private void throwSQLException() throws SQLException {
        throw new SQLException();
    }

    private void throwInterruptedException() throws InterruptedException {
        throw new InterruptedException();
    }
}
