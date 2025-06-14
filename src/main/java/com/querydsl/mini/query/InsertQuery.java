package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.sql.SqlGenerator;

import java.util.*;

/**
 * Concrete implementation of an INSERT query.
 * Implements the Builder pattern for fluent INSERT statement construction.
 * 
 * @param <T> The result type (typically the key type for generated keys)
 */
public class InsertQuery<T> extends AbstractQuery<T, InsertQuery<T>> {
    
    private final List<String> columns = new ArrayList<>();
    private final List<List<Expression<?>>> valuesBatch = new ArrayList<>();
    private AbstractQuery<?, ?> selectQuery;
    
    public InsertQuery(Class<T> resultType, QueryExecutor executor, SqlGenerator sqlGenerator) {
        super(resultType, executor, sqlGenerator);
    }
    
    /**
     * Sets the columns to insert into.
     */
    public InsertQuery<T> columns(String... columnNames) {
        Objects.requireNonNull(columnNames, "Column names cannot be null");
        this.columns.clear();
        this.columns.addAll(Arrays.asList(columnNames));
        return this;
    }
    
    /**
     * Adds a row of values to insert.
     */
    public InsertQuery<T> values(Expression<?>... values) {
        Objects.requireNonNull(values, "Values cannot be null");
        if (!columns.isEmpty() && values.length != columns.size()) {
            throw new IllegalArgumentException("Number of values (" + values.length + 
                ") must match number of columns (" + columns.size() + ")");
        }
        valuesBatch.add(Arrays.asList(values));
        return this;
    }
    
    /**
     * Adds a row of constant values to insert.
     */
    @SuppressWarnings("unchecked")
    public InsertQuery<T> values(Object... values) {
        Objects.requireNonNull(values, "Values cannot be null");
        Expression<?>[] expressions = new Expression[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                expressions[i] = com.querydsl.mini.factory.ExpressionFactory.nullExpression();
            } else {
                expressions[i] = (Expression<?>) com.querydsl.mini.factory.ExpressionFactory.constant(values[i]);
            }
        }
        return values(expressions);
    }
    
    /**
     * Sets the INSERT query to use a SELECT statement for the data.
     */
    public InsertQuery<T> select(AbstractQuery<?, ?> selectQuery) {
        Objects.requireNonNull(selectQuery, "Select query cannot be null");
        this.selectQuery = selectQuery;
        this.valuesBatch.clear(); // Clear any existing values
        return this;
    }
    
    /**
     * Executes the INSERT and returns the number of affected rows.
     */
    public long execute() {
        return executor.executeUpdate(this);
    }
    
    /**
     * Executes the INSERT and returns the generated keys.
     */
    public List<T> executeAndReturnKeys() {
        return executor.executeInsert(this, resultType);
    }
    
    @Override
    protected <R> AbstractQuery<R, ?> createNewQuery(Class<R> resultType) {
        return new InsertQuery<>(resultType, executor, sqlGenerator);
    }
    
    // Getters for SQL generation
    public List<String> getColumns() {
        return Collections.unmodifiableList(columns);
    }
    
    public List<List<Expression<?>>> getValuesBatch() {
        return Collections.unmodifiableList(valuesBatch);
    }
    
    public AbstractQuery<?, ?> getSelectQuery() {
        return selectQuery;
    }
    
    public boolean hasSelectQuery() {
        return selectQuery != null;
    }
    
    public boolean hasValues() {
        return !valuesBatch.isEmpty();
    }
    
    // Override unsupported operations
    @Override
    public <R> AbstractQuery<R, ?> select(Expression<R> expression) {
        throw new UnsupportedOperationException("SELECT is not supported on INSERT queries");
    }
    
    @Override
    public Query<Object[]> select(Expression<?>... expressions) {
        throw new UnsupportedOperationException("SELECT is not supported on INSERT queries");
    }
    
    @Override
    public List<T> fetch() {
        throw new UnsupportedOperationException("Use execute() or executeAndReturnKeys() for INSERT queries");
    }
    
    @Override
    public T fetchOne() {
        throw new UnsupportedOperationException("Use execute() or executeAndReturnKeys() for INSERT queries");
    }
    
    @Override
    public T fetchFirst() {
        throw new UnsupportedOperationException("Use execute() or executeAndReturnKeys() for INSERT queries");
    }
    
    @Override
    public long fetchCount() {
        throw new UnsupportedOperationException("Use execute() for INSERT queries");
    }
}