// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.autoconfigure;

import com.nexarank.client.NexaRankClient;
import com.nexarank.client.NexaRankConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NexaRankProperties.class)
public class NexaRankAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NexaRankConfig nexaRankConfig(NexaRankProperties properties) {
        NexaRankConfig config = new NexaRankConfig();
        config.setBaseUrl(properties.getBaseUrl());
        config.setApiKey(properties.getApiKey());
        config.setMode(properties.getMode());
        config.setCacheTtlSeconds(properties.getCacheTtlSeconds());
        config.setEnabled(properties.isEnabled());
        config.setConnectTimeoutMs(properties.getConnectTimeoutMs());
        config.setReadTimeoutMs(properties.getReadTimeoutMs());
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public NexaRankClient nexaRankClient(NexaRankConfig nexaRankConfig) {
        return new NexaRankClient(nexaRankConfig);
    }
}
