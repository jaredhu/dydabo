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

package com.dydabo.blackbox.redis.tasks;

import com.dydabo.blackbox.BlackBoxable;
import com.dydabo.blackbox.common.DyDaBoUtils;
import com.dydabo.blackbox.db.RedisConnectionManager;
import com.dydabo.blackbox.db.obj.GenericDBTableRow;
import com.dydabo.blackbox.redis.utils.RedisUtils;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

/**
 * @author viswadas leher
 */
public class RedisRangeSearchTask<T extends BlackBoxable> extends RecursiveTask<List<T>> {

    private final T startRow;
    private final T endRow;
    private final long maxResults;
    private final RedisUtils utils;
    private transient Logger logger = Logger.getLogger(RedisRangeSearchTask.class.getName());

    public RedisRangeSearchTask(T startRow, T endRow, long maxResults) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.maxResults = maxResults;
        this.utils = new RedisUtils<>();
    }

    @Override
    protected List<T> compute() {
        return search(startRow, endRow, maxResults);
    }

    private List<T> search(T startRow, T endRow, long maxResults) {
        List<T> results = new ArrayList<>();
        GenericDBTableRow startTableRow = utils.convertRowToTableRow(startRow);
        GenericDBTableRow endTableRow = utils.convertRowToTableRow(endRow);

        final String type = startRow.getClass().getTypeName() + ":";

        // An inefficient search that scans all rows
        try (Jedis connection = RedisConnectionManager.getConnection("localhost")) {
            Set<String> allKeys = connection.keys(type + "*");

            for (String key : allKeys) {
                String currentRow = connection.get(key);
                T rowObject = new Gson().fromJson(currentRow, (Type) startRow.getClass());

                if (filter(rowObject, startTableRow, endTableRow)) {
                    results.add(rowObject);
                    if (maxResults > 0 && results.size() >= maxResults) {
                        break;
                    }
                }
            }
        }

        return results;
    }

    private boolean filter(T rowObject, GenericDBTableRow startTableRow, GenericDBTableRow endTableRow) {
        GenericDBTableRow tableRowObject = utils.convertRowToTableRow(rowObject);

        for (Map.Entry<String, GenericDBTableRow.ColumnFamily> familyEntry : tableRowObject.getColumnFamilies().entrySet()) {
            GenericDBTableRow.ColumnFamily colFamily = familyEntry.getValue();
            for (Map.Entry<String, GenericDBTableRow.Column> columnEntry : colFamily.getColumns().entrySet()) {
                String columnName = columnEntry.getKey();
                GenericDBTableRow.Column columnValue = columnEntry.getValue();

                GenericDBTableRow.Column startColValue = startTableRow.getColumnFamily(colFamily.getFamilyName()).getColumn(columnName);
                GenericDBTableRow.Column endColValue = endTableRow.getColumnFamily(colFamily.getFamilyName()).getColumn(columnName);

                if (!compare(startColValue, columnValue, endColValue)) {
                    return false;
                }

            }
        }

        return true;
    }

    private boolean compare(GenericDBTableRow.Column startColValue, GenericDBTableRow.Column columnValue, GenericDBTableRow.Column endColValue) {
        Object s1 = null;
        Object s2 = null;
        Object s3 = null;

        if (startColValue != null && DyDaBoUtils.isNotBlankOrNull(startColValue.getColumnValueAsString())) {
            s1 = startColValue.getColumnValue();
        }

        if (columnValue != null && DyDaBoUtils.isNotBlankOrNull(columnValue.getColumnValueAsString())) {
            s2 = columnValue.getColumnValue();
        }

        if (endColValue != null && DyDaBoUtils.isNotBlankOrNull(endColValue.getColumnValueAsString())) {
            s3 = endColValue.getColumnValue();
        }
        //logger.info("Comparing :" + s1 + " : " + s2 + " : " + s3);
        boolean flag = false;

        if (s1 == null && s3 == null) {
            return true;
        }

        if (Objects.equals(DyDaBoUtils.EMPTY_ARRAY, startColValue.getColumnValueAsString()) && Objects.equals(DyDaBoUtils.EMPTY_ARRAY, endColValue.getColumnValueAsString())) {
            flag = true;
        }

        if (Objects.equals(DyDaBoUtils.EMPTY_MAP, startColValue.getColumnValueAsString()) && Objects.equals(DyDaBoUtils.EMPTY_MAP, endColValue.getColumnValueAsString())) {
            flag = true;
        }

        if (flag) {
            return flag;
        }

        if (s1 != null && s2 != null) {
            if (s1 instanceof Number && s2 instanceof Number) {
                if (compareTo((Number) s1, (Number) s2) <= 0) {
                    flag = true;
                }
            } else if (s1 instanceof String && s2 instanceof String) {
                if (DyDaBoUtils.isARegex((String) s1)) {
                    flag = ((String) s2).matches((String) s1);
                } else if (((String) s1).compareTo((String) s2) <= 0) {
                    flag = true;
                }
            }
        }

        if (flag && s2 != null && s3 != null) {
            if (s2 instanceof Number && s3 instanceof Number) {
                if (compareTo((Number) s2, (Number) s3) > 0) {
                    flag = false;
                }
            } else if (s2 instanceof String && s3 instanceof String) {
                if (DyDaBoUtils.isARegex((String) s3)) {
                    flag = ((String) s2).matches((String) s3);
                } else if (((String) s2).compareTo((String) s3) > 0) {
                    flag = false;
                }
            }
        }

        return flag;
    }

    private int compareTo(Number n1, Number n2) {
        // ignoring null handling
        return BigDecimal.valueOf(n1.doubleValue()).compareTo(BigDecimal.valueOf(n2.doubleValue()));
    }
}
