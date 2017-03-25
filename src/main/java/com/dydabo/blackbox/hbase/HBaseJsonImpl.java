/*
 * Copyright (C) 2017 viswadas leher <vleher@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dydabo.blackbox.hbase;

import com.dydabo.blackbox.BlackBox;
import com.dydabo.blackbox.BlackBoxException;
import com.dydabo.blackbox.BlackBoxable;
import com.dydabo.blackbox.db.HBaseConnectionManager;
import com.dydabo.blackbox.hbase.tasks.HBaseDeleteTask;
import com.dydabo.blackbox.hbase.tasks.HBaseFetchTask;
import com.dydabo.blackbox.hbase.tasks.HBaseInsertTask;
import com.dydabo.blackbox.hbase.tasks.HBaseSearchTask;
import com.dydabo.blackbox.hbase.utils.HBaseUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;

/**
 *
 * @author viswadas leher <vleher@gmail.com>
 * @param <T>
 */
public class HBaseJsonImpl<T extends BlackBoxable> implements BlackBox<T> {

    private final Configuration config;

    /**
     *
     * @throws IOException
     */
    public HBaseJsonImpl() throws IOException {
        this.config = HBaseConfiguration.create();
    }

    /**
     *
     * @param config
     *
     * @throws java.io.IOException
     */
    public HBaseJsonImpl(Configuration config) throws IOException {
        this.config = config;
    }

    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public Connection getConnection() throws IOException {
        return HBaseConnectionManager.getConnection(config);
    }

    @Override
    public boolean delete(List<T> rows) throws BlackBoxException {
        boolean successFlag = true;
        createTable(rows);
        try {
            ForkJoinPool fjPool = ForkJoinPool.commonPool();
            HBaseDeleteTask<T> deleteJob = new HBaseDeleteTask<>(getConnection(), rows);
            Boolean flag = fjPool.invoke(deleteJob);
            successFlag = successFlag && flag;
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return successFlag;
    }

    @Override
    public boolean insert(List<T> rows) throws BlackBoxException {
        boolean successFlag = true;
        createTable(rows);
        ForkJoinPool fjPool = ForkJoinPool.commonPool();
        try {
            HBaseInsertTask<T> insertJob = new HBaseInsertTask<>(getConnection(), rows, true);
            boolean flag = fjPool.invoke(insertJob);
            successFlag = successFlag && flag;
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return successFlag;
    }

    @Override
    public List<T> search(List<T> rows) throws BlackBoxException {
        List<T> combinedResults = new ArrayList<>();
        createTable(rows);
        ForkJoinPool fjPool = ForkJoinPool.commonPool();
        try {
            HBaseSearchTask<T> fetchTask = new HBaseSearchTask<>(getConnection(), rows);
            return fjPool.invoke(fetchTask);
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return combinedResults;
    }

    @Override
    public List<T> fetch(List<String> rowKeys, T row) throws BlackBoxException {
        ForkJoinPool fjPool = ForkJoinPool.commonPool();
        try {
            HBaseFetchTask<T> fetchTask = new HBaseFetchTask<>(getConnection(), rowKeys, row, false);
            return fjPool.invoke(fetchTask);
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public List<T> fetchByPartialKey(List<String> rowKeys, T bean) throws BlackBoxException {
        ForkJoinPool fjPool = ForkJoinPool.commonPool();
        try {
            HBaseFetchTask<T> fetchTask = new HBaseFetchTask<>(getConnection(), rowKeys, bean, true);
            return fjPool.invoke(fetchTask);
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean update(List<T> rows) throws BlackBoxException {
        boolean successFlag = true;
        createTable(rows);
        ForkJoinPool fjPool = ForkJoinPool.commonPool();
        try {
            HBaseInsertTask<T> insertJob = new HBaseInsertTask<>(getConnection(), rows, false);
            boolean flag = fjPool.invoke(insertJob);
            successFlag = successFlag && flag;
        } catch (IOException ex) {
            Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return successFlag;
    }

    @Override
    public boolean delete(T row) throws BlackBoxException {
        return delete(Arrays.asList(row));
    }

    @Override
    public boolean insert(T row) throws BlackBoxException {
        return insert(Arrays.asList(row));
    }

    @Override
    public List<T> search(T row) throws BlackBoxException {
        return search(Arrays.asList(row));
    }

    @Override
    public List<T> fetch(String rowKey, T bean) throws BlackBoxException {
        return fetch(Arrays.asList(rowKey), bean);
    }

    @Override
    public List<T> fetchByPartialKey(String rowKey, T bean) throws BlackBoxException {
        return fetchByPartialKey(Arrays.asList(rowKey), bean);
    }

    @Override
    public boolean update(T newRow) throws BlackBoxException {
        return update(Arrays.asList(newRow));
    }

    /**
     *
     * @param rows
     *
     * @throws BlackBoxException
     */
    protected void createTable(List<T> rows) throws BlackBoxException {
        if (rows.size() > 0) {
            try {
                new HBaseUtils<T>().createTable(rows.get(0), getConnection());
            } catch (IOException ex) {
                Logger.getLogger(HBaseJsonImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new BlackBoxException(ex.getMessage());
            }
        }
    }
}
