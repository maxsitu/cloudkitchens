package com.cloudkitchens;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.Shelf;
import com.cloudkitchens.strategy.ShelfStrategy.InternalOrder;
import com.cloudkitchens.strategy.ShelfStrategyFactory.StrategyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.BiFunction;

public class OrderManager {
    private static Logger log = LoggerFactory.getLogger(OrderManager.class);

    private final Shelf hotShelf;
    private final Shelf coldShelf;
    private final Shelf frozShelf;
    private final Shelf overShelf;
    private final StrategyType strategyType;

    public OrderManager(Shelf hotShelf,
                        Shelf coldShelf,
                        Shelf frozShelf,
                        Shelf overShelf,
                        StrategyType strategyType) {
        this.hotShelf = hotShelf;
        this.coldShelf = coldShelf;
        this.frozShelf = frozShelf;
        this.overShelf = overShelf;
        this.strategyType = strategyType;
    }

    // Try to add the new order into the shelf of temperature and overflow shelf.
    // If both shelfs are full and no expired one found, use strategy to decide
    // the existing order to be replaced.
    public synchronized void addOrder(Order order) {
        final long currTime = currentSeconds();

        final Shelf tgtShelf = targetShelf(order);

        // Program rum into this code block only when we need to decide an unexpired one to remove
        if (!tgtShelf.addOrder(order, currTime) && !overShelf.addOrder(order, currTime)) {
            Order tgtToReplace = tgtShelf.chooseReplacingOrder(currTime);
            Order overToReplace = overShelf.chooseReplacingOrder(currTime);

            if (OrderManager.genPriorThanBiFunc(currTime, strategyType).apply(
                    tgtShelf.getInternalOrder(tgtToReplace),
                    overShelf.getInternalOrder(overToReplace))) {
                tgtShelf.removeOrder(tgtToReplace);
                tgtShelf.addOrder(order, currTime);
            } else {
                overShelf.removeOrder(overToReplace);
                overShelf.addOrder(order, currTime);
            }
        }

        this.notify();
    }

    // Traverse all the shelfs, pop out the order with earlest order time.
    public synchronized Order popOrder() {

        Optional<InternalOrder> toPop = null;

        do {
            final long currTime = currentSeconds();
            Shelf toPopShelf = hotShelf;

            toPop = peekUnexpired(hotShelf, currTime);

            for ( Shelf shelf : new Shelf[]{hotShelf, coldShelf, frozShelf, overShelf}) {
                Optional<InternalOrder> peekOrder = peekUnexpired(shelf, currTime);
                if (!toPop.isPresent() ||
                        (peekOrder.isPresent() &&
                                !OrderManager
                                        .genPriorThanBiFunc(currTime, strategyType)
                                        .apply(toPop.get(), peekOrder.get()))
                        ) {
                    toPop = peekOrder;
                    toPopShelf = shelf;
                }
            }

            if (toPop.isPresent())
                toPopShelf.removeOrder(toPop.get().order);
            else {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    log.error("Exception while popping order.", e);
                }
            }

        } while(!toPop.isPresent());

        return toPop.get().order;
    }

    public static BiFunction<InternalOrder, InternalOrder, Boolean> genPriorThanBiFunc(long time, StrategyType strategyType) {
        switch (strategyType) {
            case ORDER_BY_EXPIRATION: return (a, b) -> a.expireTime <= b.expireTime;
            case ORDER_BY_VALUE: return (a, b) -> a.calcOrderValue(time) <= b.calcOrderValue(time);
            default: return null;
        }
    }

    private Shelf targetShelf(Order order) {
        Shelf shelf = null;

        switch (order.temp) {
            case hot:
                shelf = hotShelf;
                break;
            case cold:
                shelf = coldShelf;
                break;
            case frozen:
                shelf = frozShelf;
                break;
            default:
                break;
        }

        return shelf;
    }

    private Optional<InternalOrder> peekUnexpired(Shelf shelf, long time) {
        Optional<InternalOrder> result = shelf.peek(time);

        return result;
    }

    /**
     * Current system time in second
     * @return
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 100;
    }

}
