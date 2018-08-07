package com.cloudkitchens.strategy;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.ShelfType;
import com.google.common.base.Objects;

import java.util.Optional;
import java.util.function.Function;

public abstract class ShelfStrategy {
    public abstract InternalOrder addOrder(Order order, long orderTime);
    public abstract InternalOrder removeOrder(Order order);
    public abstract Order chooseReplacingOrder(long time);
    public abstract Optional<InternalOrder> peekExpiredOrder(long expireTime);
    public abstract InternalOrder getInternalOrder(Order order);
    public abstract ShelfType getShelfType();

    public class InternalOrder {
        public final Order order;
        public final long orderTime;
        public final long expireTime;

        InternalOrder(Order order, long orderTime) {
            this.order = order;
            this.orderTime = orderTime;
            this.expireTime = orderTime + calcExpireAge();
        }

        private long calcExpireAge() {
            Function<Double, Double> decayRateFunc = calcDecayRateFunc();
            return Math.round(order.shelfLife / (1 + decayRateFunc.apply(order.decayRate)));
        }

        private Function<Double, Double> calcDecayRateFunc() {
            return getShelfType() == ShelfType.overflow ? r -> 2.0 * r : Function.identity();
        }

        public double calcOrderValue(long time) {
            long age = time - orderTime;
            return order.shelfLife - age - calcDecayRateFunc().apply(order.decayRate) * age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InternalOrder that = (InternalOrder) o;
            return orderTime == that.orderTime &&
                    expireTime == that.expireTime &&
                    Objects.equal(order, that.order);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(order, orderTime, expireTime);
        }

        @Override
        public String toString() {
            return "InternalOrder{" +
                    "order=" + order +
                    ", orderTime=" + orderTime +
                    ", expireTime=" + expireTime +
                    '}';
        }
    }
}
