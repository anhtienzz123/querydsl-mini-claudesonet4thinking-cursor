package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.sql.SqlGenerator;
import com.querydsl.mini.execution.QueryExecutor;

/**
 * Concrete implementation of a SELECT query.
 * Implements the Builder pattern for fluent query construction.
 * 
 * @param <T> The result type of the query
 */
public class SelectQuery<T> extends AbstractQuery<T, SelectQuery<T>> {
    
    public SelectQuery(Class<T> resultType, QueryExecutor executor, SqlGenerator sqlGenerator) {
        super(resultType, executor, sqlGenerator);
    }
    
    @Override
    public <R> SelectQuery<R> select(Expression<R> expression) {
        return (SelectQuery<R>) createNewQuery(expression.getType())
                .copyStateFrom(this)
                .setSelectExpressions(java.util.List.of(expression));
    }
    
    @Override
    protected <R> AbstractQuery<R, ?> createNewQuery(Class<R> resultType) {
        return new SelectQuery<>(resultType, executor, sqlGenerator);
    }
}