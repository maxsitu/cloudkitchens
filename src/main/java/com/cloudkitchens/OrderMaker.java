package com.cloudkitchens;

import com.cloudkitchens.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class OrderMaker{
    private static Logger log = LoggerFactory.getLogger(OrderMaker.class);

    private final long sleepMillis;
    private final OrderManager orderManager;

    public OrderMaker(long sleepMillis, OrderManager orderManager) {
        this.sleepMillis = sleepMillis;
        this.orderManager = orderManager;
    }

    public void startMakingOrder(Iterator<Order> orderIterator) throws InterruptedException {
        Thread t = new Thread(() -> {
            long startTimeMillis = System.currentTimeMillis();
            int count = 0;
            while (orderIterator.hasNext()) {
                try {
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Order order = orderIterator.next();

                orderManager.addOrder(order);
                count++;

            }

            long timeUsedMillis = System.currentTimeMillis() - startTimeMillis;
            log.info("Totally {} orders made, time usage {} seconds", count, timeUsedMillis/100);
        });

        t.start();
        t.join();
    }
}
