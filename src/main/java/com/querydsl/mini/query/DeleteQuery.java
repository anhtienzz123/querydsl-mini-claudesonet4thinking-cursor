package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.sql.SqlGenerator;

import java.util.List;

/**
 * Concrete implementation of a DELETE query.
 * Implements the Builder pattern for fluent DELETE statement construction.
 */
public class DeleteQuery extends AbstractQuery<Void, DeleteQuery> {
    
    public DeleteQuery(QueryExecutor executor, SqlGenerator sqlGenerator) {
        super(Void.class, executor, sqlGenerator);
    }
    
    /**
     * Executes the DELETE and returns the number of affected rows.
     */
    public long execute() {
        return executor.executeUpdate(this);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected <R> AbstractQuery<R, ?> createNewQuery(Class<R> resultType) {
        return (AbstractQuery<R, ?>) new DeleteQuery(executor, sqlGenerator);
    }
    
    // Override unsupported operations
    @Override
    public <R> AbstractQuery<R, ?> select(Expression<R> expression) {
        throw new UnsupportedOperationException("SELECT is not supported on DELETE queries");
    }
    
    @Override
    public Query<Object[]> select(Expression<?>... expressions) {
        throw new UnsupportedOperationException("SELECT is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery orderBy(Expression<?> expression) {
        throw new UnsupportedOperationException("ORDER BY is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery orderBy(Expression<?> expression, OrderDirection direction) {
        throw new UnsupportedOperationException("ORDER BY is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery groupBy(Expression<?>... expressions) {
        throw new UnsupportedOperationException("GROUP BY is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery having(Expression<Boolean> condition) {
        throw new UnsupportedOperationException("HAVING is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery limit(long limit) {
        throw new UnsupportedOperationException("LIMIT is not supported on DELETE queries");
    }
    
    @Override
    public DeleteQuery offset(long offset) {
        throw new UnsupportedOperationException("OFFSET is not supported on DELETE queries");
    }
    
    @Override
    public List<Void> fetch() {
        throw new UnsupportedOperationException("Use execute() for DELETE queries");
    }
    
    @Override
    public Void fetchOne() {
        throw new UnsupportedOperationException("Use execute() for DELETE queries");
    }
    
    @Override
    public Void fetchFirst() {
        throw new UnsupportedOperationException("Use execute() for DELETE queries");
    }
    
    @Override
    public long fetchCount() {
        throw new UnsupportedOperationException("Use execute() for DELETE queries");
    }
}