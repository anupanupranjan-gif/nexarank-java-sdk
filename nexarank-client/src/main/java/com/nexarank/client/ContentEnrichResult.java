// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import java.util.EnumMap;
import java.util.Map;

/**
 * Result of a content enrich call, keyed by the requested ContentZone. A
 * zone is absent when no ACTIVE rule matched it — callers fall back to
 * their own default content for that zone.
 */
public class ContentEnrichResult {

    private final Map<ContentZone, ContentZoneResult> zones;

    public ContentEnrichResult(Map<ContentZone, ContentZoneResult> zones) {
        this.zones = zones;
    }

    public static ContentEnrichResult empty() {
        return new ContentEnrichResult(new EnumMap<>(ContentZone.class));
    }

    public boolean has(ContentZone zone) {
        return zones.containsKey(zone);
    }

    public ContentZoneResult get(ContentZone zone) {
        return zones.get(zone);
    }

    public Map<ContentZone, ContentZoneResult> asMap() {
        return zones;
    }
}
