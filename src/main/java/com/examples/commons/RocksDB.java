package com.examples.commons;

import java.io.IOException;
import java.util.Objects;

import org.rocksdb.*;

import com.google.common.base.Preconditions;

public class RocksDB {
    private final org.rocksdb.RocksDB rocksDB;

    static {
        org.rocksdb.RocksDB.loadLibrary();
    }

    public RocksDB(String path) throws IOException {
        BloomFilter fullFilter = new BloomFilter(10.0D, false);
        BlockBasedTableConfig tableFormatConfig = new BlockBasedTableConfig()
                .setFilterPolicy(fullFilter)
                .setEnableIndexCompression(false)
                .setIndexBlockRestartInterval(8)
                .setFormatVersion(5);
        Options options = new Options()
                .setCreateIfMissing(true)
                .setBottommostCompressionType(CompressionType.ZSTD_COMPRESSION)
                .setCompressionType(CompressionType.LZ4_COMPRESSION)
                .setTableFormatConfig(tableFormatConfig)
                .setInfoLogLevel(InfoLogLevel.WARN_LEVEL);

        try {
            rocksDB = org.rocksdb.RocksDB.open(options, path);
        } catch (RocksDBException e) {
            throw new IOException("Unable to open RocksDB", e);
        }
    }

    public <T> T get(byte[] key, Class<T> clazz) {
        try {
            byte[] data = rocksDB.get(key);
            return Objects.nonNull(data) ? KryoUtils.deserialize(data, clazz) : null;
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void put(byte[] key, T value) {
        Preconditions.checkArgument(value != null, "Null values are not allowed.");
        try {
            rocksDB.put(key, KryoUtils.serialize(value));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }
}
