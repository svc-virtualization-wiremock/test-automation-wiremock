package com.sudhirt.api.rest.service;

import com.sudhirt.api.rest.entity.Customer;
import com.sudhirt.api.rest.exception.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/before.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/after.sql")
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    public void getCustomers() {
        Collection<Customer> customerList = customerService.getAll();
        assertThat(customerList).hasSize(20);
    }

    @Test
    public void getCustomerById() {
        Customer customer = customerService.getById("1");
        assertThat(customer).isNotNull();
    }

    @Test
    public void getCustomerByInvalidId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> customerService.getById("100"));
    }

    @Test
    @Transactional
    public void createCustomer() {
        Customer customer = Customer.builder()
                .firstName("FIRST_NAME1")
                .lastName("LAST_NAME1")
                .salutation("Mr.")
                .dateOfBirth(new Date())
                .build();
        customerService.save(customer);
        Customer dbCustomer = customerService.getById(customer.getId());
        assertThat(dbCustomer).isEqualTo(customer);
    }

    @Test
    @Transactional
    public void updateCustomer() {
        Customer customer = Customer.builder()
                .firstName("FIRST_NAME1")
                .lastName("LAST_NAME1")
                .salutation("Mr.")
                .dateOfBirth(new Date())
                .build();
        customer = customerService.save(customer);
        customer.setFirstName("FirstName_Updated");
        customerService.save(customer);
        Customer dbCustomer = customerService.getById(customer.getId());
        assertThat(dbCustomer.getFirstName()).isEqualTo(customer.getFirstName());
    }

    @Test
    @Transactional
    public void deleteCustomerById() {
        customerService.delete("10");
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> customerService.getById("10"));
    }

    @Test
    @Transactional
    public void deleteCustomerByInvalidId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> customerService.delete("100"));
    }
}
