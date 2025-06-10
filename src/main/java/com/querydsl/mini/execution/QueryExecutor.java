package com.querydsl.mini.execution;

import com.querydsl.mini.query.AbstractQuery;

import java.util.List;

/**
 * Strategy interface for executing queries against different data sources.
 * Implements the Strategy pattern to support various execution backends.
 */
public interface QueryExecutor {
    
    /**
     * Executes a query and returns the results.
     * 
     * @param query The query to execute
     * @param <T> The result type
     * @return List of results
     */
    <T> List<T> executeQuery(AbstractQuery<T, ?> query);
    
    /**
     * Executes a count query and returns the number of rows.
     * 
     * @param query The query to count
     * @return Number of rows
     */
    long executeCount(AbstractQuery<?, ?> query);
    
    /**
     * Closes the executor and releases any resources.
     */
    void close();
}