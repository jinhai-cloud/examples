package com.examples.commons;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

public class KryoUtils {
    private static final int POOL_CAPACITY = 8;
    private static final Pool<Kryo> kryoPool = new Pool<>(true, false, POOL_CAPACITY) {
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    public static <T extends Serializable> byte[] serialize(T object) {
        Kryo kryo = kryoPool.obtain();
        Output output = new Output(4096);
        kryo.writeObject(output, object);
        output.close();
        kryoPool.free(kryo);
        return output.toBytes();
    }

    public static <T extends Serializable> void serialize(T object, OutputStream outputStream) {
        Kryo kryo = kryoPool.obtain();
        Output output = new Output(outputStream);
        kryo.writeObject(output, object);
        output.close();
        kryoPool.free(kryo);
    }

    public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryoPool.obtain();
        Input input = new Input(bytes);
        T object = kryo.readObject(input, clazz);
        input.close();
        kryoPool.free(kryo);
        return object;
    }

    public static <T extends Serializable> T deserialize(InputStream inputStream, Class<T> clazz) {
        Kryo kryo = kryoPool.obtain();
        Input input = new Input(inputStream);
        T object = kryo.readObject(input, clazz);
        input.close();
        kryoPool.free(kryo);
        return object;
    }
}
