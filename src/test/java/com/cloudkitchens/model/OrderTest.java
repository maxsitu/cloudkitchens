package com.cloudkitchens.model;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderTest {

    private Gson gson = new Gson();

    @Test
    public void testOrderToJson() {
        Order order = new Order("Cheese Pizza", Order.Temp.hot, 300, 0.45);
        String orderStr = gson.toJson(order);
        assertEquals("{\"name\":\"Cheese Pizza\",\"temp\":\"hot\",\"shelfLife\":300,\"decayRate\":0.45}", orderStr);
    }

    @Test
    public void testOrderFromJson() {
        String orderStr = "{\"name\":\"Cheese Pizza\",\"temp\":\"hot\",\"shelfLife\":300,\"decayRate\":0.45}";
        Order order = gson.fromJson(orderStr, Order.class);
        assertEquals("Cheese Pizza", order.name);
        assertEquals(Order.Temp.hot, order.temp);
        assertEquals(300, order.shelfLife);
        assertEquals(0.45, order.decayRate, 0);
    }
}
