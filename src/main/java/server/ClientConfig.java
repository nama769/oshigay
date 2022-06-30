package server;

import database.UserModel;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientConfig {

    /**
     * 截图频率，默认2s
     */
    private byte frequency;


    /**
     * 进程黑名单
     */
    private String[] appBlackList;

    private String blackListString;

    private String imageUuid;

    /**
     *所有学生列表
     */
    private List allClient;

    /**
     *下线用户列表
     */
    private List downClient = new ArrayList<>();

    /**
     *违规学生列表
     */
    private List violateClient = new ArrayList<>();

    public String getBlackListString() {
        return blackListString;
    }

    public void setBlackListString(String blackListString) {
        this.blackListString = blackListString;
    }

    public List getViolateClient() {
        return violateClient;
    }

    public void setViolateClient(List violateClient) {
        this.violateClient = violateClient;
    }

    private DataOutputStream dosTeacher;

    /**
     * 保存用户名对应的最新图片id
     */
    private HashMap<String,String> userImageMap;

    private String imageSavePath;

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }

    public List getAllClient() {
        return allClient;
    }

    public void setAllClient(List allClient) {
        this.allClient = allClient;
    }

    public List getDownClient() {
        return downClient;
    }

    public void setDownClient(List downClient) {
        this.downClient = downClient;
    }

    public DataOutputStream getDosTeacher() {
        return dosTeacher;
    }

    public void setDosTeacher(DataOutputStream dosTeacher) {
        this.dosTeacher = dosTeacher;
    }

    public HashMap<String, String> getUserImageMap() {
        return userImageMap;
    }

    public void setUserImageMap(HashMap<String, String> userImageMap) {
        this.userImageMap = userImageMap;
    }

    public void setUserNewImage(String username,String imageId){
        if(userImageMap.containsKey(username)){
            userImageMap.remove(username);
        }
        userImageMap.put(username,imageId);
    }

    public String getImageSavePath() {
        return imageSavePath;
    }

    public void setImageSavePath(String imageSavePath) {
        this.imageSavePath = imageSavePath;
    }

    /**
     *
     */

    public ClientConfig(String imageSavePath) {
        frequency= 8;
        setAppBlackList(new String[]{"test"});
        allClient = new ArrayList<>();
        downClient = new ArrayList<>();
        userImageMap = new HashMap<>();
        this.imageSavePath = imageSavePath;
    }

    public byte getFrequency() {
        return frequency;
    }

    public void setFrequency(byte frequency) {
        this.frequency = frequency;
    }

    public String[] getAppBlackList() {
        return appBlackList;
    }


    public void setAppBlackList(String[] appBlackList) {
        this.appBlackList = appBlackList;
        String bls="";
        for(String i :appBlackList){
            bls+=(i+" ");
        }
        this.blackListString =bls;
    }

    public void addClient(UserModel userModel){
        allClient.add(userModel);
    }
    public void addDownClient(UserModel userModel){
        downClient.add(userModel);
    }
}
