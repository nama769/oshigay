package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientConfig {
    private int login;
    private int role;
    private byte frequency;

    private Socket socket;

    private DataOutputStream dos;
    private DataInputStream dis;

    private String state;//状态

    private String[] BlackList={"notepad.exe", "winword.exe", "wps.exe", "wordpad.exe", "iexplore.exe", "chrome.exe", "qqbrowser.exe",

            "360chrome.exe", "360se.exe", "sogouexplorer.exe", "firefox.exe", "opera.exe", "maxthon.exe", "netscape.exe", "baidubrowser.exe",

            "2345Explorer.exe"};
    private static final int BLACKLIST_NUMBER=16;

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
}
