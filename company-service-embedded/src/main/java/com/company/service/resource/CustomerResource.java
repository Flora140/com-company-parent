package com.company.service.resource;

import com.company.service.Customer;
import com.company.service.CustomerService;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hmhlongo
 */
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static final Logger L = LoggerFactory.getLogger(CustomerResource.class);
    
    @Inject
    private CustomerService service;

    @Context
    private SecurityContext sc;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Customer create(Customer customer) {
        L.info("'{}' is creatomg {}", sc.getUserPrincipal(), customer);
        return service.createCustomer(customer);
    }

    @GET
    @Path("/{id}")
    public Customer fetch(@PathParam("id") int id) {
        L.info("'{}' is fetching {}", sc.getUserPrincipal(), id);
        return service.fetchCustomer(id);
    }

    @DELETE
    @Path("/{id}")
    public Customer delete(@PathParam("id") int id) {
        L.info("'{}' is deleting {}", sc.getUserPrincipal(), id);
        return service.deleteCustomer(id);
    }

    @GET
    @Path("/list")
    public Set<Customer> fetchList() {
        L.info("'{}' asked for the list", sc.getUserPrincipal());
        return service.fetchCustomers();
    }
}

