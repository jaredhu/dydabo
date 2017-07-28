/*
 * Copyright 2017 viswadas leher .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.dydabo.blackbox;

import com.dydabo.blackbox.cassandra.CassandraBlackBoxImpl;
import com.dydabo.blackbox.db.CassandraConnectionManager;
import com.dydabo.blackbox.hbase.HBaseBlackBoxImpl;
import com.dydabo.blackbox.mongodb.MongoBlackBoxImpl;
import com.dydabo.blackbox.redis.RedisBlackBoxImpl;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * Get an instance to to generic interface that allows you to interact with the backend database
 *
 * @author viswadas leher
 */
public class BlackBoxFactory {

    /**
     * Constant specifying the HBase database
     */
    public final static String HBASE = "hbase";

    /**
     * Constant specifying the Cassandra database
     */
    public final static String CASSANDRA = "cassandra";

    /**
     * Constant specifying the MongoDB database
     */
    public final static String MONGODB = "mongodb";

    public final static String REDIS = "redis";


    private BlackBoxFactory() {
        // No instance of this factory class
    }

    /**
     * Get an instance by specifying the type of the database
     *
     * @param databaseType
     * @return a Blackbox instance
     * @throws java.io.IOException
     */
    public static BlackBox<BlackBoxable> getDatabase(String databaseType) throws IOException {
        BlackBox<BlackBoxable> db = null;
        switch (databaseType) {
            case HBASE:
                db = new HBaseBlackBoxImpl<>();
                break;
            case CASSANDRA:
                db = new CassandraBlackBoxImpl<>();
                break;
            case MONGODB:
                db = new MongoBlackBoxImpl<>();
                break;
            case REDIS:
                db = new RedisBlackBoxImpl<>();
                break;

            default:
                db = null;
                break;
        }

        return db;
    }

    /**
     * Get a BlackBox instance for the HBase database given the configuration
     *
     * @param config the HBase Configuration object
     * @return a BlackBox instance
     * @throws IOException
     */
    public static BlackBox<BlackBoxable> getHBaseDatabase(Configuration config) throws IOException {
        return new HBaseBlackBoxImpl<>(config);
    }

    /**
     * @return
     */
    public static BlackBox<BlackBoxable> getCassandraDatabase(String address) {
        // TODO: customizable...configuration of the database
        CassandraConnectionManager.setAddress(address);
        return new CassandraBlackBoxImpl<>();
    }

    /**
     * @return
     */
    public static BlackBox<BlackBoxable> getMongoDatabase() {
        // TODO: configurable database
        return new MongoBlackBoxImpl<>();
    }

    public static BlackBox<BlackBoxable> getRedisDatabase() {
        return new RedisBlackBoxImpl<>();
    }


}
