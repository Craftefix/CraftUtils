package dev.craftefix.craftUtils.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for utility functions and helper methods
 */
class UtilsTest {
    
    @Test
    @DisplayName("Should validate UUID format")
    void testUUIDValidation() {
        // Valid UUIDs
        assertTrue(isValidUUID("550e8400-e29b-41d4-a716-446655440000"));
        assertTrue(isValidUUID("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
        
        // Invalid UUIDs
        assertFalse(isValidUUID("invalid-uuid"));
        assertFalse(isValidUUID("550e8400-e29b-41d4-a716"));
        assertFalse(isValidUUID(""));
        assertFalse(isValidUUID(null));
    }
    
    @Test
    @DisplayName("Should format time durations correctly")
    void testTimeFormatting() {
        assertEquals("1 second", formatDuration(1));
        assertEquals("30 seconds", formatDuration(30));
        assertEquals("1 minute", formatDuration(60));
        assertEquals("5 minutes", formatDuration(300));
        assertEquals("1 hour", formatDuration(3600));
        assertEquals("2 hours", formatDuration(7200));
        assertEquals("1 day", formatDuration(86400));
        assertEquals("2 days", formatDuration(172800));
    }
    
    @Test
    @DisplayName("Should parse duration strings correctly")
    void testDurationParsing() {
        assertEquals(1L, parseDuration("1s"));
        assertEquals(60L, parseDuration("1m"));
        assertEquals(3600L, parseDuration("1h"));
        assertEquals(86400L, parseDuration("1d"));
        assertEquals(300L, parseDuration("5m"));
        assertEquals(7200L, parseDuration("2h"));
        
        // Invalid formats should return null
        assertNull(parseDuration("invalid"));
        assertNull(parseDuration(""));
        assertNull(parseDuration(null));
        assertNull(parseDuration("1x"));
    }
    
    @Test
    @DisplayName("Should validate home names")
    void testHomeNameValidation() {
        // Valid home names
        assertTrue(isValidHomeName("home"));
        assertTrue(isValidHomeName("base"));
        assertTrue(isValidHomeName("farm_1"));
        assertTrue(isValidHomeName("Shop-Main"));
        assertTrue(isValidHomeName("a"));
        
        // Invalid home names
        assertFalse(isValidHomeName(""));
        assertFalse(isValidHomeName(null));
        assertFalse(isValidHomeName("this_is_a_very_long_home_name_that_exceeds_the_limit"));
        assertFalse(isValidHomeName("home with spaces"));
        assertFalse(isValidHomeName("home@invalid"));
    }
    
    @Test
    @DisplayName("Should validate warp names")
    void testWarpNameValidation() {
        // Valid warp names
        assertTrue(isValidWarpName("spawn"));
        assertTrue(isValidWarpName("pvp"));
        assertTrue(isValidWarpName("shop_1"));
        assertTrue(isValidWarpName("Arena-Main"));
        
        // Invalid warp names (same rules as home names)
        assertFalse(isValidWarpName(""));
        assertFalse(isValidWarpName(null));
        assertFalse(isValidWarpName("warp with spaces"));
        assertFalse(isValidWarpName("warp@invalid"));
    }
    
    // Helper methods - these would typically be in your actual utility classes
    private boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) return false;
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds == 1 ? "1 second" : seconds + " seconds";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes == 1 ? "1 minute" : minutes + " minutes";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours == 1 ? "1 hour" : hours + " hours";
        } else {
            long days = seconds / 86400;
            return days == 1 ? "1 day" : days + " days";
        }
    }
    
    private Long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) return null;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)([smhd])");
        java.util.regex.Matcher matcher = pattern.matcher(duration.toLowerCase());
        
        if (!matcher.matches()) return null;
        
        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);
        
        return switch (unit) {
            case "s" -> amount;
            case "m" -> amount * 60;
            case "h" -> amount * 3600;
            case "d" -> amount * 86400;
            default -> null;
        };
    }
    
    private boolean isValidHomeName(String name) {
        return isValidName(name);
    }
    
    private boolean isValidWarpName(String name) {
        return isValidName(name);
    }
    
    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) return false;
        if (name.length() > 50) return false;
        if (name.contains(" ")) return false;
        if (!name.matches("[a-zA-Z0-9_-]+")) return false;
        return true;
    }
}