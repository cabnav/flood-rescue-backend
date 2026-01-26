package com.floodrescue.backend.common.util;

public class ApplicationConstants {
    
    /**
     * Maximum number of failed login attempts before account is locked
     */
    public static final int MAX_FAILED_ATTEMPTS = 5;
    
    /**
     * Account lock duration in minutes
     */
    public static final int LOCK_TIME_DURATION_MINUTES = 30;
    
    private ApplicationConstants() {
        // Utility class - prevent instantiation
    }
}
