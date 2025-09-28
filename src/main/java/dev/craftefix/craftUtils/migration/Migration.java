package dev.craftefix.craftUtils.migration;

public interface Migration {
    String getId();
    String getDescription();
    MigrationType getType();
    void up() throws MigrationException;
    void down() throws MigrationException;
}