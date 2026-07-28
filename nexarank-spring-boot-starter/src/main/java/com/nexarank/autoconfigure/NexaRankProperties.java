// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nexarank")
public class NexaRankProperties {

    private String baseUrl;
    private String apiKey;
    private String mode = "cloud";
    private String cacheTtl = "5m";
    private boolean enabled = true;
    private int connectTimeoutMs = 3000;
    private int readTimeoutMs = 5000;
    private int maxRetries = 2;
    private long baseDelayMs = 200;
    private String tenantId = "default";
    private String projectId = "main";

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getCacheTtl() { return cacheTtl; }
    public void setCacheTtl(String cacheTtl) { this.cacheTtl = cacheTtl; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public int getReadTimeoutMs() { return readTimeoutMs; }
    public void setReadTimeoutMs(int readTimeoutMs) { this.readTimeoutMs = readTimeoutMs; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public long getBaseDelayMs() { return baseDelayMs; }
    public void setBaseDelayMs(long baseDelayMs) { this.baseDelayMs = baseDelayMs; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public long getCacheTtlSeconds() {
        if (cacheTtl == null || cacheTtl.isBlank()) return 300;
        cacheTtl = cacheTtl.trim().toLowerCase();
        if (cacheTtl.endsWith("m")) {
            return Long.parseLong(cacheTtl.replace("m", "")) * 60;
        } else if (cacheTtl.endsWith("h")) {
            return Long.parseLong(cacheTtl.replace("h", "")) * 3600;
        } else if (cacheTtl.endsWith("s")) {
            return Long.parseLong(cacheTtl.replace("s", ""));
        }
        return Long.parseLong(cacheTtl);
    }
}
