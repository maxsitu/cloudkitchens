package com.cloudkitchens.model;


import com.cloudkitchens.strategy.ShelfStrategy;
import com.cloudkitchens.strategy.ShelfStrategy.InternalOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Shelf {
    private static final Logger log = LoggerFactory.getLogger(Shelf.class);

    private final int quota;
    private final ShelfStrategy shelfStrategy;
    private final PriorityQueue<ShelfStrategy.InternalOrder> orders;

    public Shelf(int quota, ShelfStrategy shelfStrategy) {
        assert quota > 0;
        this.quota = quota;
        this.shelfStrategy = shelfStrategy;
        this.orders = new PriorityQueue<>(Comparator.comparing(order -> order.orderTime));
    }

    public synchronized boolean addOrder(Order order, long orderTime){
        if (isAvailable()) {
            doAddOrder(order, orderTime);

            return true;
        } else {
            Optional<InternalOrder> expired = shelfStrategy.peekExpiredOrder(orderTime);
            if (expired.isPresent()) {
                log.info("Remove expired order. Shelf: {}, time: {}, order: {}",
                        shelfStrategy.getShelfType().name(),
                        orderTime,
                        expired.get()
                );

                removeOrder(expired.get().order);

                doAddOrder(order, orderTime);

                return true;
            }
        }
        return false;
    }

    public synchronized void removeOrder(Order order) {
        InternalOrder internalOrder = shelfStrategy.removeOrder(order);
        orders.remove(internalOrder);
    }

    private void doAddOrder(Order order, long orderTime) {
        InternalOrder internalOrder = shelfStrategy.addOrder(order, orderTime);
        orders.add(internalOrder);
    }

    public Order chooseReplacingOrder(long time) {
        return  shelfStrategy.chooseReplacingOrder(time);
    }

    private boolean isAvailable() {
        return quota > orders.size();
    }

    public InternalOrder getInternalOrder(Order order) {
        return shelfStrategy.getInternalOrder(order);
    }

    public Optional<InternalOrder> peek(long peekTime) {
        InternalOrder result = orders.peek();

        while (result!=null && result.expireTime <= peekTime) {
            log.info("Remove expired order. Shelf: {}, time: {}, order: {}",
                    shelfStrategy.getShelfType().name(),
                    peekTime,
                    result
            );
            this.removeOrder(result.order);
            result = orders.peek();
        }

        return Optional.ofNullable(result);
    }
}
