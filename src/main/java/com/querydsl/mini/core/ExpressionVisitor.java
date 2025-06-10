package com.querydsl.mini.core;

import com.querydsl.mini.expressions.*;

/**
 * Visitor interface for traversing and processing expression trees.
 * Implements the Visitor pattern to separate operations from the object structure.
 * 
 * @param <R> Return type of visit methods
 * @param <C> Context type passed to visit methods
 */
public interface ExpressionVisitor<R, C> {
    
    /**
     * Visit a path expression (table.column)
     */
    R visit(PathExpression<?> expr, C context);
    
    /**
     * Visit a constant expression (literal value)
     */
    R visit(ConstantExpression<?> expr, C context);
    
    /**
     * Visit a binary operation expression (column = value)
     */
    R visit(BinaryOperationExpression<?> expr, C context);
    
    /**
     * Visit a function call expression (COUNT, SUM, etc.)
     */
    R visit(FunctionExpression<?> expr, C context);
    
    /**
     * Visit a case expression (CASE WHEN ... THEN ... ELSE ... END)
     */
    R visit(CaseExpression<?> expr, C context);
    
    /**
     * Visit a subquery expression
     */
    R visit(SubQueryExpression<?> expr, C context);
    
    /**
     * Default visit method for unknown expression types
     */
    default R visit(Expression<?> expr, C context) {
        throw new UnsupportedOperationException(
            "Visitor does not support expression type: " + expr.getClass().getSimpleName()
        );
    }
} 