package server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务端入口函数，开启监听，保存来自各个客户端的连接，将各个客户端放入HandleClient处理
 */
public class Server {
	public static Map<String,Socket> client=new HashMap<String,Socket>();

//	public static View view=new View();
	public static String curKey=null;
	public static boolean serverLive=true;
	public static void main(String[] args) {
		try {
//			System.out.println(System.currentTimeMillis());
			System.out.println(InetAddress.getLocalHost());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		ClientConfig clientConfig = new ClientConfig();
		try {
			ServerSocket serverSocket=new ServerSocket(33000);
//			view.create();
			while(serverLive){
				Socket socket=serverSocket.accept();
				new Thread(new HandleClient(socket,clientConfig)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
