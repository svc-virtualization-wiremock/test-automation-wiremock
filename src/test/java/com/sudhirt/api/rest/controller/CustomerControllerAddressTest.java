package com.sudhirt.api.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudhirt.api.rest.constant.AddressType;
import com.sudhirt.api.rest.entity.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/before.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/after.sql")
public class CustomerControllerAddressTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getCustomerAddresses() throws Exception {
        mockMvc.perform(get("/customers/1/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    public void getCustomerAddress() throws Exception {
        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAddressesWithNonExistingCustomer() throws Exception {
        mockMvc.perform(get("/customers/100/addresses"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAddressWithNonExistingCustomer() throws Exception {
        mockMvc.perform(get("/customers/100/addresses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAddressWithInvalidAddressId() throws Exception {
        mockMvc.perform(get("/customers/1/addresses/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAddressWithNonExistingAddressId() throws Exception {
        mockMvc.perform(get("/customers/1/addresses/500"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createAddress() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/customers/6/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode savedAddress = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/customers/6/addresses/" + savedAddress.get("id").asText()))
                .andExpect(status().isOk());

    }

    @Test
    public void createAddressForNonExistingCustomer() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        mockMvc.perform(post("/customers/600/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddress() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(address.getAddressType().toString())))
                .andExpect(jsonPath("$.address1", is(address.getAddress1())))
                .andExpect(jsonPath("$.address2", is(address.getAddress2())))
                .andExpect(jsonPath("$.city", is(address.getCity())))
                .andExpect(jsonPath("$.state", is(address.getState())))
                .andExpect(jsonPath("$.country", is(address.getCountry())))
                .andExpect(jsonPath("$.zipcode", is(address.getZipcode())));
    }

    @Test
    @Transactional
    public void updateAddressType() throws Exception {
        Address address = Address.builder().addressType(AddressType.OFFICE).build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(address.getAddressType().toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateAddress1() throws Exception {
        Address address = Address.builder().address1("ADDRESS1").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is(address.getAddress1())))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateAddress2() throws Exception {
        Address address = Address.builder().address2("ADDRESS2").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is(address.getAddress2())))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateCity() throws Exception {
        Address address = Address.builder().city("CITY").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is(address.getCity())))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateState() throws Exception {
        Address address = Address.builder().state("STATE").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is(address.getState())))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateCountry() throws Exception {
        Address address = Address.builder().country("COUNTRY").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is(address.getCountry())))
                .andExpect(jsonPath("$.zipcode", is("500072")));
    }

    @Test
    @Transactional
    public void updateZipcode() throws Exception {
        Address address = Address.builder().zipcode("321654").build();

        mockMvc.perform(put("/customers/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressType", is(AddressType.RESIDENCE.toString())))
                .andExpect(jsonPath("$.address1", is("H.No. 123")))
                .andExpect(jsonPath("$.address2", is("Kukatpally")))
                .andExpect(jsonPath("$.city", is("Hyderabad")))
                .andExpect(jsonPath("$.state", is("Telangana")))
                .andExpect(jsonPath("$.country", is("India")))
                .andExpect(jsonPath("$.zipcode", is(address.getZipcode())));
    }

    @Test
    @Transactional
    public void updateAddressForNonExistingCustomer() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        mockMvc.perform(put("/customers/100/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddressForInvalidCustomer() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        mockMvc.perform(put("/customers/1/addresses/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddressWithInvalidAddressId() throws Exception {
        Address address = Address.builder()
                .addressType(AddressType.OFFICE)
                .address1("ADDRESS1")
                .address2("ADDRESS2")
                .city("CITY")
                .state("STATE")
                .country("COUNTRY")
                .zipcode("123456")
                .build();

        mockMvc.perform(put("/customers/1/addresses/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteAddress() throws Exception {
        mockMvc.perform(delete("/customers/1/addresses/1"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/customers/1/addresses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteAddressWithNonExistingAddressId() throws Exception {
        mockMvc.perform(delete("/customers/1/addresses/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteAddressWithInvalidAddressId() throws Exception {
        mockMvc.perform(delete("/customers/1/addresses/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteAddressWithNonExistingCustomerId() throws Exception {
        mockMvc.perform(delete("/customers/100/addresses/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteAddressWithInvalidCustomerId() throws Exception {
        mockMvc.perform(delete("/customers/5/addresses/1"))
                .andExpect(status().isNotFound());
    }
}
