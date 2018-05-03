package com.sudhirt.api.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudhirt.api.rest.entity.Customer;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
public class CustomerControllerTest {

    private static String YYYY_MM_DD = "yyyy-MM-dd";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(20)));
    }

    @Test
    public void getCustomerById() throws Exception {
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mr.")))
                .andExpect(jsonPath("$.firstName", is("FIRST_NAME1")))
                .andExpect(jsonPath("$.lastName", is("LAST_NAME1")))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(new Date()))));
    }

    @Test
    public void getCustomerByInvalidId() throws Exception {
        mockMvc.perform(get("/customers/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void createCustomer() throws Exception {
        Customer customer = Customer.builder().firstName("FIRST_NAME100").lastName("LAST_NAME100").dateOfBirth(new Date()).salutation("Mrs.").build();
        MvcResult mvcResult = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode savedCustomer = objectMapper.readTree(mvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/customers/" + savedCustomer.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mrs.")))
                .andExpect(jsonPath("$.firstName", is("FIRST_NAME100")))
                .andExpect(jsonPath("$.lastName", is("LAST_NAME100")))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(new Date()))));
    }

    @Test
    @Transactional
    public void createCustomerWithoutSalutation() throws Exception {
        Customer customer = Customer.builder().firstName("FIRST_NAME100").lastName("LAST_NAME100").dateOfBirth(new Date()).build();
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createCustomerWithoutFirstName() throws Exception {
        Customer customer = Customer.builder().salutation("Mrs.").lastName("LAST_NAME100").dateOfBirth(new Date()).build();
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createCustomerWithoutLastName() throws Exception {
        Customer customer = Customer.builder().salutation("Mrs.").firstName("FIRST_NAME100").dateOfBirth(new Date()).build();
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createCustomerWithoutDateOfBirth() throws Exception {
        Customer customer = Customer.builder().salutation("Mrs.").firstName("FIRST_NAME100").lastName("LAST_NAME100").build();
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createCustomerWithoutRequestBody() throws Exception {
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createCustomerWithEmptyRequestBody() throws Exception {
        Customer customer = Customer.builder().build();
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateCustomerWithoutRequestBody() throws Exception {
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateCustomerWithEmptyRequestBody() throws Exception {
        Customer customer = Customer.builder().build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateCustomerDetails() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.JANUARY, 1);
        Date date = calendar.getTime();
        Customer customer = Customer.builder().salutation("Mrs.").firstName("FirstName").lastName("LastName").dateOfBirth(date).build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is(customer.getSalutation())))
                .andExpect(jsonPath("$.firstName", is(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(customer.getLastName())))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(customer.getDateOfBirth()))));
    }

    @Test
    @Transactional
    public void updateCustomerFirstName() throws Exception {
        Customer customer = Customer.builder().firstName("FirstName").build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mr.")))
                .andExpect(jsonPath("$.firstName", is(customer.getFirstName())))
                .andExpect(jsonPath("$.lastName", is("LAST_NAME1")))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(new Date()))));
    }

    @Test
    @Transactional
    public void updateCustomerLastName() throws Exception {
        Customer customer = Customer.builder().lastName("LastName").build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mr.")))
                .andExpect(jsonPath("$.firstName", is("FIRST_NAME1")))
                .andExpect(jsonPath("$.lastName", is(customer.getLastName())))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(new Date()))));
    }

    @Test
    @Transactional
    public void updateCustomerSalutation() throws Exception {
        Customer customer = Customer.builder().salutation("Mrs.").build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mrs.")))
                .andExpect(jsonPath("$.firstName", is("FIRST_NAME1")))
                .andExpect(jsonPath("$.lastName", is("LAST_NAME1")))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(new Date()))));
    }

    @Test
    @Transactional
    public void updateCustomerDateOfBirth() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.JANUARY, 1);
        Date date = calendar.getTime();
        Customer customer = Customer.builder().dateOfBirth(date).build();
        mockMvc.perform(put("/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salutation", is("Mr.")))
                .andExpect(jsonPath("$.firstName", is("FIRST_NAME1")))
                .andExpect(jsonPath("$.lastName", is("LAST_NAME1")))
                .andExpect(jsonPath("$.dateOfBirth", is(simpleDateFormat.format(customer.getDateOfBirth()))));
    }

    @Test
    @Transactional
    public void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteNonExistingCustomer() throws Exception {
        mockMvc.perform(delete("/customers/100"))
                .andExpect(status().isNotFound());
    }
}
