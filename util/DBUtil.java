package SHmarketplace.util;

import java.sql.*;

/**
 * JDBC 数据库工具类
 * 提供数据库连接的获取与资源释放
 */
public class DBUtil {

    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/shmarketplace?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf-8";
    private static final String USER = "root";
    private static final String PASSWORD = "060711";

    /**
     * 加载 JDBC 驱动（类加载时执行一次）
     */
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动加载失败，请检查依赖是否引入", e);
        }
    }

    /**
     * 私有构造函数，防止工具类被实例化
     */
    private DBUtil() {
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 数据库连接对象
     * @throws SQLException 如果连接失败
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 关闭连接
     *
     * @param conn 数据库连接
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭连接和语句
     *
     * @param conn 数据库连接
     * @param stmt SQL 语句
     */
    public static void close(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(conn);
    }

    /**
     * 关闭连接、语句和结果集
     *
     * @param conn 数据库连接
     * @param stmt SQL 语句
     * @param rs   查询结果集
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(conn, stmt);
    }
}