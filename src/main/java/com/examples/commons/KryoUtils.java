package com.examples.commons;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

public class KryoUtils {
    private static final int POOL_CAPACITY = 16;
    private static final Pool<Kryo> kryoPool = new Pool<>(true, false, POOL_CAPACITY) {
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    public static <T> byte[] serialize(T object) {
        Kryo kryo = kryoPool.obtain();
        try {
            Output output = new Output(4096);
            kryo.writeObject(output, object);
            output.close();
            return output.toBytes();
        } finally {
            kryoPool.free(kryo);
        }
    }

    public static <T> void serialize(T object, OutputStream outputStream) {
        Kryo kryo = kryoPool.obtain();
        try {
            Output output = new Output(outputStream);
            kryo.writeObject(output, object);
            output.close();
        } finally {
            kryoPool.free(kryo);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryoPool.obtain();
        try {
            Input input = new Input(bytes);
            T object = kryo.readObject(input, clazz);
            input.close();
            return object;
        } finally {
            kryoPool.free(kryo);
        }
    }

    public static <T> T deserialize(InputStream inputStream, Class<T> clazz) {
        Kryo kryo = kryoPool.obtain();
        try {
            Input input = new Input(inputStream);
            T object = kryo.readObject(input, clazz);
            input.close();
            return object;
        } finally {
            kryoPool.free(kryo);
        }
    }
}
