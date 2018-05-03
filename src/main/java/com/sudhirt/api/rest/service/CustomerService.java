package com.sudhirt.api.rest.service;

import com.sudhirt.api.rest.entity.Customer;
import com.sudhirt.api.rest.exception.NotFoundException;
import com.sudhirt.api.rest.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Collection<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Customer getById(String id) {
        return customerRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(String id) {
        getById(id);
        customerRepository.deleteById(id);
    }
}
