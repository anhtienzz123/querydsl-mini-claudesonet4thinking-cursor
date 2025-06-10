package com.querydsl.mini.expressions;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.core.ExpressionVisitor;

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
}