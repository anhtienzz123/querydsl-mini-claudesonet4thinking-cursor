package com.querydsl.mini;

import com.querydsl.mini.execution.JdbcQueryExecutor;
import com.querydsl.mini.execution.QueryExecutor;
import com.querydsl.mini.factory.ExpressionFactory;
import com.querydsl.mini.factory.QueryFactory;
import com.querydsl.mini.sql.SqlGenerator;
import com.querydsl.mini.sql.StandardSqlGenerator;

import javax.sql.DataSource;

/**
 * Main entry point for QueryDSL Mini.
 * Implements the Facade pattern to provide a simple interface to the complex subsystem.
 * Also demonstrates the Static Factory Method pattern.
 */
public class QueryDSL {
    
    private final QueryFactory queryFactory;
    private final QueryExecutor executor;
    
    private QueryDSL(QueryFactory queryFactory, QueryExecutor executor) {
        this.queryFactory = queryFactory;
        this.executor = executor;
    }
    
    /**
     * Creates a new QueryDSL instance with a JDBC data source.
     * Uses the default StandardSqlGenerator.
     */
    public static QueryDSL withDataSource(DataSource dataSource) {
        return withDataSource(dataSource, new StandardSqlGenerator());
    }
    
    /**
     * Creates a new QueryDSL instance with a JDBC data source and custom SQL generator.
     */
    public static QueryDSL withDataSource(DataSource dataSource, SqlGenerator sqlGenerator) {
        QueryExecutor executor = new JdbcQueryExecutor(dataSource);
        QueryFactory queryFactory = new QueryFactory(executor, sqlGenerator);
        return new QueryDSL(queryFactory, executor);
    }
    
    /**
     * Creates a new QueryDSL instance with a custom executor and SQL generator.
     */
    public static QueryDSL withExecutor(QueryExecutor executor, SqlGenerator sqlGenerator) {
        QueryFactory queryFactory = new QueryFactory(executor, sqlGenerator);
        return new QueryDSL(queryFactory, executor);
    }
    
    /**
     * Gets the query factory for creating queries.
     */
    public QueryFactory getQueryFactory() {
        return queryFactory;
    }
    
    /**
     * Gets the expression factory for creating expressions.
     */
    public ExpressionFactory getExpressionFactory() {
        return new ExpressionFactory();
    }
    
    /**
     * Convenience method to access expression factory statically.
     */
    public static ExpressionFactory expressions() {
        return new ExpressionFactory();
    }
    
    /**
     * Closes the QueryDSL instance and releases resources.
     */
    public void close() {
        if (executor != null) {
            executor.close();
        }
    }
    
    /**
     * Implements AutoCloseable for try-with-resources support.
     */
    public static class AutoCloseableQueryDSL extends QueryDSL implements AutoCloseable {
        
        private AutoCloseableQueryDSL(QueryFactory queryFactory, QueryExecutor executor) {
            super(queryFactory, executor);
        }
        
        public static AutoCloseableQueryDSL withDataSource(DataSource dataSource) {
            QueryExecutor executor = new JdbcQueryExecutor(dataSource);
            QueryFactory queryFactory = new QueryFactory(executor, new StandardSqlGenerator());
            return new AutoCloseableQueryDSL(queryFactory, executor);
        }
        
        @Override
        public void close() {
            super.close();
        }
    }
}