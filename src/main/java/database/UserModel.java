package database;

import java.sql.Timestamp;

/**
 * 用户表的模型，便于使用
 */
public class UserModel {
    private String ID;
    private String Username;
    private String Password;
    private String IP;
    private String Role;
    private String MAC;
    private String State;

    public UserModel(String ID, String username, String password, String IP, String role, String MAC, String state) {
        this.ID = ID;
        Username = username;
        Password = password;
        this.IP = IP;
        Role = role;
        this.MAC = MAC;
        State = state;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setRole(String role) {
        Role = role;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public void setState(String state) {
        State = state;
    }

    public String getID() {
        return ID;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getIP() {
        return IP;
    }

    public String getRole() {
        return Role;
    }

    public String getMAC() {
        return MAC;
    }

    public String getState() {
        return State;
    }
}
