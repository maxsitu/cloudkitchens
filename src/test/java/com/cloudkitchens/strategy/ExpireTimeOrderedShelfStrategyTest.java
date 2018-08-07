package com.cloudkitchens.strategy;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.ShelfType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import static com.cloudkitchens.model.Order.Temp.hot;

@RunWith(MockitoJUnitRunner.class)
public class ExpireTimeOrderedShelfStrategyTest {

    private ShelfStrategy strategy = new ExpireTimeOrderedShelfStrategy(ShelfType.hot);

    @Test
    public void testAddOrder() {
        Order order = new Order("order 1", hot, 100, .8);
        ShelfStrategy.InternalOrder internalOrder = strategy.addOrder(order, 100L);

        assertEquals(order, internalOrder.order);
        assertEquals(100L, internalOrder.orderTime);
        assertEquals(156, internalOrder.expireTime);
    }

    @Test
    public void testRemoveOrder() {
        Order order = new Order("order 1", hot, 100, .8);
        ShelfStrategy.InternalOrder internalOrder1 = strategy.addOrder(order, 100L);
        ShelfStrategy.InternalOrder internalOrder2 = strategy.removeOrder(order);

        assertEquals(internalOrder1, internalOrder2);
    }

    @Test
    public void testPeekExpiredOrder() {
        Order order = new Order("order 1", hot, 100, .8);
        strategy.addOrder(order, 100L);

        assertFalse(strategy.peekExpiredOrder(155).isPresent());
        assertTrue(strategy.peekExpiredOrder(156).isPresent());
    }

    @Test
    public void testGetInternalOrder() {
        Order order = new Order("order 1", hot, 100, .8);
        ShelfStrategy.InternalOrder internalOrder = strategy.addOrder(order, 100L);

        assertEquals(internalOrder, strategy.getInternalOrder(order));
    }
}
