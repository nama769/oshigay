package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
    *
    * 连接sqlite数据库,初始化数据库的表
    *用户表Users（ID,Username用户名，Password密码，IP,Role身份，MAC,State状态）
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
     */
    public InitDatabase(){

    }

    /**
     *
     * @return
     */
    public PreparedStatement getpreparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }






}
