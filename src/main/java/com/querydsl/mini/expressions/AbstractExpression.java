package com.querydsl.mini.expressions;

import com.querydsl.mini.core.Expression;

import java.util.Objects;

/**
 * Abstract base class for all expressions.
 * Provides common functionality and implements Template Method pattern.
 * 
 * @param <T> The type of the expression result
 */
public abstract class AbstractExpression<T> implements Expression<T> {
    
    private final Class<T> type;
    
    protected AbstractExpression(Class<T> type) {
        this.type = Objects.requireNonNull(type, "Type cannot be null");
    }
    
    @Override
    public Class<T> getType() {
        return type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AbstractExpression<?> that = (AbstractExpression<?>) obj;
        return Objects.equals(type, that.type) && equalsInternal(that);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, hashCodeInternal());
    }
    
    /**
     * Template method for subclass-specific equality checking.
     * Subclasses should override this to provide their own equality logic.
     */
    protected boolean equalsInternal(AbstractExpression<?> other) {
        return true;
    }
    
    /**
     * Template method for subclass-specific hash code calculation.
     * Subclasses should override this to provide their own hash code logic.
     */
    protected int hashCodeInternal() {
        return 0;
    }
}