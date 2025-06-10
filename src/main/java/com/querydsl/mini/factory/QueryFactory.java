package com.querydsl.mini.factory;

import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.query.SelectQuery;
import com.querydsl.mini.sql.SqlGenerator;

/**
 * Factory class for creating queries.
 * Implements the Factory pattern to centralize query creation.
 */
public class QueryFactory {
    
    private final QueryExecutor executor;
    private final SqlGenerator sqlGenerator;
    
    public QueryFactory(QueryExecutor executor, SqlGenerator sqlGenerator) {
        this.executor = executor;
        this.sqlGenerator = sqlGenerator;
    }
    
    /**
     * Creates a new SELECT query.
     */
    public <T> SelectQuery<T> select(Class<T> resultType) {
        return new SelectQuery<>(resultType, executor, sqlGenerator);
    }
    
    /**
     * Creates a new SELECT query that returns Object arrays.
     */
    public SelectQuery<Object[]> select() {
        return new SelectQuery<>(Object[].class, executor, sqlGenerator);
    }
    
    /**
     * Creates a new SELECT query for String results.
     */
    public SelectQuery<String> selectString() {
        return new SelectQuery<>(String.class, executor, sqlGenerator);
    }
    
    /**
     * Creates a new SELECT query for Integer results.
     */
    public SelectQuery<Integer> selectInteger() {
        return new SelectQuery<>(Integer.class, executor, sqlGenerator);
    }
    
    /**
     * Creates a new SELECT query for Long results.
     */
    public SelectQuery<Long> selectLong() {
        return new SelectQuery<>(Long.class, executor, sqlGenerator);
    }
    
    /**
     * Creates a new SELECT query for Double results.
     */
    public SelectQuery<Double> selectDouble() {
        return new SelectQuery<>(Double.class, executor, sqlGenerator);
    }
    
    /**
     * Gets the query executor.
     */
    public QueryExecutor getExecutor() {
        return executor;
    }
    
    /**
     * Gets the SQL generator.
     */
    public SqlGenerator getSqlGenerator() {
        return sqlGenerator;
    }
}