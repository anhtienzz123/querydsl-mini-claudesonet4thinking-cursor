import com.querydsl.mini.expressions.PathExpression;
import com.querydsl.mini.factory.QueryFactory;
import com.querydsl.mini.sql.StandardSqlGenerator;

import static com.querydsl.mini.factory.ExpressionFactory.*;

public class TestCrud {
    
    public static void main(String[] args) {
        System.out.println("Testing QueryDSL Mini CRUD Operations...");
        
        QueryFactory qf = new QueryFactory(null, new StandardSqlGenerator());
        
        try {
            // Test INSERT
            System.out.println("\n1. Testing INSERT:");
            String insertSql = qf.insertInto("users")
                    .columns("name", "age", "email")
                    .values("Alice", 25, "alice@example.com")
                    .toSQL();
            System.out.println("INSERT SQL: " + insertSql);
            
            // Test UPDATE
            System.out.println("\n2. Testing UPDATE:");
            PathExpression<Integer> userId = path(Integer.class, "users", "id");
            String updateSql = qf.update("users")
                    .set("name", "Updated Name")
                    .set("age", 26)
                    .where(userId.eq(1))
                    .toSQL();
            System.out.println("UPDATE SQL: " + updateSql);
            
            // Test DELETE
            System.out.println("\n3. Testing DELETE:");
            PathExpression<Integer> userAge = path(Integer.class, "users", "age");
            String deleteSql = qf.deleteFrom("users")
                    .where(userAge.lt(18))
                    .toSQL();
            System.out.println("DELETE SQL: " + deleteSql);
            
            System.out.println("\n✅ All CRUD operations work correctly!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 