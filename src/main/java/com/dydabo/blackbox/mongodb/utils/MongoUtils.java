/*
 * Copyright 2017 viswadas leher <vleher@gmail.com>.
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
package com.dydabo.blackbox.mongodb.utils;

import com.dydabo.blackbox.BlackBoxable;
import com.dydabo.blackbox.common.DBUtils;
import org.bson.Document;

/**
 * @param <T>
 * @author viswadas leher <vleher@gmail.com>
 */
public class MongoUtils<T extends BlackBoxable> extends DBUtils<T> {

    /**
     *
     */
    public static final String PRIMARYKEY = "_id";

    /**
     * @param row
     * @return
     */
    public Document parseRowToDocument(T row) {
        Document doc = Document.parse(row.getBBJson());
        // make sure we have an id
        doc.append("_id", row.getBBRowKey());
        return doc;
    }
}
