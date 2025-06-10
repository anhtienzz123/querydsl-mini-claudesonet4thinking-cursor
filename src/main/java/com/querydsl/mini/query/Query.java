package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;

import java.util.List;

/**
 * Main query interface implementing the Builder pattern for fluent query building.
 * This is the core interface that provides the DSL for building type-safe queries.
 * 
 * @param <T> The result type of the query
 */
public interface Query<T> {
    
    /**
     * Adds a FROM clause to the query.
     */
    Query<T> from(String table);
    
    /**
     * Adds a FROM clause with an alias.
     */
    Query<T> from(String table, String alias);
    
    /**
     * Adds a WHERE clause to the query.
     */
    Query<T> where(Expression<Boolean> condition);
    
    /**
     * Adds a SELECT clause to the query.
     */
    <R> Query<R> select(Expression<R> expression);
    
    /**
     * Adds multiple SELECT expressions.
     */
    Query<Object[]> select(Expression<?>... expressions);
    
    /**
     * Adds an ORDER BY clause.
     */
    Query<T> orderBy(Expression<?> expression);
    
    /**
     * Adds an ORDER BY clause with direction.
     */
    Query<T> orderBy(Expression<?> expression, OrderDirection direction);
    
    /**
     * Adds a GROUP BY clause.
     */
    Query<T> groupBy(Expression<?>... expressions);
    
    /**
     * Adds a HAVING clause.
     */
    Query<T> having(Expression<Boolean> condition);
    
    /**
     * Adds a LIMIT clause.
     */
    Query<T> limit(long limit);
    
    /**
     * Adds an OFFSET clause.
     */
    Query<T> offset(long offset);
    
    /**
     * Adds a JOIN clause.
     */
    Query<T> join(String table, String alias, Expression<Boolean> condition);
    
    /**
     * Adds a LEFT JOIN clause.
     */
    Query<T> leftJoin(String table, String alias, Expression<Boolean> condition);
    
    /**
     * Adds a RIGHT JOIN clause.
     */
    Query<T> rightJoin(String table, String alias, Expression<Boolean> condition);
    
    /**
     * Executes the query and returns a list of results.
     */
    List<T> fetch();
    
    /**
     * Executes the query and returns a single result.
     */
    T fetchOne();
    
    /**
     * Executes the query and returns the first result.
     */
    T fetchFirst();
    
    /**
     * Returns the number of results without fetching them.
     */
    long fetchCount();
    
    /**
     * Returns the SQL string representation of this query.
     */
    String toSQL();
    
    /**
     * Returns a string representation of this query.
     */
    String toString();
    
    enum OrderDirection {
        ASC, DESC
    }
}