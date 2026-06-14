// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NexaRankClient {

    private static final Logger log = LoggerFactory.getLogger(NexaRankClient.class);
    private static final String ENRICH_PATH = "/api/v1/rules/enrich";

    private final NexaRankConfig config;
    private final ObjectMapper objectMapper;
    private final Map<String, CacheEntry> cache;

    public NexaRankClient(NexaRankConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();
        this.cache = new ConcurrentHashMap<>();
    }

    public NexaRankEnrichedQuery enrich(String query) {
        if (!config.isEnabled()) {
            log.debug("NexaRank is disabled, returning empty enriched query for: {}", query);
            return emptyResult(query);
        }

        String cacheKey = query.toLowerCase().trim();
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && !cached.isExpired(config.getCacheTtlSeconds())) {
            log.debug("NexaRank cache hit for query: {}", query);
            return cached.value;
        }

        try {
            NexaRankEnrichedQuery result = callEnrichApi(query);
            cache.put(cacheKey, new CacheEntry(result));
            log.debug("NexaRank: {} rules applied for query='{}'", result.getRulesApplied(), query);
            return result;
        } catch (Exception e) {
            log.warn("NexaRank enrich failed for query='{}', falling back to passthrough. Error: {}", query, e.getMessage());
            return emptyResult(query);
        }
    }

    private NexaRankEnrichedQuery callEnrichApi(String query) throws IOException {
        String urlStr = config.getBaseUrl() + ENRICH_PATH + "?query=" + encodeQuery(query);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(config.getConnectTimeoutMs());
            conn.setReadTimeout(config.getReadTimeoutMs());
            conn.setRequestProperty("Accept", "application/json");

            if (config.getApiKey() != null && !config.getApiKey().isBlank()) {
                conn.setRequestProperty("X-Api-Key", config.getApiKey());
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new NexaRankException("NexaRank API returned status " + status, status);
            }

            byte[] body = conn.getInputStream().readAllBytes();
            return objectMapper.readValue(new String(body, StandardCharsets.UTF_8), NexaRankEnrichedQuery.class);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Rich enrich — supports sessionId and selectedFacets.
     * Uses POST to pass the full context to nexarank-api.
     * Falls back to passthrough on any error.
     */
    public NexaRankEnrichedQuery enrich(String query,
                                        String sessionId,
                                        java.util.Map<String, String> selectedFacets) {
        if (!config.isEnabled()) return emptyResult(query);

        // No session or facets — use cached simple path
        if ((sessionId == null || sessionId.isBlank())
                && (selectedFacets == null || selectedFacets.isEmpty())) {
            return enrich(query);
        }

        try {
            return callEnrichApiPost(query, sessionId, selectedFacets);
        } catch (Exception e) {
            log.warn("NexaRank enrich (rich) failed for query='{}': {} — passthrough",
                    query, e.getMessage());
            return emptyResult(query);
        }
    }

    private NexaRankEnrichedQuery callEnrichApiPost(String query,
                                                    String sessionId,
                                                    java.util.Map<String, String> selectedFacets)
            throws java.io.IOException {

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("query", query);
        if (sessionId != null && !sessionId.isBlank()) body.put("sessionId", sessionId);
        if (selectedFacets != null && !selectedFacets.isEmpty())
            body.put("selectedFacets", selectedFacets);

        String json = objectMapper.writeValueAsString(body);
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
            if (config.getApiKey() != null && !config.getApiKey().isBlank())
                conn.setRequestProperty("X-Api-Key", config.getApiKey());

            conn.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));

            int status = conn.getResponseCode();
            if (status != 200)
                throw new NexaRankException("NexaRank API returned status " + status, status);

            byte[] resp = conn.getInputStream().readAllBytes();
            return objectMapper.readValue(new String(resp, StandardCharsets.UTF_8),
                    NexaRankEnrichedQuery.class);
        } finally {
            conn.disconnect();
        }
    }

    private NexaRankEnrichedQuery emptyResult(String query) {
        NexaRankEnrichedQuery result = new NexaRankEnrichedQuery();
        result.setOriginalQuery(query);
        result.setExpandedQuery(query);
        result.setRulesApplied(0);
        return result;
    }

    private String encodeQuery(String query) {
        return java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    public void clearCache() {
        cache.clear();
    }

    private static class CacheEntry {
        final NexaRankEnrichedQuery value;
        final long createdAt;

        CacheEntry(NexaRankEnrichedQuery value) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
        }

        boolean isExpired(long ttlSeconds) {
            return (System.currentTimeMillis() - createdAt) > (ttlSeconds * 1000);
        }
    }
}
