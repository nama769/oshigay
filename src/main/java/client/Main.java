package client;

/**
 * 客户端主函数入口，与服务端创建连接，调用View创建JFrame。
 */
public class Main {
    public static void clientMain(String[] args) {
        ClientConfig clientconfig = new ClientConfig();
        Client client = new Client(clientconfig);
//        client.showSystemTray();// 显示托盘
        String serverIP ="192.168.247.1";
//        serverIP ="123.57.193.197";
        if(args.length==1){
            serverIP = args[0];
        }
        clientconfig.setServerIp(serverIP);
        clientconfig.setPort(33000);
        client.conn(serverIP,clientconfig.getPort());

        //        client.load();// 登录
        /**
         * 待实现，创建JFrame,进入登录注册，然后根据
         */
        View view = new View(clientconfig);
        view.create();

//      client.showSystemTray();

        client.handleResult();
//        while (client.isLive) {
////            client.sendImage(client.getScreenShot());
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException ev) {
//
//            }
//        }
    }

}
