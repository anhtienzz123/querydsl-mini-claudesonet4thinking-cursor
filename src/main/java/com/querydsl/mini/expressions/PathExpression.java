package com.querydsl.mini.expressions;

import com.querydsl.mini.core.ExpressionVisitor;
import com.querydsl.mini.core.Operator;

import java.util.Objects;

/**
 * Expression representing a path to a database column (e.g., table.column).
 * Implements the composite pattern as part of the expression tree.
 * 
 * @param <T> The type of the column
 */
public class PathExpression<T> extends AbstractExpression<T> {
    
    private final String table;
    private final String column;
    private final String alias;
    
    public PathExpression(Class<T> type, String table, String column) {
        this(type, table, column, null);
    }
    
    public PathExpression(Class<T> type, String table, String column, String alias) {
        super(type);
        this.table = Objects.requireNonNull(table, "Table name cannot be null");
        this.column = Objects.requireNonNull(column, "Column name cannot be null");
        this.alias = alias;
    }
    
    public String getTable() {
        return table;
    }
    
    public String getColumn() {
        return column;
    }
    
    public String getAlias() {
        return alias;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(table).append(".").append(column);
        if (alias != null) {
            sb.append(" AS ").append(alias);
        }
        return sb.toString();
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof PathExpression<?> that)) return false;
        return Objects.equals(table, that.table) &&
               Objects.equals(column, that.column) &&
               Objects.equals(alias, that.alias);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(table, column, alias);
    }
    
    // Fluent API methods for building expressions
    public BinaryOperationExpression<Boolean> eq(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.EQ, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> ne(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.NE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> lt(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LT, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> le(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> gt(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GT, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> ge(T value) {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.GE, new ConstantExpression<>(getType(), value));
    }
    
    public BinaryOperationExpression<Boolean> like(String pattern) {
        if (!String.class.equals(getType())) {
            throw new IllegalArgumentException("LIKE operation can only be used with String columns");
        }
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.LIKE, new ConstantExpression<>(String.class, pattern));
    }
    
    public BinaryOperationExpression<Boolean> isNull() {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.IS_NULL, null);
    }
    
    public BinaryOperationExpression<Boolean> isNotNull() {
        return new BinaryOperationExpression<>(Boolean.class, this, Operator.IS_NOT_NULL, null);
    }
}