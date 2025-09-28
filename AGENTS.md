# AGENTS.md - CraftUtils Development Guide

## Build/Test Commands
- **Build**: `./gradlew build`
- **Test**: `./gradlew test`
- **Run single test**: `./gradlew test --tests "ClassName.methodName"`
- **Shadow JAR**: `./gradlew shadowJar`
- **Clean build**: `./gradlew clean build`

## Code Style Guidelines
- **Java version**: Java 21 (source/target compatibility)
- **Package structure**: `dev.craftefix.craftUtils.*`
- **Imports**: Use specific imports, avoid wildcards except for static imports
- **Naming**: CamelCase for classes, camelCase for methods/variables, UPPER_SNAKE_CASE for constants
- **Error handling**: Use try-catch blocks with proper logging via `getLogger().severe/warning/info`
- **Database**: Use HikariCP connection pooling, properly close resources in try-with-resources
- **Commands**: Use Lamp framework annotations (@Command, @CommandPermission)
- **Messaging**: Use Adventure Component API for formatted messages
- **Configuration**: Commands are toggled via config.yml under `commands:` section

## Key Patterns
- Managers handle database operations (HomeManager, WarpManager, etc.)
- Commands are registered conditionally based on config.yml settings
- Use singleton pattern for Main plugin instance
- GUI classes extend CustomGUI for consistent inventory management