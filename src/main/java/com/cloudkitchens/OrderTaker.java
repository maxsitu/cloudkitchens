package com.cloudkitchens;

import com.cloudkitchens.model.Order;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrderTaker extends Thread {
    private static Logger log = LoggerFactory.getLogger(OrderTaker.class);
    private static AtomicBoolean isRunning = new AtomicBoolean(true);

    private final OrderManager orderManager;
    private final PoissonDistribution pDistribution;
    private int count = 0;

    public OrderTaker(double meanWaitTimeMillis, OrderManager orderManager) {
        this.orderManager = orderManager;
        this.pDistribution = new PoissonDistribution(meanWaitTimeMillis);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(pDistribution.sample());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Order order = orderManager.popOrder();

            count++;
            log.info("Order taken: {}", order);
        }
    }

    public static void stopTakers() {
        isRunning.set(false);
    }
}
