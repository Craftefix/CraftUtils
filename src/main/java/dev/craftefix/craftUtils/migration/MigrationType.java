package dev.craftefix.craftUtils.migration;

/**
 * Types of migrations supported
 */
public enum MigrationType {
    DATABASE("Database"),
    CONFIG("Configuration"),
    DATA("Data");
    
    private final String displayName;
    
    MigrationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}