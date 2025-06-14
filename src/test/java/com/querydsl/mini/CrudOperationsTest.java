package com.querydsl.mini;

import com.querydsl.mini.expressions.PathExpression;
import com.querydsl.mini.factory.QueryFactory;
import com.querydsl.mini.sql.StandardSqlGenerator;
import org.junit.jupiter.api.Test;

import static com.querydsl.mini.factory.ExpressionFactory.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test demonstrating the new CRUD functionality in QueryDSL Mini.
 * Tests INSERT, UPDATE, and DELETE SQL generation.
 */
public class CrudOperationsTest {
    
    private final QueryFactory queryFactory = new QueryFactory(null, new StandardSqlGenerator());
    
    @Test
    void testInsertWithValues() {
        String sql = queryFactory.insertInto("users")
                .columns("name", "age", "email")
                .values("Alice", 25, "alice@example.com")
                .toSQL();
        
        assertEquals("INSERT INTO users (name, age, email) VALUES ('Alice', 25, 'alice@example.com')", sql);
    }
    
    @Test
    void testInsertBatch() {
        String sql = queryFactory.insertInto("users")
                .columns("name", "age", "email")
                .values("Alice", 25, "alice@example.com")
                .values("Bob", 30, "bob@example.com")
                .toSQL();
        
        String expected = "INSERT INTO users (name, age, email) VALUES " +
                         "('Alice', 25, 'alice@example.com'), " +
                         "('Bob', 30, 'bob@example.com')";
        assertEquals(expected, sql);
    }
    
    @Test
    void testUpdate() {
        PathExpression<Integer> userId = path(Integer.class, "users", "id");
        
        String sql = queryFactory.update("users")
                .set("name", "Updated Name")
                .set("age", 26)
                .where(userId.eq(1))
                .toSQL();
        
        assertEquals("UPDATE users SET name = 'Updated Name', age = 26 WHERE (users.id = 1)", sql);
    }
    
    @Test
    void testDelete() {
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        
        String sql = queryFactory.deleteFrom("users")
                .where(userAge.lt(18))
                .toSQL();
        
        assertEquals("DELETE FROM users WHERE (users.age < 18)", sql);
    }
    
    @Test
    void testDeleteWithoutWhere() {
        String sql = queryFactory.deleteFrom("temp_table")
                .toSQL();
        
        assertEquals("DELETE FROM temp_table", sql);
    }
    
    @Test
    void testCrudFluentApi() {
        PathExpression<Integer> userId = path(Integer.class, "users", "id");
        PathExpression<String> userName = path(String.class, "users", "name");
        
        // Demonstrate fluent API works for all CRUD operations
        var insertSql = queryFactory.insertInto("users")
                .columns("name", "age")
                .values("Test", 25)
                .toSQL();
        
        var updateSql = queryFactory.update("users")
                .set("name", "Updated")
                .where(userId.eq(1))
                .toSQL();
        
        var deleteSql = queryFactory.deleteFrom("users")
                .where(userName.eq("Test"))
                .toSQL();
        
        assertTrue(insertSql.startsWith("INSERT INTO"));
        assertTrue(updateSql.startsWith("UPDATE"));
        assertTrue(deleteSql.startsWith("DELETE FROM"));
    }
} 