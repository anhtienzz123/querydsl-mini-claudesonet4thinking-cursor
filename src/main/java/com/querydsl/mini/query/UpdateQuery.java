package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.sql.SqlGenerator;

import java.util.*;

/**
 * Concrete implementation of an UPDATE query.
 * Implements the Builder pattern for fluent UPDATE statement construction.
 */
public class UpdateQuery extends AbstractQuery<Void, UpdateQuery> {
    
    private final Map<String, Expression<?>> setClause = new LinkedHashMap<>();
    
    public UpdateQuery(QueryExecutor executor, SqlGenerator sqlGenerator) {
        super(Void.class, executor, sqlGenerator);
    }
    
    /**
     * Adds a SET clause to the UPDATE statement.
     */
    public UpdateQuery set(String column, Expression<?> value) {
        Objects.requireNonNull(column, "Column cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");
        setClause.put(column, value);
        return this;
    }
    
    /**
     * Adds a SET clause with a constant value.
     */
    @SuppressWarnings("unchecked")
    public UpdateQuery set(String column, Object value) {
        Objects.requireNonNull(column, "Column cannot be null");
        Expression<?> valueExpr;
        if (value == null) {
            valueExpr = com.querydsl.mini.factory.ExpressionFactory.nullExpression();
        } else {
            valueExpr = (Expression<?>) com.querydsl.mini.factory.ExpressionFactory.constant(value);
        }
        return set(column, valueExpr);
    }
    
    /**
     * Executes the UPDATE and returns the number of affected rows.
     */
    public long execute() {
        return executor.executeUpdate(this);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected <R> AbstractQuery<R, ?> createNewQuery(Class<R> resultType) {
        return (AbstractQuery<R, ?>) new UpdateQuery(executor, sqlGenerator);
    }
    
    // Getters for SQL generation
    public Map<String, Expression<?>> getSetClause() {
        return Collections.unmodifiableMap(setClause);
    }
    
    // Override unsupported operations
    @Override
    public <R> AbstractQuery<R, ?> select(Expression<R> expression) {
        throw new UnsupportedOperationException("SELECT is not supported on UPDATE queries");
    }
    
    @Override
    public Query<Object[]> select(Expression<?>... expressions) {
        throw new UnsupportedOperationException("SELECT is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery orderBy(Expression<?> expression) {
        throw new UnsupportedOperationException("ORDER BY is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery orderBy(Expression<?> expression, OrderDirection direction) {
        throw new UnsupportedOperationException("ORDER BY is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery groupBy(Expression<?>... expressions) {
        throw new UnsupportedOperationException("GROUP BY is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery having(Expression<Boolean> condition) {
        throw new UnsupportedOperationException("HAVING is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery limit(long limit) {
        throw new UnsupportedOperationException("LIMIT is not supported on UPDATE queries");
    }
    
    @Override
    public UpdateQuery offset(long offset) {
        throw new UnsupportedOperationException("OFFSET is not supported on UPDATE queries");
    }
    
    @Override
    public List<Void> fetch() {
        throw new UnsupportedOperationException("Use execute() for UPDATE queries");
    }
    
    @Override
    public Void fetchOne() {
        throw new UnsupportedOperationException("Use execute() for UPDATE queries");
    }
    
    @Override
    public Void fetchFirst() {
        throw new UnsupportedOperationException("Use execute() for UPDATE queries");
    }
    
    @Override
    public long fetchCount() {
        throw new UnsupportedOperationException("Use execute() for UPDATE queries");
    }
} 