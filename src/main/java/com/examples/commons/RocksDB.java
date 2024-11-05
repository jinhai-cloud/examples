package com.examples.commons;

import org.rocksdb.*;

public class RocksDB {
    private final org.rocksdb.RocksDB rocksDB;

    static {
        org.rocksdb.RocksDB.loadLibrary();
    }

    public RocksDB(String path) {
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
            throw new RuntimeException(e);
        }
    }
}
