package com.querydsl.mini.execution;

import com.querydsl.mini.query.AbstractQuery;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of QueryExecutor.
 * Uses Chain of Responsibility pattern for result processing.
 */
public class JdbcQueryExecutor implements QueryExecutor {
    
    private final DataSource dataSource;
    private final ResultProcessor resultProcessor;
    
    public JdbcQueryExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
        this.resultProcessor = createResultProcessorChain();
    }
    
    @Override
    public <T> List<T> executeQuery(AbstractQuery<T, ?> query) {
        String sql = query.toSQL();
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            return resultProcessor.process(resultSet, query.getResultType());
            
        } catch (SQLException e) {
            throw new QueryExecutionException("Failed to execute query: " + sql, e);
        }
    }
    
    @Override
    public long executeCount(AbstractQuery<?, ?> query) {
        // Create a count query
        String originalSql = query.toSQL();
        String countSql = "SELECT COUNT(*) FROM (" + originalSql + ") AS count_query";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(countSql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            throw new QueryExecutionException("Failed to execute count query", e);
        }
    }
    
    @Override
    public long executeUpdate(AbstractQuery<?, ?> query) {
        String sql = query.toSQL();
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            return statement.executeUpdate();
            
        } catch (SQLException e) {
            throw new QueryExecutionException("Failed to execute update query: " + sql, e);
        }
    }
    
    @Override
    public <T> List<T> executeInsert(AbstractQuery<?, ?> query, Class<T> keyType) {
        String sql = query.toSQL();
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                return resultProcessor.process(generatedKeys, keyType);
            }
            
        } catch (SQLException e) {
            throw new QueryExecutionException("Failed to execute insert query: " + sql, e);
        }
    }
    
    @Override
    public void close() {
        // DataSource cleanup would go here if needed
    }
    
    /**
     * Creates a chain of result processors using Chain of Responsibility pattern.
     */
    private ResultProcessor createResultProcessorChain() {
        ResultProcessor stringProcessor = new StringResultProcessor();
        ResultProcessor numberProcessor = new NumberResultProcessor();
        ResultProcessor dateProcessor = new DateResultProcessor();
        ResultProcessor arrayProcessor = new ArrayResultProcessor();
        ResultProcessor defaultProcessor = new DefaultResultProcessor();
        
        // Chain them together
        stringProcessor.setNext(numberProcessor);
        numberProcessor.setNext(dateProcessor);
        dateProcessor.setNext(arrayProcessor);
        arrayProcessor.setNext(defaultProcessor);
        
        return stringProcessor;
    }
    
    /**
     * Base class for result processors implementing Chain of Responsibility.
     */
    public abstract static class ResultProcessor {
        protected ResultProcessor next;
        
        public void setNext(ResultProcessor next) {
            this.next = next;
        }
        
        public abstract <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException;
        
        protected <T> List<T> processNext(ResultSet resultSet, Class<T> targetType) throws SQLException {
            if (next != null) {
                return next.process(resultSet, targetType);
            }
            throw new IllegalArgumentException("Unsupported result type: " + targetType);
        }
    }
    
    /**
     * Processes String results.
     */
    public static class StringResultProcessor extends ResultProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException {
            if (String.class.equals(targetType)) {
                List<String> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(resultSet.getString(1));
                }
                return (List<T>) results;
            }
            return processNext(resultSet, targetType);
        }
    }
    
    /**
     * Processes Number results.
     */
    public static class NumberResultProcessor extends ResultProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException {
            if (Number.class.isAssignableFrom(targetType)) {
                List<Number> results = new ArrayList<>();
                while (resultSet.next()) {
                    if (Integer.class.equals(targetType)) {
                        results.add(resultSet.getInt(1));
                    } else if (Long.class.equals(targetType)) {
                        results.add(resultSet.getLong(1));
                    } else if (Double.class.equals(targetType)) {
                        results.add(resultSet.getDouble(1));
                    } else {
                        results.add(resultSet.getBigDecimal(1));
                    }
                }
                return (List<T>) results;
            }
            return processNext(resultSet, targetType);
        }
    }
    
    /**
     * Processes Date/Time results.
     */
    public static class DateResultProcessor extends ResultProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException {
            if (java.time.LocalDate.class.equals(targetType) || 
                java.time.LocalDateTime.class.equals(targetType) || 
                java.util.Date.class.equals(targetType)) {
                List<Object> results = new ArrayList<>();
                while (resultSet.next()) {
                    if (java.time.LocalDate.class.equals(targetType)) {
                        Date date = resultSet.getDate(1);
                        results.add(date != null ? date.toLocalDate() : null);
                    } else if (java.time.LocalDateTime.class.equals(targetType)) {
                        Timestamp timestamp = resultSet.getTimestamp(1);
                        results.add(timestamp != null ? timestamp.toLocalDateTime() : null);
                    } else {
                        results.add(resultSet.getDate(1));
                    }
                }
                return (List<T>) results;
            }
            return processNext(resultSet, targetType);
        }
    }
    
    /**
     * Processes Object[] results for multi-column selects.
     */
    public static class ArrayResultProcessor extends ResultProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException {
            if (Object[].class.equals(targetType)) {
                List<Object[]> results = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = resultSet.getObject(i);
                    }
                    results.add(row);
                }
                return (List<T>) results;
            }
            return processNext(resultSet, targetType);
        }
    }
    
    /**
     * Default processor for any remaining types.
     */
    public static class DefaultResultProcessor extends ResultProcessor {
        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> process(ResultSet resultSet, Class<T> targetType) throws SQLException {
            List<Object> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(resultSet.getObject(1));
            }
            return (List<T>) results;
        }
    }
    
    /**
     * Exception thrown when query execution fails.
     */
    public static class QueryExecutionException extends RuntimeException {
        public QueryExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}