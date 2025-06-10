package com.querydsl.mini.core;

/**
 * Base interface for all expressions in QueryDSL Mini.
 * Implements the Composite pattern to allow building complex expressions.
 * 
 * @param <T> The type of the expression result
 */
public interface Expression<T> {
    
    /**
     * Returns the Java type of this expression.
     */
    Class<T> getType();
    
    /**
     * Accepts a visitor for processing this expression.
     * Part of the Visitor pattern implementation.
     */
    <R, C> R accept(ExpressionVisitor<R, C> visitor, C context);
    
    /**
     * Returns a string representation of this expression.
     */
    String toString();
    
    /**
     * Checks if this expression equals another expression.
     */
    boolean equals(Object other);
    
    /**
     * Returns the hash code for this expression.
     */
    int hashCode();
} 