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
package com.dydabo.blackbox.usecase;

import com.dydabo.blackbox.BlackBox;
import com.dydabo.blackbox.BlackBoxException;
import com.dydabo.blackbox.BlackBoxFactory;
import com.dydabo.blackbox.BlackBoxable;
import com.dydabo.blackbox.beans.Customer;
import com.dydabo.blackbox.beans.Employee;
import com.dydabo.blackbox.beans.User;
import com.dydabo.blackbox.utils.DyDaBoTestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author viswadas leher
 */
public class SimpleUseCase {

    private static final Logger logger = Logger.getLogger(SimpleUseCase.class.getName());

    private BlackBox cassandraInstance;
    private BlackBox hbaseInstance;
    private BlackBox mongoInstance;
    private BlackBox redisInstance;

    private final DyDaBoTestUtils utils = new DyDaBoTestUtils();
    private final Random random = new Random();


    /**
     * @throws IOException
     */
    public SimpleUseCase() throws IOException {
        if (utils.dbToTest.contains(BlackBoxFactory.HBASE)) {
            hbaseInstance = BlackBoxFactory.getDatabase(BlackBoxFactory.HBASE);
        }
        if (utils.dbToTest.contains(BlackBoxFactory.CASSANDRA)) {
            cassandraInstance = BlackBoxFactory.getCassandraDatabase();
        }
        if (utils.dbToTest.contains(BlackBoxFactory.MONGODB)) {
            mongoInstance = BlackBoxFactory.getMongoDatabase();
        }
        if (utils.dbToTest.contains(BlackBoxFactory.REDIS)) {
            redisInstance = BlackBoxFactory.getRedisDatabase();
        }
    }

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    /**
     * @throws Exception
     */
    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    protected void testTaxRates() throws BlackBoxException {
        int testSize = 2;
        List<Customer> userList = utils.generateCustomers(testSize);
        if (hbaseInstance != null) {
            hbaseInstance.update(userList);
        }
        if (cassandraInstance != null) {
            cassandraInstance.update(userList);
        }
        if (mongoInstance != null) {
            mongoInstance.update(userList);
        }
        // Search tax rates
        Double tRate = userList.get(random.nextInt(99) % userList.size()).getTaxRate();
        Double minTaxRate = tRate * 0.5;
        Double maxTaxRate = tRate + minTaxRate;
        Customer startCustomer = new Customer(null, null);
        startCustomer.setTaxRate(minTaxRate);
        Customer endCustomer = new Customer(null, null);
        endCustomer.setTaxRate(maxTaxRate);
        if (hbaseInstance != null) {
            List<Customer> taxRateCust = hbaseInstance.search(startCustomer, endCustomer);
            Assert.assertTrue(taxRateCust.size() > 0);
            for (Customer customer : taxRateCust) {
                if (customer.getTaxRate() != null) {
                    Assert.assertTrue(customer.getTaxRate() >= minTaxRate);
                    Assert.assertTrue(customer.getTaxRate() < maxTaxRate);
                }
            }
        }

        if (cassandraInstance != null) {
            List<Customer> taxRateCust = cassandraInstance.search(startCustomer, endCustomer);
            Assert.assertTrue(taxRateCust.size() > 0);
            for (Customer customer : taxRateCust) {
                if (customer.getTaxRate() != null) {
                    Assert.assertTrue(customer.getTaxRate() >= minTaxRate);
                    Assert.assertTrue(customer.getTaxRate() < maxTaxRate);
                }
            }
        }

        if (mongoInstance != null) {
            List<Customer> taxRateCust = mongoInstance.search(startCustomer, endCustomer);
            Assert.assertTrue(taxRateCust.size() > 0);
            for (Customer customer : taxRateCust) {
                if (customer.getTaxRate() != null) {
                    Assert.assertTrue(customer.getTaxRate() >= minTaxRate);
                    Assert.assertTrue(customer.getTaxRate() < maxTaxRate);
                }
            }
        }
    }

    /**
     * @throws BlackBoxException
     */
    @Test
    public void testUseCaseOne() throws BlackBoxException {
        int testSize = 3;
        // Update new Users
        List<Customer> userList = utils.generateCustomers(testSize);
        // Delete Users
        List<Customer> delUserList = utils.generateCustomers(testSize);


        if (hbaseInstance != null) {
            boolean success = hbaseInstance.update(userList);
            Assert.assertTrue(success);

            success = hbaseInstance.delete(delUserList);
            Assert.assertTrue(success);
        }

        if (cassandraInstance != null) {
            boolean success = cassandraInstance.update(userList);
            Assert.assertTrue(success);

            success = cassandraInstance.delete(delUserList);
            Assert.assertTrue(success);
        }


        if (mongoInstance != null) {
            boolean success = mongoInstance.update(userList);
            Assert.assertTrue(success);

            success = mongoInstance.delete(delUserList);
            Assert.assertTrue(success);
        }

        if (redisInstance != null) {
            boolean success = redisInstance.update(userList);
            Assert.assertTrue(success);

            success = redisInstance.delete(delUserList);
            Assert.assertTrue(success);
        }
    }

    /**
     * @throws BlackBoxException
     */
    @Test
    public void testUseCaseTwo() throws BlackBoxException {
        int testSize = 1;
        // Update new Users
        List<Employee> userList = utils.generateEmployees(testSize);
        // select random key
        String userName = userList.get(random.nextInt(99) % userList.size()).getUserName();

        // Search
        List<BlackBoxable> hbaseList = new ArrayList<>();
        hbaseList.add(new Employee(null, userName));
        hbaseList.add(new Customer(null, userName));

        if (hbaseInstance != null) {
            boolean success = hbaseInstance.update(userList);
            Assert.assertTrue(success);

            List<BlackBoxable> searchResult = hbaseInstance.search(hbaseList);
            logger.info("HBASE Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.contains(userName)) {
                        Assert.fail("HBASE Does not contain  " + userName + " :" + res);
                    }
                }
            }
        }


        if (cassandraInstance != null) {
            boolean success = cassandraInstance.update(userList);
            Assert.assertTrue(success);

            List<BlackBoxable> cassList = new ArrayList<>();
            cassList.add(new Employee(null, userName));
            cassList.add(new Customer(null, userName));
            List<BlackBoxable> searchResult = cassandraInstance.search(cassList);
            logger.info("CASS Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userName)) {
                        Assert.fail("CASS Does not start with " + userName + res);
                    }
                }
            }
        }

        if (mongoInstance != null) {
            boolean success = mongoInstance.update(userList);
            Assert.assertTrue(success);

            List<BlackBoxable> cassList = new ArrayList<>();
            cassList.add(new Employee(null, userName));
            cassList.add(new Customer(null, userName));
            List<BlackBoxable> searchResult = mongoInstance.search(cassList);
            logger.info("Mongo Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userName)) {
                        Assert.fail("MONGO Does not start with " + userName + res);
                    }
                }
            }
        }

        if (redisInstance != null) {
            boolean success = redisInstance.update(userList);
            Assert.assertTrue(success);

            List<BlackBoxable> redisList = new ArrayList<>();
            redisList.add(new Employee(null, userName));
            redisList.add(new Customer(null, userName));

            List<BlackBoxable> searchResults = redisInstance.search(redisList);
            Assert.assertTrue(searchResults.size()>0);

            for (BlackBoxable res : searchResults) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userName)) {
                        Assert.fail("REDIS Does not start with " + userName + res);
                    }
                }
            }
        }

        // Search
        hbaseList = new ArrayList<>();
        final String userPrefix = userName.substring(0, 3);
        hbaseList.add(new Employee(null, "^" + userPrefix + ".*"));
        hbaseList.add(new Customer(null, "^" + userPrefix + ".*"));
        List<BlackBoxable> searchResult;
        if (hbaseInstance != null) {
            searchResult = hbaseInstance.search(hbaseList);
            logger.info("HBASE Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userPrefix)) {
                        Assert.fail(" Does not start with  " + userPrefix + " :" + res);
                    }
                }
            }
        }


        List<BlackBoxable> cassList = new ArrayList<>();
        cassList.add(new Employee(null, userPrefix + ".*"));
        cassList.add(new Customer(null, userPrefix + ".*"));

        if (cassandraInstance != null) {
            searchResult = cassandraInstance.search(cassList);
            logger.info("CASS Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userPrefix)) {
                        Assert.fail(" Does not start with  " + userPrefix + " :" + res);
                    }
                }
            }
        }

        if (mongoInstance != null) {
            searchResult = mongoInstance.search(cassList);
            logger.info("Mongo Search Result :" + searchResult.size());
            Assert.assertTrue(searchResult.size() > 0);
            for (BlackBoxable res : searchResult) {
                if (res instanceof User) {
                    final String uName = ((User) res).getUserName();
                    if (uName == null || !uName.startsWith(userPrefix)) {
                        Assert.fail(" Does not start with  " + userPrefix + " :" + res);
                    }
                }
            }
        }
        // Delete Users
        userList = utils.generateEmployees(testSize);

        boolean success;
        if (hbaseInstance != null) {
            success = hbaseInstance.delete(userList);
            Assert.assertTrue(success);
        }

        if (cassandraInstance != null) {
            success = cassandraInstance.delete(userList);
            Assert.assertTrue(success);
        }

        if (mongoInstance != null) {
            success = mongoInstance.delete(userList);
            Assert.assertTrue(success);
        }

        if (redisInstance != null) {
            success = redisInstance.delete(userList);
            Assert.assertTrue(success);
        }
    }

    /**
     * @throws BlackBoxException
     */
    @Test
    public void testUseCaseThree() throws BlackBoxException {
        // Insert an unique record
        Customer cust = new Customer(new Random().nextInt(1000), "AQWERDSFSTOIOPIoioioiIIII1111");
        cust.setTaxRate(random.nextDouble() * 100);
        if (hbaseInstance != null) {
            hbaseInstance.insert(cust);
        }
        if (cassandraInstance != null) {
            cassandraInstance.insert(cust);
        }
        if (mongoInstance != null) {
            mongoInstance.insert(cust);
        }

        Customer cust1 = new Customer(new Random().nextInt(1000), "AQWERDSFSTOIOPIoioioiIIII222");
        cust1.setTaxRate(random.nextDouble() * 100);
        if (hbaseInstance != null) {
            hbaseInstance.insert(cust1);
        }
        if (cassandraInstance != null) {
            cassandraInstance.insert(cust1);
        }
        if (mongoInstance != null) {
            mongoInstance.insert(cust1);
        }

        Customer cust2 = new Customer(new Random().nextInt(1000), "AQWERDSFSTOIOPIoioioiIIII333");
        cust2.setTaxRate(random.nextDouble() * 100);
        if (hbaseInstance != null) {
            hbaseInstance.insert(cust2);
        }
        if (cassandraInstance != null) {
            cassandraInstance.insert(cust2);
        }
        if (mongoInstance != null) {
            mongoInstance.insert(cust2);
        }

        List<BlackBoxable> searchResult;
        if (hbaseInstance != null) {
            searchResult = hbaseInstance.search(cust);
            Assert.assertEquals(searchResult.size(), 1);


            searchResult = hbaseInstance.fetch(Arrays.asList(cust.getBBRowKey(), cust1.getBBRowKey(), cust2.getBBRowKey()), cust);
            Assert.assertEquals(searchResult.size(), 3);

            hbaseInstance.delete(Arrays.asList(cust, cust1, cust2));
        }
        if (cassandraInstance != null) {
            searchResult = cassandraInstance.search(cust);
            Assert.assertEquals(searchResult.size(), 1);

            searchResult = cassandraInstance.fetch(Arrays.asList(cust.getBBRowKey(), cust1.getBBRowKey(), cust2.getBBRowKey()), cust);
            Assert.assertEquals(searchResult.size(), 3);

            cassandraInstance.delete(Arrays.asList(cust, cust1, cust2));
        }

        if (mongoInstance != null) {
            searchResult = mongoInstance.search(cust);
            Assert.assertEquals(searchResult.size(), 1);

            searchResult = mongoInstance.fetch(Arrays.asList(cust.getBBRowKey(), cust1.getBBRowKey(), cust2.getBBRowKey()), cust);
            Assert.assertEquals(searchResult.size(), 3);

            mongoInstance.delete(Arrays.asList(cust, cust1, cust2));
        }


    }

    @Test
    public void testFetchPartial() throws BlackBoxException {
        final Employee employee = new Employee(null, null);

        if (hbaseInstance != null) {
            List<Employee> userList = hbaseInstance.search(employee);

            // select random key
            String key = userList.get(random.nextInt(99) % userList.size()).getBBRowKey();

            String keyQuery1 = key.substring(0, key.length() / 2) + ".*";

            List<Employee> hbaseEmps = hbaseInstance.fetchByPartialKey(keyQuery1, employee);
            Assert.assertTrue(hbaseEmps.size() > 0);
            for (Employee emp : hbaseEmps) {
                Assert.assertTrue(emp.getBBRowKey().startsWith(key), emp.toString());
            }
        }

        if (cassandraInstance != null) {
            List<Employee> userList = cassandraInstance.search(employee);

            // select random key
            String key = userList.get(random.nextInt(99) % userList.size()).getBBRowKey();

            String keyQuery1 = key.substring(0, key.length() / 2) + ".*";
            List<Employee> cassEmps = cassandraInstance.fetchByPartialKey(keyQuery1, employee);
            Assert.assertTrue(cassEmps.size() > 0);
            for (Employee emp : cassEmps) {
                Assert.assertTrue(emp.getBBRowKey().startsWith(key), emp.toString());
            }
        }

        if (mongoInstance != null) {
            List<Employee> userList = mongoInstance.search(employee);
            // select random key
            String key = userList.get(random.nextInt(99) % userList.size()).getBBRowKey();

            String keyQuery1 = key.substring(0, key.length() / 2) + ".*";
            List<Employee> mongoEmps = mongoInstance.fetchByPartialKey(keyQuery1, employee);
            Assert.assertTrue(mongoEmps.size() > 0);
            for (Employee emp : mongoEmps) {
                Assert.assertTrue(emp.getBBRowKey().startsWith(key), emp.toString());
            }
        }
    }

    /**
     * @throws BlackBoxException
     */
    @Test
    public void testDoubleSearch() throws BlackBoxException {
        final String name = "ZZZZZZZZZZZZZZZZZ";
        final int id = random.nextInt(1000);
        final double taxRate = random.nextDouble() * 100;

        Customer cust = new Customer(id, name);
        cust.setTaxRate(taxRate);
        if (hbaseInstance!= null) hbaseInstance.insert(cust);
        if (cassandraInstance!= null) cassandraInstance.insert(cust);
        if (mongoInstance!= null) mongoInstance.insert(cust);

        if (mongoInstance != null) {
            Customer searchCust = new Customer(null, name);

            List<Customer> searchResult = mongoInstance.search(searchCust);
            Assert.assertTrue(!searchResult.isEmpty());
            for (Customer customer : searchResult) {
                Assert.assertTrue(customer.getUserName().equals(name), customer.getUserName());
            }

            searchCust = new Customer(id, null);

            searchResult = mongoInstance.search(searchCust);
            Assert.assertTrue(!searchResult.isEmpty());
            for (Customer customer : searchResult) {
                Assert.assertTrue(customer.getUserId().equals(id), customer.getUserId().toString());
            }

            searchCust = new Customer(null, name);
            searchCust.setTaxRate(taxRate);

            searchResult = mongoInstance.search(searchCust);
            Assert.assertTrue(!searchResult.isEmpty());
            for (Customer customer : searchResult) {
                Assert.assertTrue(customer.getTaxRate().equals(taxRate), customer.getTaxRate().toString());
            }
        }

    }
}
