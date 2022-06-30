package client;

import jdk.internal.dynalink.linker.LinkerServices;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientConfig {
    private int login;
    private int role;
    private byte frequency;

    private Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;

    private String state;//状态

    private List<String> violateUsernameList = new ArrayList<>();

    private List<String> downUsernameList = new ArrayList<>();

    public List<String> getDownUsernameList() {
        return downUsernameList;
    }

    public void setDownUsernameList(List<String> downUsernameList) {
        this.downUsernameList = downUsernameList;
    }

    public List<String> getViolateUsernameList() {
        return violateUsernameList;
    }

    public String[] getViolateUsernames(){
        return (String[])violateUsernameList.toArray();
    }

    public String[] getDownUsernames(){
        return (String[])downUsernameList.toArray();
    }

    public void addViolateUsername(String violateUsername){
        if (!violateUsernameList.contains(violateUsername)){
            violateUsernameList.add(violateUsername);
        }
    }

    public void setViolateUsernameList(List<String> violateUsernameList) {
        this.violateUsernameList = violateUsernameList;
    }

    public boolean isIfBlackDetect() {
        return ifBlackDetect;
    }

    public void setIfBlackDetect(boolean ifBlackDetect) {
        this.ifBlackDetect = ifBlackDetect;
    }

    private boolean ifBlackDetect=false;//学生端是否违规

    private String[] BlackList={"notepad.exe", "winword.exe", "wps.exe", "wordpad.exe", "iexplore.exe", "chrome.exe", "qqbrowser.exe",

            "360chrome.exe", "360se.exe", "sogouexplorer.exe", "firefox.exe", "opera.exe", "maxthon.exe", "netscape.exe", "baidubrowser.exe",

            "2345Explorer.exe"};



    public void setBlackList(String[] blackList) {
        BlackList = blackList;
    }

    private static final int BLACKLIST_NUMBER=16;

    private String []imageIDsSearchList;

    private int secede =0;

    private boolean isLive =true;

//    private Map<String,Socket> client=new HashMap<String,Socket>();

    /**
     *老师端正在请求谁的图像，用户列表中选中了谁，或按IP，USERNAME,MAC进行选择
     */
    private String focusImageType;

    public String getFocusImageType() {
        return focusImageType;
    }

    public void setFocusImageType(String focusImageType) {
        this.focusImageType = focusImageType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public ClientConfig() {
        this.login = 0;
        this.role = 0;
        this.frequency = 2;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public byte getFrequency() {
        return frequency;
    }

    public void setFrequency(byte frequency) {
        this.frequency = frequency;
    }

    public String[] getBlackList(){
        return BlackList;
    }

    public static int getBlacklistNumber() {
        return BLACKLIST_NUMBER;
    }

    public String[] getImageIDsSearchList() {
        return imageIDsSearchList;
    }

    public void setImageIDsSearchList(String[] imageIDsSearchList) {
        this.imageIDsSearchList = imageIDsSearchList;
    }

    public int getSecede() {
        return secede;
    }
    public void setSecede(int secede) {
        this.secede = secede;
    }

    public void setLive(boolean live) {
        this.isLive = live;
    }

    public boolean getIsLive() {
        return isLive;
    }
    public void changeDos(){
        try {
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void changeSocket() throws IOException {
        socket.close();
    }
    public void changDis() throws IOException {
        dis.close();
    }

}
