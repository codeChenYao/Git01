package com.chenyao.note.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    //得到配置文件对象
    private static Properties properties = new Properties();

    static {
        //加载配置文件（输入流）
        InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
        //通过load方法将输入流中的内容加载到配置文件对象中
        try {
            properties.load(in);
            //通过配置文件对象的getProperty（）方法获取驱动名，并加载驱动
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取数据库连接
     */
    public static Connection getConnetion(){
        Connection connection = null;
        //得到数据库连接的相关参数
        String dbUrl = properties.getProperty("dbUrl");
        String dbName = properties.getProperty("dbName");
        String dbPwd = properties.getProperty("dbPwd");
        try {
            connection = DriverManager.getConnection(dbUrl, dbName, dbPwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    /**
     * 关闭资源
     */
    public static void close(ResultSet resultSet, PreparedStatement preparedStatement,Connection connection){
            try {
                if (resultSet!=null){
                    resultSet.close();
                }
                if (preparedStatement!=null){
                    preparedStatement.close();
                }
                if (connection!=null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }
}
