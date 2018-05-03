package com.sudhirt.api.rest.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sudhirt.api.rest.controller.utils.WireMockRecordingInitializer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/before.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/after.sql")
public class WireMockGeneratorIT {

    private static WireMockServer wireMockServer;

    @LocalServerPort
    private int port;

    @AfterClass
    public static void teardown() {
        WireMockRecordingInitializer.teardown(wireMockServer);
    }

    @Before
    public void initialize() throws IOException {
        if (wireMockServer == null) {
            wireMockServer = WireMockRecordingInitializer.initialize(port);
        }
    }

    @Test
    public void getCustomers() {
        given().port(port + 1)
                .when().get("/customers")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer1() {
        given().port(port + 1)
                .when().get("/customers/1")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer1Addresses() {
        given().port(port + 1)
                .when().get("/customers/1/addresses")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer1Address1() {
        given().port(port + 1)
                .when().get("/customers/1/addresses/1")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer1Address2() {
        given().port(port + 1)
                .when().get("/customers/1/addresses/2")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer1Address3() {
        given().port(port + 1)
                .when().get("/customers/1/addresses/3")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer2() {
        given().port(port + 1)
                .when().get("/customers/2")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer2Addresses() {
        given().port(port + 1)
                .when().get("/customers/2/addresses")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer2Address1() {
        given().port(port + 1)
                .when().get("/customers/2/addresses/4")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer3() {
        given().port(port + 1)
                .when().get("/customers/3")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer3Addresses() {
        given().port(port + 1)
                .when().get("/customers/3/addresses")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer3Address1() {
        given().port(port + 1)
                .when().get("/customers/3/addresses/5")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer4Addresses() {
        given().port(port + 1)
                .when().get("/customers/4/addresses")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer4Address1() {
        given().port(port + 1)
                .when().get("/customers/4/addresses/6")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer5Addresses() {
        given().port(port + 1)
                .when().get("/customers/4/addresses")
                .then().statusCode(200);
    }

    @Test
    public void getCustomer5Address1() {
        given().port(port + 1)
                .when().get("/customers/5/addresses/1")
                .then().statusCode(404);
    }
}
