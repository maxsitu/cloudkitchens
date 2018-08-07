package com.cloudkitchens.strategy;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.ShelfType;

import java.util.*;

public class ExpireTimeOrderedShelfStrategy extends ShelfStrategy{

    private final ShelfType shelfType;
    private final PriorityQueue<InternalOrder> expiringQueue;
    protected final Map<Order, InternalOrder> internalOrderMap;

    public ExpireTimeOrderedShelfStrategy(ShelfType shelfType) {
        this.shelfType = shelfType;
        this.expiringQueue = new PriorityQueue<>(Comparator.comparing(order -> order.expireTime));
        this.internalOrderMap = new HashMap<>();
    }

    @Override
    public synchronized InternalOrder addOrder(Order order, long orderTime) {
        InternalOrder internalOrder = new InternalOrder(order, orderTime);
        internalOrderMap.put(order, internalOrder);
        expiringQueue.offer(internalOrder);
        return internalOrder;
    }

    @Override
    public synchronized InternalOrder removeOrder(Order order) {
        InternalOrder internalOrder = internalOrderMap.remove(order);
        expiringQueue.remove(internalOrder);
        return internalOrder;
    }

    @Override
    public synchronized Order chooseReplacingOrder(long time) {
        return expiringQueue.peek().order;
    }

    @Override
    public synchronized Optional<InternalOrder> peekExpiredOrder(long expireTime) {
        InternalOrder head = expiringQueue.peek();

        if (head != null && head.expireTime <= expireTime) {
            return Optional.of(head);
        }

        return Optional.empty();
    }

    @Override
    public ShelfType getShelfType() {
        return shelfType;
    }

    @Override
    public InternalOrder getInternalOrder(Order order) {
        return internalOrderMap.get(order);
    }
}
