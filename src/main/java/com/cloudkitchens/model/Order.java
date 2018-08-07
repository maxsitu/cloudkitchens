package com.cloudkitchens.model;

import com.google.common.base.Objects;

public class Order {
    public enum Temp {
        hot, cold, frozen
    }

    public final String name;
    public final Temp temp;
    public final int shelfLife;
    public final double decayRate;

    public Order(String name, Temp temp, int shelfLife, double decayRate) {
        this.name = name;
        this.temp = temp;
        this.shelfLife = shelfLife;
        this.decayRate = decayRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return shelfLife == order.shelfLife &&
                Double.compare(order.decayRate, decayRate) == 0 &&
                Objects.equal(name, order.name) &&
                temp == order.temp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, temp, shelfLife, decayRate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", temp=" + temp +
                ", shelfLife=" + shelfLife +
                ", decayRate=" + decayRate +
                '}';
    }
}
