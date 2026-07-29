// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

public class NexaRankConfig {

    private String baseUrl;
    private String apiKey;
    private String mode = "cloud";
    private long cacheTtlSeconds = 300;
    private boolean enabled = true;
    private int connectTimeoutMs = 3000;
    private int readTimeoutMs = 5000;
    private int maxRetries = 2;
    private long baseDelayMs = 200;
    private String tenantId = "default";
    private String projectId = "main";

    public NexaRankConfig() {}

    public NexaRankConfig(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public long getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(long cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public int getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }

    /** Read by ContentEnrichClient only — NexaRankClient's query-pipeline enrich() has no retry. */
    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public long getBaseDelayMs() { return baseDelayMs; }
    public void setBaseDelayMs(long baseDelayMs) { this.baseDelayMs = baseDelayMs; }

    /** Sent as X-Tenant-Id/X-Project-Id by both NexaRankClient and ContentEnrichClient, matching nexarank-api's TenantContext defaults. */
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
}
