package com.querydsl.mini.factory;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.expressions.*;

import java.util.List;

/**
 * Factory class for creating expressions.
 * Implements the Factory pattern to centralize expression creation.
 */
public class ExpressionFactory {
    
    /**
     * Creates a path expression for a table column.
     */
    public static <T> PathExpression<T> path(Class<T> type, String table, String column) {
        return new PathExpression<>(type, table, column);
    }
    
    /**
     * Creates a path expression with an alias.
     */
    public static <T> PathExpression<T> path(Class<T> type, String table, String column, String alias) {
        return new PathExpression<>(type, table, column, alias);
    }
    
    /**
     * Creates a constant expression.
     */
    public static <T> ConstantExpression<T> constant(Class<T> type, T value) {
        return new ConstantExpression<>(type, value);
    }
    
    /**
     * Creates a string constant.
     */
    public static ConstantExpression<String> constant(String value) {
        return new ConstantExpression<>(String.class, value);
    }
    
    /**
     * Creates an integer constant.
     */
    public static ConstantExpression<Integer> constant(Integer value) {
        return new ConstantExpression<>(Integer.class, value);
    }
    
    /**
     * Creates a long constant.
     */
    public static ConstantExpression<Long> constant(Long value) {
        return new ConstantExpression<>(Long.class, value);
    }
    
    /**
     * Creates a constant expression for any value, determining type automatically.
     */
    @SuppressWarnings("unchecked")
    public static <T> ConstantExpression<T> constant(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Use nullExpression() for null values");
        }
        return new ConstantExpression<>((Class<T>) value.getClass(), value);
    }
    
    /**
     * Creates a NULL constant expression.
     */
    public static <T> ConstantExpression<T> nullExpression() {
        return new ConstantExpression<>(null, null);
    }
    
    /**
     * Creates a function expression.
     */
    public static <T> FunctionExpression<T> function(Class<T> type, String name, Expression<?>... args) {
        return new FunctionExpression<>(type, name, args);
    }
    
    /**
     * Creates a function expression with a list of arguments.
     */
    public static <T> FunctionExpression<T> function(Class<T> type, String name, List<Expression<?>> args) {
        return new FunctionExpression<>(type, name, args);
    }
    
    /**
     * Creates a COUNT function.
     */
    public static FunctionExpression<Long> count(Expression<?> expression) {
        return function(Long.class, "COUNT", expression);
    }
    
    /**
     * Creates a COUNT(*) function.
     */
    public static FunctionExpression<Long> countAll() {
        return function(Long.class, "COUNT", constant("*"));
    }
    
    /**
     * Creates a SUM function.
     */
    public static <T extends Number> FunctionExpression<T> sum(PathExpression<T> expression) {
        return function(expression.getType(), "SUM", expression);
    }
    
    /**
     * Creates an AVG function.
     */
    public static <T extends Number> FunctionExpression<Double> avg(PathExpression<T> expression) {
        return function(Double.class, "AVG", expression);
    }
    
    /**
     * Creates a MIN function.
     */
    public static <T> FunctionExpression<T> min(PathExpression<T> expression) {
        return function(expression.getType(), "MIN", expression);
    }
    
    /**
     * Creates a MAX function.
     */
    public static <T> FunctionExpression<T> max(PathExpression<T> expression) {
        return function(expression.getType(), "MAX", expression);
    }
    
    /**
     * Creates a CASE expression.
     */
    public static <T> CaseExpression<T> caseExpression(Class<T> type) {
        return new CaseExpression<>(type);
    }
    
    /**
     * Creates a subquery expression.
     */
    public static <T> SubQueryExpression<T> subQuery(Class<T> type, com.querydsl.mini.query.Query<T> query) {
        return new SubQueryExpression<>(type, query);
    }
} 