package com.querydsl.mini.sql;

import com.querydsl.mini.query.AbstractQuery;

/**
 * Strategy interface for generating SQL from query objects.
 * Implements the Strategy pattern to allow different SQL dialects.
 */
public interface SqlGenerator {
    
    /**
     * Generates SQL string from a query object.
     * 
     * @param query The query to generate SQL for
     * @return SQL string representation
     */
    String generate(AbstractQuery<?, ?> query);
    
    /**
     * Returns the SQL dialect this generator supports.
     */
    SqlDialect getDialect();
    
    enum SqlDialect {
        STANDARD_SQL("Standard SQL"),
        MYSQL("MySQL"),
        POSTGRESQL("PostgreSQL"),
        H2("H2 Database"),
        ORACLE("Oracle Database");
        
        private final String displayName;
        
        SqlDialect(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}