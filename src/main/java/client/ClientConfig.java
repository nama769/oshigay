package client;

public class ClientConfig {
    private int login;
    private int role;

    public ClientConfig() {
        this.login = 0;
        this.role = 0;
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
}
