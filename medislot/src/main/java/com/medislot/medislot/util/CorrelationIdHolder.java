package com.medislot.medislot.util;

/**
 * Thread-local holder for correlation ID (X-Request-Id)
 * Allows accessing the correlation ID from anywhere in the request lifecycle
 */
public class CorrelationIdHolder {
    
    private static final ThreadLocal<String> correlationIdHolder = new ThreadLocal<>();
    
    /**
     * Set the correlation ID for the current thread
     */
    public static void setCorrelationId(String correlationId) {
        correlationIdHolder.set(correlationId);
    }
    
    /**
     * Get the correlation ID for the current thread
     */
    public static String getCorrelationId() {
        return correlationIdHolder.get();
    }
    
    /**
     * Clear the correlation ID from the current thread
     * IMPORTANT: Must be called to prevent memory leaks
     */
    public static void clear() {
        correlationIdHolder.remove();
    }
    
    /**
     * Check if a correlation ID exists for the current thread
     */
    public static boolean hasCorrelationId() {
        return correlationIdHolder.get() != null;
    }
}
