// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Page context for content zone trigger-condition matching (pageType,
 * category, query, customerSegment, deviceType — the fields
 * ContentRuleService's matcher understands; any other key is ignored
 * server-side rather than rejected).
 */
public class PageContext {

    private final Map<String, String> values = new LinkedHashMap<>();

    public static PageContext of() {
        return new PageContext();
    }

    public PageContext with(String key, String value) {
        if (key != null && value != null) values.put(key, value);
        return this;
    }

    public Map<String, String> toMap() {
        return values;
    }
}
