package com.querydsl.mini.expressions;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.core.ExpressionVisitor;
import com.querydsl.mini.core.Operator;

import java.util.Objects;

/**
 * Expression representing a binary operation between two expressions.
 * Implements the Composite pattern by combining two expressions.
 * 
 * @param <T> The result type of the operation
 */
public class BinaryOperationExpression<T> extends AbstractExpression<T> {
    
    private final Expression<?> left;
    private final Operator operator;
    private final Expression<?> right;
    
    public BinaryOperationExpression(Class<T> type, Expression<?> left, Operator operator, Expression<?> right) {
        super(type);
        this.left = Objects.requireNonNull(left, "Left expression cannot be null");
        this.operator = Objects.requireNonNull(operator, "Operator cannot be null");
        this.right = right; // Can be null for unary operations like IS NULL
    }
    
    public Expression<?> getLeft() {
        return left;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public Expression<?> getRight() {
        return right;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        if (right == null) {
            // Unary operation
            return left + " " + operator;
        }
        return "(" + left + " " + operator + " " + right + ")";
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof BinaryOperationExpression<?> that)) return false;
        return Objects.equals(left, that.left) &&
               Objects.equals(operator, that.operator) &&
               Objects.equals(right, that.right);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(left, operator, right);
    }
    
    // Fluent API methods for chaining logical operations
    public BinaryOperationExpression<Boolean> and(Expression<Boolean> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.AND, other);
    }
    
    public BinaryOperationExpression<Boolean> or(Expression<Boolean> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.OR, other);
    }
} 