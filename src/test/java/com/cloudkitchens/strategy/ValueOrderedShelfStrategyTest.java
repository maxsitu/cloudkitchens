package com.cloudkitchens.strategy;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.ShelfType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

import static com.cloudkitchens.model.Order.Temp.hot;

@RunWith(MockitoJUnitRunner.class)
public class ValueOrderedShelfStrategyTest {

    private ShelfStrategy strategy = new ValueOrderedShelfStrategy(ShelfType.hot);

    @Test
    public void testChooseReplacingOrder() {
        Order order1 = new Order("order 1", hot, 100, 1); //21 60
        Order order2 = new Order("order 2", hot, 200, 4); // 5 100

        strategy.addOrder(order1, 0L);
        strategy.addOrder(order2, 0L);

        assertEquals(order1, strategy.chooseReplacingOrder(20));
        assertEquals(order2, strategy.chooseReplacingOrder(39));
    }
}
