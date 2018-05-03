package com.sudhirt.api.rest.controller;

import com.sudhirt.api.rest.entity.Address;
import com.sudhirt.api.rest.entity.Customer;
import com.sudhirt.api.rest.exception.DataValidationException;
import com.sudhirt.api.rest.service.AddressService;
import com.sudhirt.api.rest.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@Validated
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/customers")
    public Collection<Customer> getAllCustomers() {
        return customerService.getAll();
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable String id) {
        return customerService.getById(id);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody @Valid Customer customer) {
        return customerService.save(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable String id, @RequestBody @NotNull Customer customer) {
        if(customer.isEmpty()) {
            throw new DataValidationException("Empty request body is provided.");
        }
        Customer dbCustomer = customerService.getById(id);
        if (customer.getSalutation() != null) {
            dbCustomer.setSalutation(customer.getSalutation());
        }
        if (customer.getFirstName() != null) {
            dbCustomer.setFirstName(customer.getFirstName());
        }
        if (customer.getLastName() != null) {
            dbCustomer.setLastName(customer.getLastName());
        }
        if (customer.getDateOfBirth() != null) {
            dbCustomer.setDateOfBirth(customer.getDateOfBirth());
        }
        return customerService.save(dbCustomer);
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createCustomer(@PathVariable String id) {
        customerService.delete(id);
    }

    @GetMapping("/customers/{customerId}/addresses")
    public Collection<Address> getCustomerAddresses(@PathVariable String customerId) {
        return addressService.getByCustomerId(customerId);
    }

    @GetMapping("/customers/{customerId}/addresses/{addressId}")
    public Address getCustomerAddressById(@PathVariable String customerId, @PathVariable String addressId) {
        return addressService.getByIdAndCustomerId(addressId, customerId);
    }

    @PostMapping("/customers/{customerId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public Address createAddress(@PathVariable String customerId, @RequestBody @Valid Address address) {
        return addressService.save(customerId, address);
    }

    @PutMapping("/customers/{customerId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAddress(@PathVariable String customerId, @PathVariable String addressId, @RequestBody @NotNull Address address) {
        Address dbAddress = addressService.getByIdAndCustomerId(customerId, addressId);
        if(address.getAddress1() != null) {
            dbAddress.setAddress1(address.getAddress1());
        }
        if(address.getAddress2() != null) {
            dbAddress.setAddress2(address.getAddress2());
        }
        if(address.getAddressType() != null) {
            dbAddress.setAddressType(address.getAddressType());
        }
        if(address.getCity() != null) {
            dbAddress.setCity(address.getCity());
        }
        if(address.getState() != null) {
            dbAddress.setState(address.getState());
        }
        if(address.getCountry() != null) {
            dbAddress.setCountry(address.getCountry());
        }
        if(address.getZipcode() != null) {
            dbAddress.setZipcode(address.getZipcode());
        }
        addressService.save(customerId, dbAddress);
    }

    @DeleteMapping("/customers/{customerId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable String customerId, @PathVariable String addressId) {
        addressService.deleteByIdAndCustomerId(addressId, customerId);
    }
}
