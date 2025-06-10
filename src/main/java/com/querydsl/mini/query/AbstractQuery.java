package com.querydsl.mini.query;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.sql.SqlGenerator;
import com.querydsl.mini.execution.QueryExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for queries implementing the Template Method pattern.
 * Provides common functionality for all query types.
 * 
 * @param <T> The result type of the query
 * @param <Q> The concrete query type (for fluent interface)
 */
public abstract class AbstractQuery<T, Q extends AbstractQuery<T, Q>> implements Query<T> {
    
    protected final QueryExecutor executor;
    protected final SqlGenerator sqlGenerator;
    protected final Class<T> resultType;
    
    // Query clauses
    protected String fromTable;
    protected String fromAlias;
    protected Expression<Boolean> whereCondition;
    protected List<Expression<?>> selectExpressions = new ArrayList<>();
    protected List<OrderByClause> orderByClauses = new ArrayList<>();
    protected List<Expression<?>> groupByExpressions = new ArrayList<>();
    protected Expression<Boolean> havingCondition;
    protected Long limitValue;
    protected Long offsetValue;
    protected List<JoinClause> joinClauses = new ArrayList<>();
    
    protected AbstractQuery(Class<T> resultType, QueryExecutor executor, SqlGenerator sqlGenerator) {
        this.resultType = Objects.requireNonNull(resultType, "Result type cannot be null");
        this.executor = Objects.requireNonNull(executor, "Executor cannot be null");
        this.sqlGenerator = Objects.requireNonNull(sqlGenerator, "SQL generator cannot be null");
    }
    
    @Override
    public Q from(String table) {
        return from(table, null);
    }
    
    @Override
    public Q from(String table, String alias) {
        this.fromTable = Objects.requireNonNull(table, "Table name cannot be null");
        this.fromAlias = alias;
        return self();
    }
    
    @Override
    public Q where(Expression<Boolean> condition) {
        this.whereCondition = Objects.requireNonNull(condition, "Condition cannot be null");
        return self();
    }
    
    @Override
    public Query<Object[]> select(Expression<?>... expressions) {
        return createNewQuery(Object[].class)
                .copyStateFrom(this)
                .setSelectExpressions(Arrays.asList(expressions));
    }
    
    @Override
    public Q orderBy(Expression<?> expression) {
        return orderBy(expression, OrderDirection.ASC);
    }
    
    @Override
    public Q orderBy(Expression<?> expression, OrderDirection direction) {
        Objects.requireNonNull(expression, "Expression cannot be null");
        Objects.requireNonNull(direction, "Direction cannot be null");
        orderByClauses.add(new OrderByClause(expression, direction));
        return self();
    }
    
    @Override
    public Q groupBy(Expression<?>... expressions) {
        Objects.requireNonNull(expressions, "Expressions cannot be null");
        groupByExpressions.addAll(Arrays.asList(expressions));
        return self();
    }
    
    @Override
    public Q having(Expression<Boolean> condition) {
        this.havingCondition = Objects.requireNonNull(condition, "Condition cannot be null");
        return self();
    }
    
    @Override
    public Q limit(long limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }
        this.limitValue = limit;
        return self();
    }
    
    @Override
    public Q offset(long offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }
        this.offsetValue = offset;
        return self();
    }
    
    @Override
    public Q join(String table, String alias, Expression<Boolean> condition) {
        return addJoin(JoinType.INNER, table, alias, condition);
    }
    
    @Override
    public Q leftJoin(String table, String alias, Expression<Boolean> condition) {
        return addJoin(JoinType.LEFT, table, alias, condition);
    }
    
    @Override
    public Q rightJoin(String table, String alias, Expression<Boolean> condition) {
        return addJoin(JoinType.RIGHT, table, alias, condition);
    }
    
    protected Q addJoin(JoinType type, String table, String alias, Expression<Boolean> condition) {
        Objects.requireNonNull(type, "Join type cannot be null");
        Objects.requireNonNull(table, "Table name cannot be null");
        Objects.requireNonNull(condition, "Join condition cannot be null");
        joinClauses.add(new JoinClause(type, table, alias, condition));
        return self();
    }
    
    @Override
    public List<T> fetch() {
        return executor.executeQuery(this);
    }
    
    @Override
    public T fetchOne() {
        List<T> results = fetch();
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IllegalStateException("Expected single result, but found " + results.size());
        }
        return results.get(0);
    }
    
    @Override
    public T fetchFirst() {
        List<T> results = limit(1).fetch();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public long fetchCount() {
        return executor.executeCount(this);
    }
    
    @Override
    public String toSQL() {
        return sqlGenerator.generate(this);
    }
    
    @Override
    public String toString() {
        return toSQL();
    }
    
    // Template method for subclasses to return themselves for fluent interface
    @SuppressWarnings("unchecked")
    protected Q self() {
        return (Q) this;
    }
    
    // Template method for creating new query instances
    protected abstract <R> AbstractQuery<R, ?> createNewQuery(Class<R> resultType);
    
    // Template method for copying state between queries
    protected Q copyStateFrom(AbstractQuery<?, ?> other) {
        this.fromTable = other.fromTable;
        this.fromAlias = other.fromAlias;
        this.whereCondition = other.whereCondition;
        this.orderByClauses = new ArrayList<>(other.orderByClauses);
        this.groupByExpressions = new ArrayList<>(other.groupByExpressions);
        this.havingCondition = other.havingCondition;
        this.limitValue = other.limitValue;
        this.offsetValue = other.offsetValue;
        this.joinClauses = new ArrayList<>(other.joinClauses);
        return self();
    }
    
    protected Q setSelectExpressions(List<Expression<?>> expressions) {
        this.selectExpressions = new ArrayList<>(expressions);
        return self();
    }
    
    // Getters for SQL generation
    public Class<T> getResultType() { return resultType; }
    public String getFromTable() { return fromTable; }
    public String getFromAlias() { return fromAlias; }
    public Expression<Boolean> getWhereCondition() { return whereCondition; }
    public List<Expression<?>> getSelectExpressions() { return selectExpressions; }
    public List<OrderByClause> getOrderByClauses() { return orderByClauses; }
    public List<Expression<?>> getGroupByExpressions() { return groupByExpressions; }
    public Expression<Boolean> getHavingCondition() { return havingCondition; }
    public Long getLimitValue() { return limitValue; }
    public Long getOffsetValue() { return offsetValue; }
    public List<JoinClause> getJoinClauses() { return joinClauses; }
    
    // Inner classes for query clauses
    public record OrderByClause(Expression<?> expression, OrderDirection direction) {}
    
    public record JoinClause(JoinType type, String table, String alias, Expression<Boolean> condition) {}
    
    public enum JoinType {
        INNER("INNER JOIN"),
        LEFT("LEFT JOIN"),
        RIGHT("RIGHT JOIN");
        
        private final String sql;
        
        JoinType(String sql) {
            this.sql = sql;
        }
        
        public String getSql() {
            return sql;
        }
    }
}