package com.cloudkitchens;

import com.cloudkitchens.model.Shelf;
import com.cloudkitchens.model.ShelfType;
import com.cloudkitchens.strategy.ExpireTimeOrderedShelfStrategy;
import com.cloudkitchens.strategy.ShelfStrategyFactory;
import com.cloudkitchens.strategy.ShelfStrategyFactory.StrategyType;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Configuration
@PropertySource("file:${order-service.properties}")
public class SpringConfig {

    @Value("${shelfStrategy}")
    private StrategyType strategyType;

    @Value("${quota.hot}")
    private int hotQuota;

    @Value("${quota.cold}")
    private int coldQuota;

    @Value("${quota.frozen}")
    private int frozenQuota;

    @Value("${quota.overflow}")
    private int overflowQuota;

    @Value("${orderTaker.meanWaitTimeMillis}")
    private double meanWaitTimeMillis;

    @Value("${orderMaker.freqPerSecond}")
    private int orderMakerFreqPerSecond;

    @Bean
    public ShelfStrategyFactory getShelfStrategyFactory() {
        return new ShelfStrategyFactory(strategyType);
    }

    @Bean
    public Map<ShelfType, Integer> quotaMap() {
        return ImmutableMap
                .of(
                        ShelfType.hot, hotQuota,
                        ShelfType.cold, coldQuota,
                        ShelfType.frozen, frozenQuota,
                        ShelfType.overflow, overflowQuota
                );
    }

    @Bean
    public OrderManager orderManager() {
        Shelf hotShelf = new Shelf(hotQuota, getShelfStrategyFactory().createShelfStrategy(ShelfType.hot));
        Shelf coldShelf = new Shelf(coldQuota, getShelfStrategyFactory().createShelfStrategy(ShelfType.cold));
        Shelf frozShelf = new Shelf(frozenQuota, getShelfStrategyFactory().createShelfStrategy(ShelfType.frozen));
        Shelf overShelf = new Shelf(overflowQuota, getShelfStrategyFactory().createShelfStrategy(ShelfType.overflow));

        return new OrderManager(hotShelf, coldShelf, frozShelf, overShelf, strategyType);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OrderTaker orderTaker() {
        return new OrderTaker(meanWaitTimeMillis, orderManager());
    }

    @Bean
    public OrderMaker orderMaker() {
        return new OrderMaker( 1000 / orderMakerFreqPerSecond, orderManager());
    }
}
