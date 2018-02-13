package com.company.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

/**
 *
 * @author hmhlongo
 */
@Dependent
public class CustomerServiceImpl implements CustomerService {
    
    private Database database;

    @PostConstruct
    protected void create() {
        database = new Database();
    }
    
    @Override
    public Customer fetchCustomer(Integer id) {
        return database.fetchCustomer(id);
    }

    @Override
    public Set<Customer> fetchCustomers() {
        return database.fetchCustomers();
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return database.createCustomer(customer);
    }

    @Override
    public Customer deleteCustomer(Integer id) {
        return database.deleteCustomer(id);
    }
}


class Database {

    private static final Logger L = Logger.getLogger(Database.class.getName());
    private static final Set<Customer> FAKE_CUSTOMERS = new HashSet<>();

    static {
        FAKE_CUSTOMERS.add(new Customer(1, "Hlulani Mhlongo", "hmhlongo@novalinc.net", true));
        FAKE_CUSTOMERS.add(new Customer(2, "Simon Cal", "simaon@gmail.com", true));
        FAKE_CUSTOMERS.add(new Customer(3, "Binjy Shilubane", "binjys@gmail.com", true));
        FAKE_CUSTOMERS.add(new Customer(4, "Lunghile Rikhotso", "lunghi@hotmail", true));
        FAKE_CUSTOMERS.add(new Customer(5, "Hazel Nutz", "hazelnutz@hotmail", true));
        FAKE_CUSTOMERS.add(new Customer(6, "John Cena", "cena@hotmail", true));
    }

    public Customer fetchCustomer(Integer id) {
        for (Customer c : FAKE_CUSTOMERS) {
            if (Objects.equals(c.getId(), id)) {
                return c;
            }
        }
        throw new UnsupportedOperationException("Cannot get user " + id);
    }

    public Set<Customer> fetchCustomers() {
        return FAKE_CUSTOMERS;
    }

    public Customer createCustomer(Customer customer) {

        int newId = 0;
        for (Customer c : FAKE_CUSTOMERS) {
            if (c.getId() > newId) {
                newId = c.getId();
            }
        }
        customer.setId(newId + 1);
        FAKE_CUSTOMERS.add(customer);
        L.log(Level.INFO, "Created new customer {0}", customer);
        return customer;
    }

    public Customer deleteCustomer(Integer id) {
        Iterator<Customer> it = FAKE_CUSTOMERS.iterator();
        while (it.hasNext()) {
            Customer c = it.next();
            if (Objects.equals(c.getId(), id)) {
                it.remove();
                return c;
            }
        }

        throw new UnsupportedOperationException("Cannot delete user " + id);
    }
}