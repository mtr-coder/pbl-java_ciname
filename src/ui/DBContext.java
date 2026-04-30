package ui;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
    public Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=CinemaJava;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        return DriverManager.getConnection(url);
    }
    public static void main(String[] args) {
        try {
            DBContext db = new DBContext();
            if (db.getConnection() != null) {
                System.out.println("Ket noi thanh cong");
            }
        } catch (Exception e) {
            System.out.println("Loi ket noi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//java -cp ".;lib/mssql-jdbc-13.4.0.jre11.jar" ui/page_login.java
