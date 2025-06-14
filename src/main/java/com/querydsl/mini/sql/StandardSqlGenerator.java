package com.querydsl.mini.sql;

import com.querydsl.mini.core.Expression;
import com.querydsl.mini.core.ExpressionVisitor;
import com.querydsl.mini.expressions.*;
import com.querydsl.mini.query.AbstractQuery;
import com.querydsl.mini.query.InsertQuery;
import com.querydsl.mini.query.UpdateQuery;
import com.querydsl.mini.query.DeleteQuery;

import java.util.stream.Collectors;

/**
 * Standard SQL generator implementing the Visitor pattern.
 * Traverses expression trees to generate SQL strings.
 */
public class StandardSqlGenerator implements SqlGenerator, ExpressionVisitor<String, Void> {
    
    @Override
    public String generate(AbstractQuery<?, ?> query) {
        // Determine query type and generate appropriate SQL
        if (query instanceof InsertQuery<?> insertQuery) {
            return generateInsert(insertQuery);
        } else if (query instanceof UpdateQuery updateQuery) {
            return generateUpdate(updateQuery);
        } else if (query instanceof DeleteQuery deleteQuery) {
            return generateDelete(deleteQuery);
        } else {
            return generateSelect(query);
        }
    }
    
    private String generateSelect(AbstractQuery<?, ?> query) {
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        sql.append("SELECT ");
        if (query.getSelectExpressions().isEmpty()) {
            sql.append("*");
        } else {
            sql.append(query.getSelectExpressions().stream()
                    .map(expr -> expr.accept(this, null))
                    .collect(Collectors.joining(", ")));
        }
        
        // FROM clause
        if (query.getFromTable() != null) {
            sql.append(" FROM ").append(query.getFromTable());
            if (query.getFromAlias() != null) {
                sql.append(" ").append(query.getFromAlias());
            }
        }
        
        // JOIN clauses
        for (AbstractQuery.JoinClause join : query.getJoinClauses()) {
            sql.append(" ").append(join.type().getSql()).append(" ")
               .append(join.table());
            if (join.alias() != null) {
                sql.append(" ").append(join.alias());
            }
            sql.append(" ON ").append(join.condition().accept(this, null));
        }
        
        // WHERE clause
        if (query.getWhereCondition() != null) {
            sql.append(" WHERE ").append(query.getWhereCondition().accept(this, null));
        }
        
        // GROUP BY clause
        if (!query.getGroupByExpressions().isEmpty()) {
            sql.append(" GROUP BY ")
               .append(query.getGroupByExpressions().stream()
                       .map(expr -> expr.accept(this, null))
                       .collect(Collectors.joining(", ")));
        }
        
        // HAVING clause
        if (query.getHavingCondition() != null) {
            sql.append(" HAVING ").append(query.getHavingCondition().accept(this, null));
        }
        
        // ORDER BY clause
        if (!query.getOrderByClauses().isEmpty()) {
            sql.append(" ORDER BY ")
               .append(query.getOrderByClauses().stream()
                       .map(order -> order.expression().accept(this, null) + " " + order.direction())
                       .collect(Collectors.joining(", ")));
        }
        
        // LIMIT clause
        if (query.getLimitValue() != null) {
            sql.append(" LIMIT ").append(query.getLimitValue());
        }
        
        // OFFSET clause
        if (query.getOffsetValue() != null) {
            sql.append(" OFFSET ").append(query.getOffsetValue());
        }
        
        return sql.toString();
    }
    
    private String generateInsert(InsertQuery<?> query) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO ").append(query.getFromTable());
        
        // Columns clause
        if (!query.getColumns().isEmpty()) {
            sql.append(" (")
               .append(String.join(", ", query.getColumns()))
               .append(")");
        }
        
        // VALUES or SELECT clause
        if (query.hasSelectQuery()) {
            sql.append(" ").append(query.getSelectQuery().toSQL());
        } else if (query.hasValues()) {
            sql.append(" VALUES ");
            sql.append(query.getValuesBatch().stream()
                    .map(values -> values.stream()
                            .map(expr -> expr.accept(this, null))
                            .collect(Collectors.joining(", ", "(", ")")))
                    .collect(Collectors.joining(", ")));
        }
        
        return sql.toString();
    }
    
    private String generateUpdate(UpdateQuery query) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("UPDATE ").append(query.getFromTable());
        
        // SET clause
        if (!query.getSetClause().isEmpty()) {
            sql.append(" SET ");
            sql.append(query.getSetClause().entrySet().stream()
                    .map(entry -> entry.getKey() + " = " + entry.getValue().accept(this, null))
                    .collect(Collectors.joining(", ")));
        }
        
        // WHERE clause
        if (query.getWhereCondition() != null) {
            sql.append(" WHERE ").append(query.getWhereCondition().accept(this, null));
        }
        
        return sql.toString();
    }
    
    private String generateDelete(DeleteQuery query) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("DELETE FROM ").append(query.getFromTable());
        
        // WHERE clause
        if (query.getWhereCondition() != null) {
            sql.append(" WHERE ").append(query.getWhereCondition().accept(this, null));
        }
        
        return sql.toString();
    }
    
    @Override
    public SqlDialect getDialect() {
        return SqlDialect.STANDARD_SQL;
    }
    
    // Visitor pattern implementation for expressions
    
    @Override
    public String visit(PathExpression<?> expr, Void context) {
        StringBuilder result = new StringBuilder();
        result.append(expr.getTable()).append(".").append(expr.getColumn());
        if (expr.getAlias() != null) {
            result.append(" AS ").append(expr.getAlias());
        }
        return result.toString();
    }
    
    @Override
    public String visit(ConstantExpression<?> expr, Void context) {
        Object value = expr.getValue();
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + value.toString().replace("'", "''") + "'";
        }
        if (value instanceof java.time.LocalDate || value instanceof java.time.LocalDateTime) {
            return "'" + value.toString() + "'";
        }
        return value.toString();
    }
    
    @Override
    public String visit(BinaryOperationExpression<?> expr, Void context) {
        String left = expr.getLeft().accept(this, context);
        String operator = expr.getOperator().getSymbol();
        
        if (expr.getRight() == null) {
            // Unary operation like IS NULL
            return left + " " + operator;
        }
        
        String right = expr.getRight().accept(this, context);
        return "(" + left + " " + operator + " " + right + ")";
    }
    
    @Override
    public String visit(FunctionExpression<?> expr, Void context) {
        String args = expr.getArguments().stream()
                .map(arg -> arg.accept(this, context))
                .collect(Collectors.joining(", "));
        return expr.getFunctionName() + "(" + args + ")";
    }
    
    @Override
    public String visit(CaseExpression<?> expr, Void context) {
        StringBuilder result = new StringBuilder("CASE");
        
        for (CaseExpression.WhenClause<?> clause : expr.getWhenClauses()) {
            result.append(" WHEN ").append(clause.condition().accept(this, context))
                  .append(" THEN ").append(clause.result().accept(this, context));
        }
        
        if (expr.getElseExpression() != null) {
            result.append(" ELSE ").append(expr.getElseExpression().accept(this, context));
        }
        
        result.append(" END");
        return result.toString();
    }
    
    @Override
    public String visit(SubQueryExpression<?> expr, Void context) {
        return "(" + expr.getSubQuery().toSQL() + ")";
    }
}