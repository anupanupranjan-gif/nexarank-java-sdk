// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NexaRankEnrichedQuery {

    private String originalQuery;
    private String expandedQuery;
    private List<BoostInstruction> boosts;
    private List<PinInstruction> pins;
    private List<BuryInstruction> buries;
    private List<String> synonyms;
    private int rulesApplied;
    private long processingMs;

    public String getOriginalQuery() { return originalQuery; }
    public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

    public String getExpandedQuery() { return expandedQuery; }
    public void setExpandedQuery(String expandedQuery) { this.expandedQuery = expandedQuery; }

    public List<BoostInstruction> getBoosts() { return boosts; }
    public void setBoosts(List<BoostInstruction> boosts) { this.boosts = boosts; }

    public List<PinInstruction> getPins() { return pins; }
    public void setPins(List<PinInstruction> pins) { this.pins = pins; }

    public List<BuryInstruction> getBuries() { return buries; }
    public void setBuries(List<BuryInstruction> buries) { this.buries = buries; }

    public List<String> getSynonyms() { return synonyms; }
    public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }

    public int getRulesApplied() { return rulesApplied; }
    public void setRulesApplied(int rulesApplied) { this.rulesApplied = rulesApplied; }

    public long getProcessingMs() { return processingMs; }
    public void setProcessingMs(long processingMs) { this.processingMs = processingMs; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoostInstruction {
        private String field;
        private String value;
        private float factor;

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public float getFactor() { return factor; }
        public void setFactor(float factor) { this.factor = factor; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PinInstruction {
        private String productId;
        private int position;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BuryInstruction {
        private String field;
        private String value;

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
