// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * NR-104: JVM counterpart of nexarank-content-sdk's contentEnrich(). Calls
 * POST /api/v1/content/enrich and returns typed, per-zone content. Fails
 * open (empty ContentEnrichResult) after exhausting retries — callers
 * always fall back to their own default content, same contract as
 * NexaRankClient's query-pipeline enrich().
 */
public class ContentEnrichClient {

    private static final Logger log = LoggerFactory.getLogger(ContentEnrichClient.class);
    private static final String ENRICH_PATH = "/api/v1/content/enrich";

    private final NexaRankConfig config;
    private final ObjectMapper objectMapper;

    public ContentEnrichClient(NexaRankConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
    }

    public ContentEnrichResult enrich(List<ContentZone> zones, PageContext context) {
        if (!config.isEnabled() || zones == null || zones.isEmpty()) {
            return ContentEnrichResult.empty();
        }

        Exception lastError = null;
        for (int attempt = 0; attempt <= config.getMaxRetries(); attempt++) {
            try {
                return callEnrichApi(zones, context);
            } catch (RetriableException e) {
                lastError = e.getCause() != null ? (Exception) e.getCause() : e;
                if (attempt == config.getMaxRetries()) break;
                sleep(config.getBaseDelayMs() * (1L << attempt));
            } catch (Exception e) {
                lastError = e;
                break;
            }
        }

        log.warn("NexaRank content enrich failed for zones={}, falling back to empty. Error: {}",
                zones, lastError != null ? lastError.getMessage() : "unknown");
        return ContentEnrichResult.empty();
    }

    private ContentEnrichResult callEnrichApi(List<ContentZone> zones, PageContext context) throws IOException {
        String urlStr = config.getBaseUrl() + ENRICH_PATH;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(config.getConnectTimeoutMs());
            conn.setReadTimeout(config.getReadTimeoutMs());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-Tenant-Id", config.getTenantId());
            conn.setRequestProperty("X-Project-Id", config.getProjectId());
            if (config.getApiKey() != null && !config.getApiKey().isBlank()) {
                conn.setRequestProperty("X-Api-Key", config.getApiKey());
            }

            Map<String, Object> body = Map.of(
                    "zones", zones.stream().map(Enum::name).toList(),
                    "context", context != null ? context.toMap() : Map.of()
            );
            conn.getOutputStream().write(objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8));

            int status = conn.getResponseCode();
            if (status != 200) {
                NexaRankException cause = new NexaRankException("NexaRank content enrich returned status " + status, status);
                if (status >= 500) throw new RetriableException(cause);
                throw cause;
            }

            byte[] resp = conn.getInputStream().readAllBytes();
            return parseResponse(new String(resp, StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Connection refused / timeout / reset — network-level, retriable.
            throw new RetriableException(e);
        } finally {
            conn.disconnect();
        }
    }

    private ContentEnrichResult parseResponse(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode zonesNode = root.get("zones");
        Map<ContentZone, ContentZoneResult> zones = new EnumMap<>(ContentZone.class);
        if (zonesNode != null && zonesNode.isObject()) {
            zonesNode.fields().forEachRemaining(entry -> {
                try {
                    ContentZone zone = ContentZone.valueOf(entry.getKey());
                    ContentZoneResult result = objectMapper.treeToValue(entry.getValue(), ContentZoneResult.class);
                    zones.put(zone, result);
                } catch (Exception e) {
                    log.warn("Skipping unrecognized content zone '{}' in enrich response", entry.getKey());
                }
            });
        }
        return new ContentEnrichResult(zones);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Marks an error as worth retrying (network failure or 5xx) vs. terminal (4xx, parse error). */
    private static class RetriableException extends RuntimeException {
        RetriableException(Throwable cause) { super(cause); }
    }
}
