package database;

import java.sql.*;

/**
    *
    * 连接sqlite数据库,初始化数据库的表
    * 用户表Users（ID,Username用户名，Password密码，IP,Role身份，MAC,State状态）
    * 图像表Images(ID（图片名）,Time时间，UserID用户)
    * 并把数据库连接封装在本类的属性中
 */

public class InitDatabase {


    /**
     * 数据库会话
     */
    private Connection connection;


    /**
     * 构造函数，进行数据库的连接，与数据库的初始化。
     * @author 老张
     */
    public InitDatabase(){
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("drop table if exists Users");
            statement.executeUpdate("drop table if exists Images");
            statement.executeUpdate("create table Users (ID string, Username string,Password string,IP string,Role integer,MAC string,State string)");
            statement.executeUpdate("create table Images (ID string, CreatTime integer,UserID string)");
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public PreparedStatement getpreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
