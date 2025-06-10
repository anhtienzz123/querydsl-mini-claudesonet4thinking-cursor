package com.querydsl.mini.expressions;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.core.ExpressionVisitor;
import com.querydsl.mini.core.Operator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Expression representing a function call (e.g., COUNT(*), SUM(column)).
 * 
 * @param <T> The return type of the function
 */
public class FunctionExpression<T> extends AbstractExpression<T> {
    
    private final String functionName;
    private final List<Expression<?>> arguments;
    
    public FunctionExpression(Class<T> type, String functionName, Expression<?>... arguments) {
        this(type, functionName, Arrays.asList(arguments));
    }
    
    public FunctionExpression(Class<T> type, String functionName, List<Expression<?>> arguments) {
        super(type);
        this.functionName = Objects.requireNonNull(functionName, "Function name cannot be null");
        this.arguments = List.copyOf(Objects.requireNonNull(arguments, "Arguments cannot be null"));
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<Expression<?>> getArguments() {
        return arguments;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        String args = arguments.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return functionName + "(" + args + ")";
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof FunctionExpression<?> that)) return false;
        return Objects.equals(functionName, that.functionName) &&
               Objects.equals(arguments, that.arguments);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(functionName, arguments);
    }
    
    // Fluent API methods for building comparison expressions
    public BinaryOperationExpression<Boolean> eq(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.EQ, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> eq(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.EQ, other);
    }
    
    public BinaryOperationExpression<Boolean> ne(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.NE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> ne(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.NE, other);
    }
    
    public BinaryOperationExpression<Boolean> lt(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LT, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> lt(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LT, other);
    }
    
    public BinaryOperationExpression<Boolean> le(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> le(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LE, other);
    }
    
    public BinaryOperationExpression<Boolean> gt(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GT, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> gt(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GT, other);
    }
    
    public BinaryOperationExpression<Boolean> ge(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> ge(Expression<T> other) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GE, other);
    }
    
    public BinaryOperationExpression<Boolean> isNull() {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.IS_NULL, null);
    }
    
    public BinaryOperationExpression<Boolean> isNotNull() {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.IS_NOT_NULL, null);
    }
}