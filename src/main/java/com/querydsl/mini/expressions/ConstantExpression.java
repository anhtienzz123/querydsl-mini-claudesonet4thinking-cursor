package com.querydsl.mini.expressions;

import com.querydsl.mini.core.ExpressionVisitor;

import java.util.Objects;

/**
 * Expression representing a constant/literal value.
 * 
 * @param <T> The type of the constant value
 */
public class ConstantExpression<T> extends AbstractExpression<T> {
    
    private final T value;
    
    public ConstantExpression(Class<T> type, T value) {
        super(type);
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof ConstantExpression<?> that)) return false;
        return Objects.equals(value, that.value);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(value);
    }
}