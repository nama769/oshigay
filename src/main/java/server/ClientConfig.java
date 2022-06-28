package server;

public class ClientConfig {

    /**
     * 截图频率，默认2000ms
     */
    private int frequency;

    /**
     * 进程黑名单
     */
    private String appBlackList;



    /**
     *
     */

    public ClientConfig() {
        frequency= 2000;
        appBlackList = "QQ";
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getAppBlackList() {
        return appBlackList;
    }

    public void setAppBlackList(String appBlackList) {
        this.appBlackList = appBlackList;
    }
}
