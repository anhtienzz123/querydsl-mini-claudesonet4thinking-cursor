package com.querydsl.mini;

import com.querydsl.mini.expressions.PathExpression;
import com.querydsl.mini.factory.ExpressionFactory;
import com.querydsl.mini.factory.QueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.querydsl.mini.factory.ExpressionFactory.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test demonstrating QueryDSL Mini usage and design patterns.
 */
public class QueryDSLExampleTest {
    
    private DataSource dataSource;
    private QueryDSL queryDSL;
    
    @BeforeEach
    void setUp() throws SQLException {
        // Create H2 in-memory database
        dataSource = createH2DataSource();
        setupTestData();
        queryDSL = QueryDSL.withDataSource(dataSource);
    }
    
    @AfterEach
    void tearDown() {
        if (queryDSL != null) {
            queryDSL.close();
        }
    }
    
    @Test
    void demonstrateBasicQuery() {
        // Demonstrate Builder Pattern with fluent API
        QueryFactory qf = queryDSL.getQueryFactory();
        
        PathExpression<String> userName = path(String.class, "users", "name");
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        
        String sql = qf.select()
                .from("users")
                .where(userAge.gt(25))
                .orderBy(userName)
                .limit(10)
                .toSQL();
        
        assertEquals("SELECT * FROM users WHERE (users.age > 25) ORDER BY users.name ASC LIMIT 10", sql);
    }
    
    @Test
    void demonstrateComplexQuery() {
        // Demonstrate multiple design patterns: Builder, Factory, Composite
        QueryFactory qf = queryDSL.getQueryFactory();
        
        PathExpression<String> userName = path(String.class, "users", "name");
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        PathExpression<String> userEmail = path(String.class, "users", "email");
        
        String sql = qf.select()
                .from("users", "u")
                .where(userAge.ge(18)
                        .and(userName.like("%John%"))
                        .or(userEmail.isNotNull()))
                .orderBy(userName)
                .orderBy(userAge, com.querydsl.mini.query.Query.OrderDirection.DESC)
                .toSQL();
        
        String expected = "SELECT * FROM users u WHERE (((users.age >= 18) AND (users.name LIKE '%John%')) OR (users.email IS NOT NULL)) ORDER BY users.name ASC, users.age DESC";
        assertEquals(expected, sql);
    }
    
    @Test
    void demonstrateAggregateQuery() {
        // Demonstrate Factory Pattern with aggregate functions
        QueryFactory qf = queryDSL.getQueryFactory();
        
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        
        String sql = qf.select()
                .select(count(userAge), avg(userAge), min(userAge), max(userAge))
                .from("users")
                .where(userAge.gt(0))
                .toSQL();
        
        assertEquals("SELECT COUNT(users.age), AVG(users.age), MIN(users.age), MAX(users.age) FROM users WHERE (users.age > 0)", sql);
    }
    
    @Test
    void demonstrateJoinQuery() {
        // Demonstrate complex query with joins
        QueryFactory qf = queryDSL.getQueryFactory();
        
        PathExpression<Integer> userId = path(Integer.class, "users", "id");
        PathExpression<Integer> orderUserId = path(Integer.class, "orders", "user_id");
        PathExpression<String> userName = path(String.class, "users", "name");
        PathExpression<Double> orderAmount = path(Double.class, "orders", "amount");
        
        String sql = qf.select()
                .select(userName, sum(orderAmount))
                .from("users", "u")
                .leftJoin("orders", "o", userId.eq(orderUserId))
                .groupBy(userId, userName)
                .having(sum(orderAmount).gt(100.0))
                .orderBy(sum(orderAmount), com.querydsl.mini.query.Query.OrderDirection.DESC)
                .toSQL();
        
        String expected = "SELECT users.name, SUM(orders.amount) FROM users u LEFT JOIN orders o ON (users.id = orders.user_id) GROUP BY users.id, users.name HAVING (SUM(orders.amount) > 100.0) ORDER BY SUM(orders.amount) DESC";
        assertEquals(expected, sql);
    }
    
    @Test
    void demonstrateCaseExpression() {
        // Demonstrate Case Expression pattern
        QueryFactory qf = queryDSL.getQueryFactory();
        
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        
        var ageCategory = caseExpression(String.class)
                .when(userAge.lt(18), constant("Minor"))
                .when(userAge.lt(65), constant("Adult"))
                .otherwise(constant("Senior"));
        
        String sql = qf.select()
                .select(ageCategory)
                .from("users")
                .toSQL();
        
        assertEquals("SELECT CASE WHEN (users.age < 18) THEN 'Minor' WHEN (users.age < 65) THEN 'Adult' ELSE 'Senior' END FROM users", sql);
    }
    
    @Test
    void demonstrateVisitorPattern() {
        // Demonstrate Visitor Pattern with SQL generation
        PathExpression<String> userName = path(String.class, "users", "name");
        
        // The visitor pattern is used internally by the SQL generator
        String result = userName.accept(queryDSL.getQueryFactory().getSqlGenerator(), null);
        assertEquals("users.name", result);
    }
    
    @Test
    void demonstrateFactoryPattern() {
        // Demonstrate Factory Pattern usage
        ExpressionFactory ef = queryDSL.getExpressionFactory();
        
        // Factory methods for different expression types
        var stringPath = path(String.class, "table", "column");
        var stringConstant = constant("value");
        var numberConstant = constant(42);
        var countExpr = countAll();
        
        assertNotNull(stringPath);
        assertNotNull(stringConstant);
        assertNotNull(numberConstant);
        assertNotNull(countExpr);
        
        assertEquals(String.class, stringPath.getType());
        assertEquals(String.class, stringConstant.getType());
        assertEquals(Integer.class, numberConstant.getType());
        assertEquals(Long.class, countExpr.getType());
    }
    
    private DataSource createH2DataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            }
            
            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }
            
            // Other DataSource methods with default implementations
            @Override public java.io.PrintWriter getLogWriter() { return null; }
            @Override public void setLogWriter(java.io.PrintWriter out) {}
            @Override public void setLoginTimeout(int seconds) {}
            @Override public int getLoginTimeout() { return 0; }
            @Override public java.util.logging.Logger getParentLogger() { return null; }
            @Override public <T> T unwrap(Class<T> iface) { return null; }
            @Override public boolean isWrapperFor(Class<?> iface) { return false; }
        };
    }
    
    private void setupTestData() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create test tables
            stmt.execute("CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100), age INT)");
            stmt.execute("CREATE TABLE orders (id INT PRIMARY KEY, user_id INT, amount DECIMAL(10,2))");
            
            // Insert test data
            stmt.execute("INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30)");
            stmt.execute("INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25)");
            stmt.execute("INSERT INTO users VALUES (3, 'Bob Johnson', null, 35)");
            
            stmt.execute("INSERT INTO orders VALUES (1, 1, 150.00)");
            stmt.execute("INSERT INTO orders VALUES (2, 1, 75.50)");
            stmt.execute("INSERT INTO orders VALUES (3, 2, 200.00)");
        }
    }
} 