package dev.craftefix.craftUtils.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * Integration tests for database functionality with MariaDB and MySQL
 */
@Testcontainers
class DatabaseIntegrationTest {
    
    @Container
    static final MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.4")
            .withDatabaseName("craftutils_test")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("craftutils_test")
            .withUsername("test")
            .withPassword("test");
    
    private HikariDataSource mariaDbDataSource;
    private HikariDataSource mysqlDataSource;
    
    @BeforeEach
    void setUp() {
        // Setup MariaDB connection
        HikariConfig mariaConfig = new HikariConfig();
        mariaConfig.setJdbcUrl(mariadb.getJdbcUrl());
        mariaConfig.setUsername(mariadb.getUsername());
        mariaConfig.setPassword(mariadb.getPassword());
        mariaConfig.setMaximumPoolSize(5);
        mariaDbDataSource = new HikariDataSource(mariaConfig);
        
        // Setup MySQL connection
        HikariConfig mysqlConfig = new HikariConfig();
        mysqlConfig.setJdbcUrl(mysql.getJdbcUrl());
        mysqlConfig.setUsername(mysql.getUsername());
        mysqlConfig.setPassword(mysql.getPassword());
        mysqlConfig.setMaximumPoolSize(5);
        mysqlDataSource = new HikariDataSource(mysqlConfig);
    }
    
    @AfterEach
    void tearDown() {
        if (mariaDbDataSource != null && !mariaDbDataSource.isClosed()) {
            mariaDbDataSource.close();
        }
        if (mysqlDataSource != null && !mysqlDataSource.isClosed()) {
            mysqlDataSource.close();
        }
    }
    
    @Test
    @DisplayName("MariaDB connection should work")
    void testMariaDBConnection() throws SQLException {
        try (Connection conn = mariaDbDataSource.getConnection()) {
            assertTrue(conn.isValid(5), "MariaDB connection should be valid");
            
            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 as test")) {
                ResultSet rs = stmt.executeQuery();
                assertTrue(rs.next(), "Query should return a result");
                assertEquals(1, rs.getInt("test"), "Query should return correct value");
            }
        }
    }
    
    @Test
    @DisplayName("MySQL connection should work")
    void testMySQLConnection() throws SQLException {
        try (Connection conn = mysqlDataSource.getConnection()) {
            assertTrue(conn.isValid(5), "MySQL connection should be valid");
            
            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 as test")) {
                ResultSet rs = stmt.executeQuery();
                assertTrue(rs.next(), "Query should return a result");
                assertEquals(1, rs.getInt("test"), "Query should return correct value");
            }
        }
    }
    
    @Test
    @DisplayName("Should create homes table in MariaDB")
    void testCreateHomesTableMariaDB() throws SQLException {
        testCreateHomesTable(mariaDbDataSource, "MariaDB");
    }
    
    @Test
    @DisplayName("Should create homes table in MySQL")
    void testCreateHomesTableMySQL() throws SQLException {
        testCreateHomesTable(mysqlDataSource, "MySQL");
    }
    
    private void testCreateHomesTable(HikariDataSource dataSource, String dbType) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Create homes table similar to your DatabaseManager
            String createTable = """
                CREATE TABLE IF NOT EXISTS homes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36) NOT NULL,
                    home_name VARCHAR(50) NOT NULL,
                    world VARCHAR(50) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    pitch FLOAT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_player_home (player_uuid, home_name)
                )
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createTable)) {
                stmt.executeUpdate();
            }
            
            // Test inserting a home
            String insertHome = """
                INSERT INTO homes (player_uuid, home_name, world, x, y, z, yaw, pitch) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(insertHome)) {
                stmt.setString(1, "550e8400-e29b-41d4-a716-446655440000");
                stmt.setString(2, "test_home");
                stmt.setString(3, "world");
                stmt.setDouble(4, 100.0);
                stmt.setDouble(5, 64.0);
                stmt.setDouble(6, 200.0);
                stmt.setFloat(7, 0.0f);
                stmt.setFloat(8, 0.0f);
                
                int affected = stmt.executeUpdate();
                assertEquals(1, affected, dbType + " should insert one row");
            }
            
            // Test querying the home
            String selectHome = "SELECT * FROM homes WHERE player_uuid = ? AND home_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectHome)) {
                stmt.setString(1, "550e8400-e29b-41d4-a716-446655440000");
                stmt.setString(2, "test_home");
                
                ResultSet rs = stmt.executeQuery();
                assertTrue(rs.next(), dbType + " should return the inserted home");
                assertEquals("test_home", rs.getString("home_name"));
                assertEquals("world", rs.getString("world"));
                assertEquals(100.0, rs.getDouble("x"));
            }
        }
    }
    
    @Test
    @DisplayName("Should handle database transactions in MariaDB")
    void testTransactionMariaDB() throws SQLException {
        testTransactionHandling(mariaDbDataSource, "MariaDB");
    }
    
    @Test
    @DisplayName("Should handle database transactions in MySQL")
    void testTransactionMySQL() throws SQLException {
        testTransactionHandling(mysqlDataSource, "MySQL");
    }
    
    private void testTransactionHandling(HikariDataSource dataSource, String dbType) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Create a simple test table
            try (PreparedStatement stmt = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(50))")) {
                stmt.executeUpdate();
            }
            
            // Test transaction rollback
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO test_table VALUES (1, 'test1')")) {
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO test_table VALUES (1, 'test2')")) {
                    // This should fail due to duplicate key
                    stmt.executeUpdate();
                }
                conn.commit();
                fail(dbType + " should have thrown an exception for duplicate key");
            } catch (SQLException e) {
                conn.rollback();
                // Expected exception due to duplicate key
            }
            
            // Verify rollback worked
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM test_table")) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                assertEquals(0, rs.getInt(1), dbType + " should have no rows after rollback");
            }
            
            conn.setAutoCommit(true);
        }
    }
    
    @Test
    @DisplayName("Connection pool should handle concurrent connections")
    void testConnectionPooling() {
        // Test that we can get multiple connections from the pool
        await().atMost(java.time.Duration.ofSeconds(10))
               .until(() -> {
                   try (Connection conn1 = mariaDbDataSource.getConnection();
                        Connection conn2 = mariaDbDataSource.getConnection();
                        Connection conn3 = mysqlDataSource.getConnection()) {
                       return conn1.isValid(1) && conn2.isValid(1) && conn3.isValid(1);
                   } catch (SQLException e) {
                       return false;
                   }
               });
    }
}