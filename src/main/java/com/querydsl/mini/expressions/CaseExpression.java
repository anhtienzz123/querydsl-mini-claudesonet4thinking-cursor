package com.querydsl.mini.expressions;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.core.ExpressionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Expression representing a CASE WHEN ... THEN ... ELSE ... END expression.
 * 
 * @param <T> The result type of the case expression
 */
public class CaseExpression<T> extends AbstractExpression<T> {
    
    private final List<WhenClause<T>> whenClauses;
    private final Expression<T> elseExpression;
    
    public CaseExpression(Class<T> type) {
        super(type);
        this.whenClauses = new ArrayList<>();
        this.elseExpression = null;
    }
    
    private CaseExpression(Class<T> type, List<WhenClause<T>> whenClauses, Expression<T> elseExpression) {
        super(type);
        this.whenClauses = new ArrayList<>(whenClauses);
        this.elseExpression = elseExpression;
    }
    
    public CaseExpression<T> when(Expression<Boolean> condition, Expression<T> result) {
        List<WhenClause<T>> newClauses = new ArrayList<>(whenClauses);
        newClauses.add(new WhenClause<>(condition, result));
        return new CaseExpression<>(getType(), newClauses, elseExpression);
    }
    
    public CaseExpression<T> otherwise(Expression<T> defaultResult) {
        return new CaseExpression<>(getType(), whenClauses, defaultResult);
    }
    
    public List<WhenClause<T>> getWhenClauses() {
        return List.copyOf(whenClauses);
    }
    
    public Expression<T> getElseExpression() {
        return elseExpression;
    }
    
    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CASE");
        for (WhenClause<T> clause : whenClauses) {
            sb.append(" WHEN ").append(clause.condition())
              .append(" THEN ").append(clause.result());
        }
        if (elseExpression != null) {
            sb.append(" ELSE ").append(elseExpression);
        }
        sb.append(" END");
        return sb.toString();
    }
    
    @Override
    protected boolean equalsInternal(AbstractExpression<?> other) {
        if (!(other instanceof CaseExpression<?> that)) return false;
        return Objects.equals(whenClauses, that.whenClauses) &&
               Objects.equals(elseExpression, that.elseExpression);
    }
    
    @Override
    protected int hashCodeInternal() {
        return Objects.hash(whenClauses, elseExpression);
    }
    
    public record WhenClause<T>(Expression<Boolean> condition, Expression<T> result) {
        public WhenClause {
            Objects.requireNonNull(condition, "Condition cannot be null");
            Objects.requireNonNull(result, "Result cannot be null");
        }
    }
}