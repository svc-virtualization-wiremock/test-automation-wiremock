package com.sudhirt.api.rest.service;

import com.sudhirt.api.rest.constant.AddressType;
import com.sudhirt.api.rest.entity.Address;
import com.sudhirt.api.rest.entity.Customer;
import com.sudhirt.api.rest.exception.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/before.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/after.sql")
public class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Test
    public void getAddressByCustomer() {
        List<Address> addressList = addressService.getByCustomerId("1");
        assertThat(addressList).hasSize(3);
    }

    @Test
    public void getAddressByInvalidCustomer() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> addressService.getByCustomerId("100"));
    }

    @Test
    public void getAddressByInvalidId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> addressService.getById("100"));
    }

    @Test
    public void getAddressByIdAndCustomerId() {
        Address address = addressService.getByIdAndCustomerId("1", "1");
        assertThat(address).isNotNull();
    }

    @Test
    public void getAddressByIdAndUnrelatedCustomerId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> addressService.getByIdAndCustomerId("1", "5"));
    }

    @Test
    public void getAddressByInvalidIdAndCustomerId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> addressService.getByIdAndCustomerId("200", "1"));
    }

    @Test
    public void getAddressByInvalidIdAndInvalidCustomerId() {
        assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> addressService.getByIdAndCustomerId("200", "1000"));
    }

    @Test
    @Transactional
    public void createAddress() {
        Address address = Address.builder()
                .id("100")
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .addressType(AddressType.OFFICE)
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();
        Address dbAddress = addressService.save("15", address);
        dbAddress = addressService.getById(dbAddress.getId());
        assertThat(dbAddress.getId()).isEqualTo(address.getId());
        assertThat(dbAddress.getAddress1()).isEqualTo(address.getAddress1());
        assertThat(dbAddress.getAddress2()).isEqualTo(address.getAddress2());
        assertThat(dbAddress.getAddressType()).isEqualTo(address.getAddressType());
        assertThat(dbAddress.getCity()).isEqualTo(address.getCity());
        assertThat(dbAddress.getState()).isEqualTo(address.getState());
        assertThat(dbAddress.getCountry()).isEqualTo(address.getCountry());
        assertThat(dbAddress.getZipcode()).isEqualTo(address.getZipcode());
    }

    @Test
    @Transactional
    public void updateAddress() {
        Address address = Address.builder()
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .addressType(AddressType.OFFICE)
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();
        Address dbAddress = addressService.save("15", address);
        dbAddress = addressService.getById(dbAddress.getId());
        dbAddress.setAddress1("ADDRESS1 UPDATED");
        addressService.save("15", dbAddress);
        dbAddress = addressService.getById(dbAddress.getId());
        assertThat(dbAddress.getId()).isEqualTo(address.getId());
        assertThat(dbAddress.getAddress1()).isEqualTo("ADDRESS1 UPDATED");
        assertThat(dbAddress.getAddress2()).isEqualTo(address.getAddress2());
        assertThat(dbAddress.getAddressType()).isEqualTo(address.getAddressType());
        assertThat(dbAddress.getCity()).isEqualTo(address.getCity());
        assertThat(dbAddress.getState()).isEqualTo(address.getState());
        assertThat(dbAddress.getCountry()).isEqualTo(address.getCountry());
        assertThat(dbAddress.getZipcode()).isEqualTo(address.getZipcode());
    }
}
