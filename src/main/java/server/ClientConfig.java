package server;

public class ClientConfig {

    /**
     * 截图频率，默认2s
     */
    private byte frequency;


    /**
     * 进程黑名单
     */
    private String appBlackList;

    private String imageUuid;



    /**
     *
     */

    public ClientConfig() {
        frequency= 2;
        appBlackList = "QQ";
    }

    public byte getFrequency() {
        return frequency;
    }

    public void setFrequency(byte frequency) {
        this.frequency = frequency;
    }

    public String getAppBlackList() {
        return appBlackList;
    }

    public void setAppBlackList(String appBlackList) {
        this.appBlackList = appBlackList;
    }
}
