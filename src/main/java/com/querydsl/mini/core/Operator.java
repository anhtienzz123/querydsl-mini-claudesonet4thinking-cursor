package com.querydsl.mini.core;

/**
 * Enumeration of operators used in query expressions.
 * Supports various SQL operators for building type-safe queries.
 */
public enum Operator {
    // Comparison operators
    EQ("=", "equals"),
    NE("!=", "not equals"),
    LT("<", "less than"),
    LE("<=", "less than or equal"),
    GT(">", "greater than"),
    GE(">=", "greater than or equal"),
    
    // Logical operators
    AND("AND", "logical and"),
    OR("OR", "logical or"),
    NOT("NOT", "logical not"),
    
    // String operators
    LIKE("LIKE", "like"),
    NOT_LIKE("NOT LIKE", "not like"),
    IN("IN", "in"),
    NOT_IN("NOT IN", "not in"),
    
    // Null operators
    IS_NULL("IS NULL", "is null"),
    IS_NOT_NULL("IS NOT NULL", "is not null"),
    
    // Arithmetic operators
    PLUS("+", "addition"),
    MINUS("-", "subtraction"),
    MULT("*", "multiplication"),
    DIV("/", "division"),
    MOD("%", "modulo"),
    
    // Aggregate functions
    COUNT("COUNT", "count"),
    SUM("SUM", "sum"),
    AVG("AVG", "average"),
    MIN("MIN", "minimum"),
    MAX("MAX", "maximum");
    
    private final String symbol;
    private final String description;
    
    Operator(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return symbol;
    }
} 