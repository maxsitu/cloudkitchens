package com.cloudkitchens.model;

import com.cloudkitchens.strategy.ShelfStrategy;
import static com.cloudkitchens.model.Order.Temp.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShelfTest {
    @Mock
    private ShelfStrategy shelfStrategy;

    private Shelf shelf;

    @Before
    public void init() {
        shelf = new Shelf(1, shelfStrategy);
    }

    @Test
    public void testAddOrder() {

        Order order1 = new Order("order 1", hot, 100, .8);
        Order order2 = new Order("order 2", cold, 100, .7);

        when(shelfStrategy.getShelfType()).thenReturn(ShelfType.hot);
        when(shelfStrategy.addOrder(any(Order.class), anyLong())).thenReturn(mock(ShelfStrategy.InternalOrder.class));
        shelf.addOrder(order1, 0L);

        when(shelfStrategy.peekExpiredOrder(anyLong())).thenReturn(Optional.empty());
        assertFalse(shelf.addOrder(order2, 0L) );
        when(shelfStrategy.peekExpiredOrder(anyLong())).thenReturn(Optional.of(mock(ShelfStrategy.InternalOrder.class)));
        assertTrue(shelf.addOrder(order2, 1L) );
    }

    @Test
    public void testRemoveOrder() {
        Order order1 = new Order("order 1", hot, 100, .8);
        when(shelfStrategy.addOrder(any(Order.class), anyLong())).thenReturn(mock(ShelfStrategy.InternalOrder.class));
        shelf.addOrder(order1, 0L);

        shelf.removeOrder(order1);
        verify(shelfStrategy, times(1)).removeOrder(order1);
    }

    @Test
    public void testGetInternalOrder() {
        Order order1 = new Order("order 1", hot, 100, .8);
        when(shelfStrategy.addOrder(any(Order.class), anyLong())).thenReturn(mock(ShelfStrategy.InternalOrder.class));
        shelf.addOrder(order1, 0L);

        ShelfStrategy.InternalOrder internalOrder = shelf.getInternalOrder(order1);
        verify(shelfStrategy, times(1)).getInternalOrder(order1);
    }
}
