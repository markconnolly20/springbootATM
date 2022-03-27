package com.mdc.atm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.money.convert.ExchangeRateProvider;

@Profile("test")
@Configuration
public class TestConfig {

    @Bean
    public ExchangeRateProvider exchangeRateProvider() {
        return new TestingExchangeRateProvider();
    }
}
