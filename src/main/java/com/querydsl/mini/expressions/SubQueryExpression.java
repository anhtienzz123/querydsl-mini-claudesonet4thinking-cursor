package com.querydsl.mini.expressions;

import com.querydsl.mini.core.ExpressionVisitor;
import com.querydsl.mini.query.Query;

import java.util.Objects;

/**
 * Expression representing a subquery.
 * 
 * @param <T> The result type of the subquery
 */
public class SubQueryExpression<T> extends AbstractExpression<T> {
    
    private final Query<T> subQuery;
    
    public SubQueryExpression(Class<T> type, Query<T> subQuery) {
        super(type);
        this.subQuery = Objects.requireNonNull(subQuery, "Subquery cannot be null");
    }
    
    public Query<T> getSubQuery() {
        return subQuery;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        return "(" + subQuery.toString() + ")";
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof SubQueryExpression<?> that)) return false;
        return Objects.equals(subQuery, that.subQuery);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(subQuery);
    }
}