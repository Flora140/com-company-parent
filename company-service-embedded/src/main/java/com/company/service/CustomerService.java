package com.company.service;

import java.util.Set;

/**
 *
 * @author hmhlongo
 */
public interface CustomerService {
    
    public Customer fetchCustomer(Integer id);

    public Set<Customer> fetchCustomers();

    public Customer createCustomer(Customer customer);

    public Customer deleteCustomer(Integer id);
    
}
