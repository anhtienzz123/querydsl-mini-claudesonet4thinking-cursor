# QueryDSL Mini

A mini implementation of a QueryDSL-like library built from scratch using Java core, demonstrating multiple design patterns and type-safe query building.

## Overview

QueryDSL Mini is a type-safe SQL query builder that allows you to construct SQL queries using fluent Java APIs instead of string concatenation. This implementation showcases numerous design patterns and serves as an educational example of how to build complex, well-architected Java libraries.

## Features

-   ✅ **Type-safe query building** - Compile-time checking of query structure
-   ✅ **Fluent API** - Intuitive, readable query construction
-   ✅ **Multiple SQL dialects** - Extensible SQL generation system
-   ✅ **Complex expressions** - Support for functions, case expressions, subqueries
-   ✅ **JDBC integration** - Ready-to-use database execution
-   ✅ **Comprehensive design patterns** - Educational implementation showcasing 9+ patterns

## Design Patterns Implemented

### 1. **Builder Pattern**

The core query building functionality uses the Builder pattern for fluent API construction:

```java
QueryFactory qf = queryDSL.getQueryFactory();
qf.select()
  .from("users")
  .where(userName.eq("John"))
  .orderBy(userAge)
  .limit(10)
  .fetch();
```

**Location**: `Query` interface, `AbstractQuery`, `SelectQuery`

### 2. **Factory Pattern**

Factory classes centralize object creation and provide convenient static methods:

```java
// Expression factory
PathExpression<String> userName = ExpressionFactory.path(String.class, "users", "name");
ConstantExpression<String> value = ExpressionFactory.constant("John");
FunctionExpression<Long> count = ExpressionFactory.countAll();

// Query factory
QueryFactory qf = new QueryFactory(executor, sqlGenerator);
SelectQuery<String> query = qf.selectString();
```

**Location**: `ExpressionFactory`, `QueryFactory`

### 3. **Visitor Pattern**

Expression tree traversal and SQL generation use the Visitor pattern:

```java
public interface ExpressionVisitor<R, C> {
    R visit(PathExpression<?> expr, C context);
    R visit(ConstantExpression<?> expr, C context);
    R visit(BinaryOperationExpression<?> expr, C context);
    // ... other visit methods
}
```

**Location**: `ExpressionVisitor`, `StandardSqlGenerator`

### 4. **Strategy Pattern**

Different SQL dialects and query execution strategies:

```java
public interface SqlGenerator {
    String generate(AbstractQuery<?, ?> query);
    SqlDialect getDialect();
}

public interface QueryExecutor {
    <T> List<T> executeQuery(AbstractQuery<T, ?> query);
    long executeCount(AbstractQuery<?, ?> query);
}
```

**Location**: `SqlGenerator`, `StandardSqlGenerator`, `QueryExecutor`, `JdbcQueryExecutor`

### 5. **Template Method Pattern**

Abstract base classes define algorithm structure with customizable steps:

```java
public abstract class AbstractExpression<T> implements Expression<T> {
    // Template method for equality checking
    protected boolean equalsInternal(AbstractExpression<?> other) { ... }

    // Template method for hash code calculation
    protected int hashCodeInternal() { ... }
}
```

**Location**: `AbstractExpression`, `AbstractQuery`

### 6. **Composite Pattern**

Complex expressions are built by combining simpler expressions:

```java
Expression<Boolean> complexCondition = userAge.ge(18)
    .and(userName.like("%John%"))
    .or(userEmail.isNotNull());
```

**Location**: All expression classes, `BinaryOperationExpression`

### 7. **Chain of Responsibility Pattern**

Result processing uses a chain of specialized processors:

```java
// Chain: String -> Number -> Date -> Array -> Default
ResultProcessor chain = new StringResultProcessor();
chain.setNext(new NumberResultProcessor());
// ... continue chaining
```

**Location**: `JdbcQueryExecutor.ResultProcessor` hierarchy

### 8. **Observer Pattern**

While not fully implemented in this mini version, the architecture supports query execution listeners and event handling through the executor framework.

**Location**: Framework structure in `QueryExecutor` interface

### 9. **Adapter Pattern**

The library adapts different data sources and SQL dialects:

```java
public class JdbcQueryExecutor implements QueryExecutor {
    // Adapts JDBC DataSource to QueryExecutor interface
}
```

**Location**: `JdbcQueryExecutor`, SQL dialect implementations

### 10. **Facade Pattern**

The main `QueryDSL` class provides a simplified interface to the complex subsystem:

```java
QueryDSL queryDSL = QueryDSL.withDataSource(dataSource);
QueryFactory qf = queryDSL.getQueryFactory();
ExpressionFactory ef = queryDSL.getExpressionFactory();
```

**Location**: `QueryDSL` main class

## Quick Start

### 1. Add Dependencies

```xml
<dependency>
    <groupId>com.querydsl.mini</groupId>
    <artifactId>querydsl-mini</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Basic Usage

```java
import static com.querydsl.mini.factory.ExpressionFactory.*;

// Create QueryDSL instance with DataSource
QueryDSL queryDSL = QueryDSL.withDataSource(dataSource);
QueryFactory qf = queryDSL.getQueryFactory();

// Define column references
PathExpression<String> userName = path(String.class, "users", "name");
PathExpression<Integer> userAge = path(Integer.class, "users", "age");
PathExpression<String> userEmail = path(String.class, "users", "email");

// Build and execute query
List<String> names = qf.selectString()
    .select(userName)
    .from("users")
    .where(userAge.gt(25))
    .orderBy(userName)
    .fetch();
```

## Advanced Examples

### Complex WHERE Conditions

```java
// Complex logical expressions
Expression<Boolean> condition = userAge.ge(18)
    .and(userName.like("%John%")
         .or(userEmail.isNotNull()));

List<Object[]> results = qf.select()
    .from("users")
    .where(condition)
    .fetch();
```

### Aggregate Queries

```java
// Using aggregate functions
List<Object[]> aggregates = qf.select()
    .select(count(userName), avg(userAge), min(userAge), max(userAge))
    .from("users")
    .where(userAge.gt(0))
    .fetch();
```

### JOIN Queries

```java
PathExpression<Integer> userId = path(Integer.class, "users", "id");
PathExpression<Integer> orderUserId = path(Integer.class, "orders", "user_id");
PathExpression<Double> orderAmount = path(Double.class, "orders", "amount");

List<Object[]> joinResults = qf.select()
    .select(userName, sum(orderAmount))
    .from("users", "u")
    .leftJoin("orders", "o", userId.eq(orderUserId))
    .groupBy(userId, userName)
    .having(sum(orderAmount).gt(100.0))
    .orderBy(sum(orderAmount), Query.OrderDirection.DESC)
    .fetch();
```

### CASE Expressions

```java
CaseExpression<String> ageCategory = caseExpression(String.class)
    .when(userAge.lt(18), constant("Minor"))
    .when(userAge.lt(65), constant("Adult"))
    .otherwise(constant("Senior"));

List<String> categories = qf.selectString()
    .select(ageCategory)
    .from("users")
    .fetch();
```

### Subqueries

```java
SelectQuery<Double> avgAgeQuery = qf.selectDouble()
    .select(avg(userAge))
    .from("users");

SubQueryExpression<Double> avgAge = subQuery(Double.class, avgAgeQuery);

List<String> aboveAverage = qf.selectString()
    .select(userName)
    .from("users")
    .where(userAge.gt(avgAge))
    .fetch();
```

## Architecture

### Core Components

```
com.querydsl.mini/
├── core/                    # Core interfaces and base classes
│   ├── Expression.java      # Base expression interface
│   ├── ExpressionVisitor.java # Visitor pattern interface
│   └── Operator.java        # SQL operators enumeration
├── expressions/             # Expression implementations
│   ├── AbstractExpression.java     # Template method base class
│   ├── PathExpression.java         # Table.column references
│   ├── ConstantExpression.java     # Literal values
│   ├── BinaryOperationExpression.java # Operations between expressions
│   ├── FunctionExpression.java     # Function calls (COUNT, SUM, etc.)
│   ├── CaseExpression.java         # CASE WHEN expressions
│   └── SubQueryExpression.java     # Subquery expressions
├── query/                   # Query building framework
│   ├── Query.java          # Main query interface (Builder pattern)
│   ├── AbstractQuery.java  # Template method implementation
│   └── SelectQuery.java    # Concrete SELECT query implementation
├── sql/                     # SQL generation (Strategy pattern)
│   ├── SqlGenerator.java   # Strategy interface
│   └── StandardSqlGenerator.java # Standard SQL implementation
├── execution/               # Query execution (Strategy + Chain of Responsibility)
│   ├── QueryExecutor.java  # Execution strategy interface
│   └── JdbcQueryExecutor.java # JDBC implementation with result processing chain
├── factory/                 # Factory pattern implementations
│   ├── ExpressionFactory.java # Expression creation factory
│   └── QueryFactory.java      # Query creation factory
└── QueryDSL.java           # Main facade class
```

### Extension Points

The library is designed for extensibility:

1. **Custom SQL Dialects**: Implement `SqlGenerator` interface
2. **Custom Executors**: Implement `QueryExecutor` interface
3. **Custom Expressions**: Extend `AbstractExpression`
4. **Custom Functions**: Use `FunctionExpression` with custom names
5. **Custom Result Processors**: Extend `ResultProcessor` in the chain

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

The test suite demonstrates all design patterns and provides usage examples.

## Requirements

-   Java 21+
-   Maven 3.6+
-   JDBC-compatible database (H2 included for testing)

## Key Design Decisions

### Type Safety

All expressions are parameterized with their result types, providing compile-time type checking:

```java
PathExpression<String> name = path(String.class, "users", "name");
PathExpression<Integer> age = path(Integer.class, "users", "age");

// This would be a compile-time error:
// age.like("%pattern%"); // LIKE only works with String types
```

### Immutability

Query objects are immutable; each fluent method returns a new instance:

```java
SelectQuery<String> base = qf.selectString().from("users");
SelectQuery<String> filtered = base.where(age.gt(25)); // New instance
SelectQuery<String> ordered = filtered.orderBy(name);   // Another new instance
```

### Extensible Architecture

The use of interfaces and patterns makes the library highly extensible without modifying existing code.

## Learning Objectives

This implementation demonstrates:

-   How to design fluent APIs using the Builder pattern
-   Effective use of the Visitor pattern for tree traversal
-   Strategy pattern for algorithm selection
-   Factory pattern for object creation
-   Template method for common functionality
-   Composite pattern for recursive structures
-   Chain of responsibility for processing pipelines
-   Facade pattern for simplified interfaces

## Contributing

This is an educational project demonstrating design patterns. To contribute:

1. Fork the repository
2. Create a feature branch
3. Ensure all tests pass
4. Add documentation for new patterns or features
5. Submit a pull request

## License

MIT License - See LICENSE file for details.
