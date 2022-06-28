package database;

import java.sql.Timestamp;

/**
 * 图像表的模型，便于使用
 */
public class ImageModel {
    private String ID;
    private long CreatTime;
    private String UserID;

    /**
     * 构造函数
     * @param ID
     * @param CreatTime
     * @param UserID
     */
    public ImageModel(String ID,long CreatTime,String UserID){
        this.ID=ID;
        this.CreatTime=CreatTime;
        this.UserID=UserID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setCreatTime(long creatTime) {
        CreatTime = creatTime;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getID() {
        return ID;
    }

    public long getCreatTime() {
        return CreatTime;
    }

    public String getUserID() {
        return UserID;
    }
}
