package database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
    *调用InitDatabase的获取数据库连接的接口，进行特定查询或特定语句，并返回结果。
    * 特定查询如新建一个用户项、按ip查找图像表等等。
    * 所有的数据库查询操作应都放在这个类中。
 */
public class DatabaseTool {

    private InitDatabase initDatabase;

    /**
     * 构造函数，调用InitDatabase，为statement赋值
     */
    public DatabaseTool (){
        this.initDatabase = new InitDatabase();
    }

    /**
     *下面是使用Statement，执行特定的语句。可以任意添加。下面为一个添加用户的例子
     */

    /**
     * 添加一个用户
     * @return
     * @author nama
     */
    public boolean addUser(UserModel user){
        try{
            PreparedStatement preparedStatement = initDatabase.getpreparedStatement("insert into Users values(?,?,?,?,?,?,?)");
            preparedStatement.setString(1,user.getID());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getIP());
            preparedStatement.setInt(5,user.getRole());
            preparedStatement.setString(6,user.getMAC());
            preparedStatement.setString(7,user.getState());
            preparedStatement.execute();
        }catch (SQLException e){
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        return true;
    }


}
