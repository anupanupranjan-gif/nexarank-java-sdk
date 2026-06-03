// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
package com.nexarank.client;

public class NexaRankException extends RuntimeException {

    private final int statusCode;

    public NexaRankException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public NexaRankException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public NexaRankException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public int getStatusCode() { return statusCode; }
}
