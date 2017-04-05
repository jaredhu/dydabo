/*
 * Copyright 2017 viswadas leher <vleher@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dydabo.blackbox.db;

import com.dydabo.blackbox.common.DyDaBoUtils;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author viswadas leher <vleher@gmail.com>
 */
public class MongoDBConnectionManager {

    private static Object lockObj = new Object();
    private static MongoClient mongoClient = null;

    public static MongoCollection<Document> getMongoDBCollection(String connString, String database, String collection) {
        if (mongoClient == null) {
            if (DyDaBoUtils.isBlankOrNull(connString)) {
                connString = "mongodb://localhost";
            }
            synchronized (lockObj) {
                mongoClient = MongoClients.create(connString);
            }
        }
        return mongoClient.getDatabase(database).getCollection(collection);
    }
}
