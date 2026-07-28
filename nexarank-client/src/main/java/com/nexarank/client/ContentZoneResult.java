// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * One zone's winning content, matching ContentEnrichController.toPayload()
 * in nexarank-api field-for-field. Fields absent from the winning rule's
 * payload come back as null, not omitted. `raw` carries the full untyped
 * payload for zone types (PROMO_GRID, FEATURED_PRODUCTS) the fixed fields
 * below don't fully cover.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentZoneResult {

    private String contentRuleId;
    private String headline;
    private String subheadline;
    private String imageUrl;
    private String ctaText;
    private String ctaLink;
    private String backgroundColor;
    private String textColor;
    private Map<String, String> raw;

    public String getContentRuleId() { return contentRuleId; }
    public void setContentRuleId(String contentRuleId) { this.contentRuleId = contentRuleId; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getSubheadline() { return subheadline; }
    public void setSubheadline(String subheadline) { this.subheadline = subheadline; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCtaText() { return ctaText; }
    public void setCtaText(String ctaText) { this.ctaText = ctaText; }

    public String getCtaLink() { return ctaLink; }
    public void setCtaLink(String ctaLink) { this.ctaLink = ctaLink; }

    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }

    public Map<String, String> getRaw() { return raw; }
    public void setRaw(Map<String, String> raw) { this.raw = raw; }
}
