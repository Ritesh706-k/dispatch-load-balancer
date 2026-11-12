package com.ritesh.dispatch.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ritesh.dispatch.repository.OrderRepository;
import com.ritesh.dispatch.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private OrderRepository orderRepository;
    @Autowired private VehicleRepository vehicleRepository;

    @BeforeEach
    void clean() {
        orderRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    private void postJson(String url, String body) throws Exception {
        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void fullFlowProducesPlanWithUnassignedOrders() throws Exception {
        postJson("/api/dispatch/orders", """
                {"orders":[
                 {"orderId":"ORD001","latitude":28.6139,"longitude":77.2090,"address":"Connaught Place, Delhi, India","packageWeight":10,"priority":"HIGH"},
                 {"orderId":"ORD002","latitude":28.7041,"longitude":77.1025,"address":"Karol Bagh, Delhi, India","packageWeight":10,"priority":"LOW"}]}""");
        postJson("/api/dispatch/vehicles", """
                {"vehicles":[{"vehicleId":"VEH001","capacity":10,"currentLatitude":28.7041,"currentLongitude":77.1025,"currentAddress":"Karol Bagh, Delhi, India"}]}""");

        mvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders[0].orderId").value("ORD001"))
                .andExpect(jsonPath("$.unassignedOrders", hasSize(1)))
                .andExpect(jsonPath("$.unassignedOrders[0].order.orderId").value("ORD002"));
    }

    @Test
    void invalidPriorityReturns400() throws Exception {
        mvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content("""
                {"orders":[{"orderId":"X","latitude":1,"longitude":1,"address":"a","packageWeight":5,"priority":"URGENT"}]}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void negativeWeightReturns400WithFieldError() throws Exception {
        mvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content("""
                {"orders":[{"orderId":"X","latitude":1,"longitude":1,"address":"a","packageWeight":-5,"priority":"HIGH"}]}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(containsString("packageWeight")));
    }

    @Test
    void planWithNoDataReturns409() throws Exception {
        mvc.perform(get("/api/dispatch/plan")).andExpect(status().isConflict());
    }
}
