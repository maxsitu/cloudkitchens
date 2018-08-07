package com.cloudkitchens.strategy;

import com.cloudkitchens.model.Order;
import com.cloudkitchens.model.ShelfType;

import java.util.*;

public class ValueOrderedShelfStrategy extends ExpireTimeOrderedShelfStrategy {

    public ValueOrderedShelfStrategy(ShelfType shelfType) {
        super(shelfType);
    }

    @Override
    public Order chooseReplacingOrder(long time) {
        return internalOrderMap.values()
                .stream()
                .sorted(Comparator.comparing(o -> o.calcOrderValue(time)))
                .findFirst()
                .get()
                .order;
    }
}
