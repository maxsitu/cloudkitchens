package com.cloudkitchens.strategy;

import com.cloudkitchens.model.ShelfType;

public class ShelfStrategyFactory {
    public static enum StrategyType {ORDER_BY_VALUE, ORDER_BY_EXPIRATION}
    private final StrategyType strategyType;

    public ShelfStrategyFactory(StrategyType strategyType) {
        this.strategyType = strategyType;
    }

    public ShelfStrategy createShelfStrategy(ShelfType shelfType) {
        switch (strategyType) {
            case ORDER_BY_EXPIRATION: return new ExpireTimeOrderedShelfStrategy(shelfType);
            case ORDER_BY_VALUE: return new ValueOrderedShelfStrategy(shelfType);
            default: return null;
        }
    }
}
