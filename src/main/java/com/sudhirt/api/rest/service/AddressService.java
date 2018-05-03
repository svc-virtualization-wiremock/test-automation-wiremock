package com.sudhirt.api.rest.service;

import com.sudhirt.api.rest.entity.Address;
import com.sudhirt.api.rest.entity.Customer;
import com.sudhirt.api.rest.exception.NotFoundException;
import com.sudhirt.api.rest.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerService customerService;

    public List<Address> getByCustomerId(String customerId) {
        customerService.getById(customerId);
        return addressRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Address save(String customerId, Address address) {
        Customer customer = customerService.getById(customerId);
        address.setCustomer(customer);
        return addressRepository.save(address);
    }

    public Address getById(String addressId) {
        return addressRepository.findById(addressId).orElseThrow(NotFoundException::new);
    }

    public Address getByIdAndCustomerId(String addressId, String customerId) {
        return addressRepository.getByIdAndCustomerId(addressId, customerId).orElseThrow(NotFoundException::new);
    }

    public void deleteByIdAndCustomerId(String addressId, String customerId) {
        Address address = getByIdAndCustomerId(addressId, customerId);
        addressRepository.delete(address);
    }
}
