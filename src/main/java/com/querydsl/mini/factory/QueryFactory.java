package com.querydsl.mini.factory;

import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.query.SelectQuery;
import com.querydsl.mini.query.InsertQuery;
import com.querydsl.mini.query.UpdateQuery;
import com.querydsl.mini.query.DeleteQuery;
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
    
    /**
     * Creates a new INSERT query.
     */
    public <T> InsertQuery<T> insertInto(String tableName, Class<T> keyType) {
        return new InsertQuery<>(keyType, executor, sqlGenerator).from(tableName);
    }
    
    /**
     * Creates a new INSERT query with Long key type (common for auto-generated keys).
     */
    public InsertQuery<Long> insertInto(String tableName) {
        return insertInto(tableName, Long.class);
    }
    
    /**
     * Creates a new UPDATE query.
     */
    public UpdateQuery update(String tableName) {
        return new UpdateQuery(executor, sqlGenerator).from(tableName);
    }
    
    /**
     * Creates a new DELETE query.
     */
    public DeleteQuery deleteFrom(String tableName) {
        return new DeleteQuery(executor, sqlGenerator).from(tableName);
    }
    
    /**
     * Creates a new DELETE query (alias for deleteFrom).
     */
    public DeleteQuery delete() {
        return new DeleteQuery(executor, sqlGenerator);
    }
}