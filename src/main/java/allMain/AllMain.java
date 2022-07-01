package allMain;

import client.Main;
import server.Server;

public class AllMain {
    public static void main(String[] args) {
        if(args.length>0){
            if(args[0].equals("server")){
                Server.serverMain(new String[]{args[1]});
            }else {
                Main.clientMain(new String[]{args[1]});
            }
        }else {
            Main.clientMain(new String[]{"123.57.193.197"});
        }
    }
}
