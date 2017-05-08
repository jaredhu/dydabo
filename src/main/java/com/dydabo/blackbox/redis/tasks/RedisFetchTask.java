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
import com.dydabo.blackbox.db.RedisConnectionManager;
import com.google.gson.Gson;
import org.mortbay.util.SingletonList;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author viswadas leher
 */
public class RedisFetchTask<T extends BlackBoxable> extends RecursiveTask<List<T>> {
    private final T bean;
    private final List<String> keys;
    private final boolean isPartialKey;
    private final long maxResults;


    public RedisFetchTask(List<String> rowKeys, T bean, boolean isPartialKey, long maxResults) {
        this.keys = rowKeys;
        this.bean = bean;
        this.isPartialKey = isPartialKey;
        this.maxResults = maxResults;
    }

    @Override
    protected List<T> compute() {

        return fetch(keys, bean);
    }

    private List<T> fetch(List<String> keys, T bean) {
        if (keys.size() < 2) {
            List<T> fullResult = new ArrayList<>();
            for (String k:keys) {
                fullResult.addAll(fetch(k, bean));
            }
            return fullResult;
        }

        List<T> fullResult = new ArrayList<>();

        List<ForkJoinTask<List<T>>> taskList = new ArrayList<>();
        for (String rowKey : keys) {
            ForkJoinTask<List<T>> fjTask = new RedisFetchTask<>(Collections.singletonList(rowKey), bean, isPartialKey, maxResults).fork();
            taskList.add(fjTask);
        }

        for (ForkJoinTask<List<T>> forkJoinTask : taskList) {
            fullResult.addAll(forkJoinTask.join());
        }

        return fullResult;
    }

    private List<T> fetch(String key, T bean) {
        try (Jedis connection = RedisConnectionManager.getConnection("localhost")) {
            String result = connection.get(key);

            T resultObj = new Gson().fromJson(result, (Type) bean.getClass());
            return SingletonList.newSingletonList(resultObj);
        }
    }
}
