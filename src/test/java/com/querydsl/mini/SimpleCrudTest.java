package com.querydsl.mini;

import com.querydsl.mini.expressions.PathExpression;
import com.querydsl.mini.factory.QueryFactory;
import com.querydsl.mini.sql.StandardSqlGenerator;

import static com.querydsl.mini.factory.ExpressionFactory.*;

/**
 * Simple test class to verify CRUD functionality without JUnit dependencies.
 * Can be run directly with java command.
 */
public class SimpleCrudTest {
    
    private static final QueryFactory queryFactory = new QueryFactory(null, new StandardSqlGenerator());
    
    public static void main(String[] args) {
        System.out.println("Testing QueryDSL Mini CRUD Operations...");
        
        try {
            testInsertWithValues();
            testUpdate();
            testDelete();
            
            System.out.println("All tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testInsertWithValues() {
        System.out.println("Testing INSERT...");
        
        String sql = queryFactory.insertInto("users")
                .columns("name", "age", "email")
                .values("Alice", 25, "alice@example.com")
                .toSQL();
        
        String expected = "INSERT INTO users (name, age, email) VALUES ('Alice', 25, 'alice@example.com')";
        if (!expected.equals(sql)) {
            throw new RuntimeException("INSERT test failed. Expected: " + expected + ", Got: " + sql);
        }
        
        System.out.println("INSERT test passed");
    }
    
    private static void testUpdate() {
        System.out.println("Testing UPDATE...");
        
        PathExpression<Integer> userId = path(Integer.class, "users", "id");
        
        String sql = queryFactory.update("users")
                .set("name", "Updated Name")
                .set("age", 26)
                .where(userId.eq(1))
                .toSQL();
        
        String expected = "UPDATE users SET name = 'Updated Name', age = 26 WHERE (users.id = 1)";
        if (!expected.equals(sql)) {
            throw new RuntimeException("UPDATE test failed. Expected: " + expected + ", Got: " + sql);
        }
        
        System.out.println("UPDATE test passed");
    }
    
    private static void testDelete() {
        System.out.println("Testing DELETE...");
        
        PathExpression<Integer> userAge = path(Integer.class, "users", "age");
        
        String sql = queryFactory.deleteFrom("users")
                .where(userAge.lt(18))
                .toSQL();
        
        String expected = "DELETE FROM users WHERE (users.age < 18)";
        if (!expected.equals(sql)) {
            throw new RuntimeException("DELETE test failed. Expected: " + expected + ", Got: " + sql);
        }
        
        System.out.println("DELETE test passed");
    }
} 